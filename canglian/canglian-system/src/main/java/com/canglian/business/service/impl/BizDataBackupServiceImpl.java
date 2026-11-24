package com.canglian.business.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.BizDataBackupInfo;
import com.canglian.business.service.IBizDataBackupService;
import com.canglian.common.config.CanglianConfig;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 数据库备份服务实现（纯 JDBC 逻辑 SQL 快照导出、SHA-256 校验、过期清理与受控恢复）
 *
 * @author canglian
 */
@Service
public class BizDataBackupServiceImpl implements IBizDataBackupService
{
    private static final Logger log = LoggerFactory.getLogger(BizDataBackupServiceImpl.class);

    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("^[A-Za-z0-9_.-]+\\.sql(\\.gz)?$");

    private static final String BACKUP_HEADER_SIGNATURE = "-- Canglian ERP SQL Backup";

    private static final int BATCH_INSERT_SIZE = 100;

    private static final int MIN_RETAIN_FILES = 3;

    @Autowired
    private DataSource dataSource;

    @Override
    public List<BizDataBackupInfo> selectBackupList()
    {
        Path backupDir = resolveBackupDir();
        List<BizDataBackupInfo> list = new ArrayList<BizDataBackupInfo>();
        if (!Files.exists(backupDir))
        {
            return list;
        }
        File[] files = backupDir.toFile().listFiles((dir, name) -> SAFE_FILENAME_PATTERN.matcher(name).matches());
        if (files == null)
        {
            return list;
        }
        for (File file : files)
        {
            if (file.isFile())
            {
                BizDataBackupInfo info = new BizDataBackupInfo();
                info.setFileName(file.getName());
                info.setFileSize(file.length());
                info.setFileSizeText(formatFileSize(file.length()));
                info.setCreateTime(new Date(file.lastModified()));
                info.setCompressed(file.getName().endsWith(".gz"));
                String recordedHash = readRecordedChecksum(file.toPath());
                if (StringUtils.isNotEmpty(recordedHash))
                {
                    info.setSha256(recordedHash);
                    info.setChecksumStatus("recorded");
                    info.setVerified(Boolean.TRUE);
                }
                else
                {
                    info.setChecksumStatus("missing");
                    info.setVerified(Boolean.FALSE);
                }
                list.add(info);
            }
        }
        list.sort(Comparator.comparing(BizDataBackupInfo::getCreateTime, Comparator.nullsLast(Comparator.reverseOrder())));
        return list;
    }

    @Override
    public synchronized BizDataBackupInfo createBackup(String operator)
    {
        return createBackupInternal("canglian_backup_", operator, false);
    }

    @Override
    public synchronized BizDataBackupInfo createBackup(String operator, boolean compress)
    {
        return createBackupInternal("canglian_backup_", operator, compress);
    }

    private BizDataBackupInfo createBackupInternal(String prefix, String operator, boolean compress)
    {
        Path backupDir = resolveBackupDir();
        try
        {
            Files.createDirectories(backupDir);
        }
        catch (IOException e)
        {
            throw new ServiceException("创建备份目录失败：" + e.getMessage());
        }
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = prefix + timestamp + (compress ? ".sql.gz" : ".sql");
        Path targetPath = backupDir.resolve(fileName).normalize();
        if (!targetPath.startsWith(backupDir))
        {
            throw new ServiceException("非法备份文件路径");
        }
        int seq = 1;
        while (Files.exists(targetPath))
        {
            fileName = prefix + timestamp + "_" + seq + (compress ? ".sql.gz" : ".sql");
            targetPath = backupDir.resolve(fileName).normalize();
            seq++;
        }

        int tableCount = 0;
        long totalRows = 0L;
        try (Connection conn = dataSource.getConnection();
             OutputStream rawOut = new FileOutputStream(targetPath.toFile());
             OutputStream finalOut = compress ? new GZIPOutputStream(rawOut) : rawOut;
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(finalOut, StandardCharsets.UTF_8)))
        {
            String databaseName = conn.getCatalog();
            writer.write(BACKUP_HEADER_SIGNATURE + "\n");
            writer.write("-- Database: " + (databaseName == null ? "" : databaseName) + "\n");
            writer.write("-- Operator: " + (operator == null ? "system" : operator) + "\n");
            writer.write("-- Generated At: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n\n");
            writer.write("SET NAMES utf8mb4;\n");
            writer.write("SET FOREIGN_KEY_CHECKS = 0;\n\n");

            List<String> tables = listBaseTables(conn);
            boolean snapshotStarted = false;
            boolean origAutoCommit = conn.getAutoCommit();
            try
            {
                try (Statement snapSt = conn.createStatement())
                {
                    snapSt.execute("SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ");
                }
                catch (SQLException ignored)
                {
                    // 非 MySQL 或受限环境降级继续
                }
                conn.setAutoCommit(false);
                try (Statement snapSt = conn.createStatement())
                {
                    snapSt.execute("START TRANSACTION WITH CONSISTENT SNAPSHOT");
                    snapshotStarted = true;
                }
                catch (SQLException ignored)
                {
                    // 兼容不支持 CONSISTENT SNAPSHOT 的驱动，保持 REPEATABLE READ 事务
                }

                for (String table : tables)
                {
                    tableCount++;
                    writeTableDdl(conn, table, writer);
                    totalRows += writeTableData(conn, table, writer);
                }
                if (snapshotStarted || !conn.getAutoCommit())
                {
                    conn.commit();
                }
            }
            catch (Exception snapEx)
            {
                try
                {
                    if (!conn.getAutoCommit())
                    {
                        conn.rollback();
                    }
                }
                catch (SQLException ignored)
                {
                }
                throw snapEx;
            }
            finally
            {
                try
                {
                    conn.setAutoCommit(origAutoCommit);
                }
                catch (SQLException ignored)
                {
                }
            }

            writer.write("SET FOREIGN_KEY_CHECKS = 1;\n");
            writer.flush();
        }
        catch (Exception e)
        {
            try
            {
                Files.deleteIfExists(targetPath);
            }
            catch (IOException ignored)
            {
                // ignore cleanup failure
            }
            log.error("执行数据库备份失败", e);
            throw new ServiceException("执行数据库备份失败：" + e.getMessage());
        }

        String sha256;
        try
        {
            sha256 = computeSha256(targetPath);
            writeChecksumSidecar(targetPath, fileName, sha256);
        }
        catch (Exception e)
        {
            log.warn("写入备份文件 SHA-256 校验摘要失败：{}", fileName, e);
            sha256 = null;
        }

        File file = targetPath.toFile();
        BizDataBackupInfo info = new BizDataBackupInfo();
        info.setFileName(fileName);
        info.setFileSize(file.length());
        info.setFileSizeText(formatFileSize(file.length()));
        info.setCreateTime(new Date(file.lastModified()));
        info.setTableCount(tableCount);
        info.setRowCount(totalRows);
        info.setCompressed(compress);
        info.setSha256(sha256);
        info.setChecksumStatus(StringUtils.isNotEmpty(sha256) ? "valid" : "missing");
        info.setVerified(StringUtils.isNotEmpty(sha256));
        return info;
    }

    @Override
    public File getBackupFile(String fileName)
    {
        Path targetPath = resolveSafeBackupFile(fileName);
        File file = targetPath.toFile();
        if (!file.exists() || !file.isFile())
        {
            throw new ServiceException("备份文件不存在：" + fileName);
        }
        return file;
    }

    @Override
    public Map<String, Object> verifyBackup(String fileName)
    {
        Path targetPath = resolveSafeBackupFile(fileName);
        File file = targetPath.toFile();
        if (!file.exists() || !file.isFile())
        {
            throw new ServiceException("备份文件不存在：" + fileName);
        }
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("fileName", fileName);
        result.put("fileSize", file.length());
        result.put("compressed", fileName.endsWith(".gz"));
        try
        {
            String actualHash = computeSha256(targetPath);
            String recordedHash = readRecordedChecksum(targetPath);
            boolean headerValid = verifyHeaderSignature(targetPath);
            result.put("actualSha256", actualHash);
            result.put("recordedSha256", recordedHash);
            result.put("headerValid", headerValid);

            if (!headerValid)
            {
                result.put("checksumStatus", "tampered");
                result.put("verified", false);
                result.put("message", "文件头签名无效，不是合法的仓链 SQL 备份文件或已损坏");
                return result;
            }
            if (StringUtils.isEmpty(recordedHash))
            {
                result.put("checksumStatus", "missing");
                result.put("verified", true);
                result.put("message", "文件头校验通过，但缺少 .sha256 伴生校验文件（旧版快照）");
                return result;
            }
            if (recordedHash.equalsIgnoreCase(actualHash))
            {
                result.put("checksumStatus", "valid");
                result.put("verified", true);
                result.put("message", "SHA-256 完整性校验与文件头签名全部通过");
            }
            else
            {
                result.put("checksumStatus", "tampered");
                result.put("verified", false);
                result.put("message", "SHA-256 校验不匹配，备份文件可能已被篡改或截断");
            }
            return result;
        }
        catch (Exception e)
        {
            log.error("校验备份文件失败：{}", fileName, e);
            throw new ServiceException("校验备份文件失败：" + e.getMessage());
        }
    }

    @Override
    public synchronized int cleanExpiredBackups(int retainDays)
    {
        int effectiveDays = retainDays < 1 ? 30 : retainDays;
        List<BizDataBackupInfo> list = selectBackupList();
        if (list.size() <= MIN_RETAIN_FILES)
        {
            return 0;
        }
        long cutoffMillis = System.currentTimeMillis() - effectiveDays * 24L * 3600L * 1000L;
        int deletedCount = 0;
        for (int i = MIN_RETAIN_FILES; i < list.size(); i++)
        {
            BizDataBackupInfo info = list.get(i);
            if (info.getCreateTime() != null && info.getCreateTime().getTime() < cutoffMillis)
            {
                try
                {
                    deleteBackup(info.getFileName());
                    deletedCount++;
                }
                catch (Exception e)
                {
                    log.warn("自动清理过期备份失败：{}", info.getFileName(), e);
                }
            }
        }
        return deletedCount;
    }

    @Override
    public synchronized Map<String, Object> restoreBackup(String fileName, String confirmPhrase, String operator)
    {
        if (StringUtils.isEmpty(fileName))
        {
            throw new ServiceException("备份文件名不能为空");
        }
        String expectedPhrase = "RESTORE:" + fileName;
        if (!expectedPhrase.equals(confirmPhrase))
        {
            throw new ServiceException("二次确认口令错误，请输入：" + expectedPhrase);
        }
        Map<String, Object> verifyResult = verifyBackup(fileName);
        if (!Boolean.TRUE.equals(verifyResult.get("headerValid")) || "tampered".equals(verifyResult.get("checksumStatus")))
        {
            throw new ServiceException("备份文件校验未通过，已拒绝恢复：" + verifyResult.get("message"));
        }

        // 恢复前强制创建兜底快照，防止误操作不可回滚
        BizDataBackupInfo preSnapshot = createBackupInternal("pre_restore_", operator, false);
        Path targetPath = resolveSafeBackupFile(fileName);
        int executedStatements = 0;
        try (Connection conn = dataSource.getConnection();
             InputStream rawIn = new FileInputStream(targetPath.toFile());
             InputStream finalIn = fileName.endsWith(".gz") ? new GZIPInputStream(rawIn) : rawIn;
             BufferedReader reader = new BufferedReader(new InputStreamReader(finalIn, StandardCharsets.UTF_8));
             Statement stmt = conn.createStatement())
        {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            List<String> statements = parseSqlStatements(reader);
            for (String sql : statements)
            {
                stmt.execute(sql);
                executedStatements++;
            }
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        catch (Exception e)
        {
            log.error("执行受控数据库恢复失败：{}", fileName, e);
            throw new ServiceException("数据库恢复失败（已自动保留恢复前快照 " + preSnapshot.getFileName() + "）：" + e.getMessage());
        }

        Map<String, Object> summary = new LinkedHashMap<String, Object>();
        summary.put("restoredFile", fileName);
        summary.put("preRestoreBackupFile", preSnapshot.getFileName());
        summary.put("executedStatements", executedStatements);
        summary.put("operator", operator);
        summary.put("restoreTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return summary;
    }

    @Override
    public int deleteBackup(String fileName)
    {
        Path targetPath = resolveSafeBackupFile(fileName);
        try
        {
            if (!Files.exists(targetPath))
            {
                throw new ServiceException("备份文件不存在：" + fileName);
            }
            Files.delete(targetPath);
            Path sidecarPath = resolveChecksumSidecarPath(targetPath);
            Files.deleteIfExists(sidecarPath);
            return 1;
        }
        catch (IOException e)
        {
            throw new ServiceException("删除备份文件失败：" + e.getMessage());
        }
    }

    private Path resolveBackupDir()
    {
        String profile = CanglianConfig.getProfile();
        if (StringUtils.isEmpty(profile))
        {
            profile = System.getProperty("java.io.tmpdir");
        }
        return Paths.get(profile, "backup").toAbsolutePath().normalize();
    }

    private Path resolveSafeBackupFile(String fileName)
    {
        if (StringUtils.isEmpty(fileName) || !SAFE_FILENAME_PATTERN.matcher(fileName).matches()
            || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\"))
        {
            throw new ServiceException("非法的备份文件名");
        }
        Path backupDir = resolveBackupDir();
        Path targetPath = backupDir.resolve(fileName).normalize();
        if (!targetPath.startsWith(backupDir))
        {
            throw new ServiceException("非法访问备份路径");
        }
        return targetPath;
    }

    private Path resolveChecksumSidecarPath(Path backupPath)
    {
        return backupPath.resolveSibling(backupPath.getFileName().toString() + ".sha256");
    }

    private String computeSha256(Path filePath) throws Exception
    {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream in = Files.newInputStream(filePath))
        {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1)
            {
                digest.update(buffer, 0, len);
            }
        }
        byte[] hashBytes = digest.digest();
        StringBuilder hex = new StringBuilder(hashBytes.length * 2);
        for (byte b : hashBytes)
        {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    private void writeChecksumSidecar(Path backupPath, String fileName, String sha256) throws IOException
    {
        Path sidecar = resolveChecksumSidecarPath(backupPath);
        String content = sha256 + "  " + fileName + "\n";
        Files.write(sidecar, content.getBytes(StandardCharsets.UTF_8));
    }

    private String readRecordedChecksum(Path backupPath)
    {
        Path sidecar = resolveChecksumSidecarPath(backupPath);
        if (!Files.exists(sidecar) || !Files.isRegularFile(sidecar))
        {
            return null;
        }
        try
        {
            String content = new String(Files.readAllBytes(sidecar), StandardCharsets.UTF_8).trim();
            if (StringUtils.isEmpty(content))
            {
                return null;
            }
            int spaceIdx = content.indexOf(' ');
            return spaceIdx > 0 ? content.substring(0, spaceIdx).trim() : content;
        }
        catch (IOException e)
        {
            return null;
        }
    }

    private boolean verifyHeaderSignature(Path backupPath) throws IOException
    {
        boolean compressed = backupPath.getFileName().toString().endsWith(".gz");
        try (InputStream rawIn = Files.newInputStream(backupPath);
             InputStream finalIn = compressed ? new GZIPInputStream(rawIn) : rawIn;
             BufferedReader reader = new BufferedReader(new InputStreamReader(finalIn, StandardCharsets.UTF_8)))
        {
            String firstLine = reader.readLine();
            return firstLine != null && firstLine.startsWith(BACKUP_HEADER_SIGNATURE);
        }
    }

    private List<String> parseSqlStatements(BufferedReader reader) throws IOException
    {
        List<String> statements = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        boolean inSingleQuote = false;
        boolean escaped = false;
        String line;
        while ((line = reader.readLine()) != null)
        {
            if (!inSingleQuote)
            {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--"))
                {
                    continue;
                }
            }
            for (int i = 0; i < line.length(); i++)
            {
                char c = line.charAt(i);
                if (escaped)
                {
                    current.append(c);
                    escaped = false;
                    continue;
                }
                if (c == '\\' && inSingleQuote)
                {
                    current.append(c);
                    escaped = true;
                    continue;
                }
                if (c == '\'')
                {
                    inSingleQuote = !inSingleQuote;
                    current.append(c);
                    continue;
                }
                if (c == ';' && !inSingleQuote)
                {
                    String sql = current.toString().trim();
                    if (!sql.isEmpty())
                    {
                        statements.add(sql);
                    }
                    current.setLength(0);
                    continue;
                }
                current.append(c);
            }
            if (current.length() > 0)
            {
                current.append('\n');
            }
        }
        String tail = current.toString().trim();
        if (!tail.isEmpty())
        {
            statements.add(tail);
        }
        return statements;
    }

    private List<String> listBaseTables(Connection conn) throws SQLException
    {
        List<String> tables = new ArrayList<String>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SHOW FULL TABLES WHERE Table_type = 'BASE TABLE'"))
        {
            while (rs.next())
            {
                tables.add(rs.getString(1));
            }
        }
        return tables;
    }

    private void writeTableDdl(Connection conn, String table, BufferedWriter writer) throws SQLException, IOException
    {
        writer.write("-- ----------------------------\n");
        writer.write("-- Table structure for `" + table + "`\n");
        writer.write("-- ----------------------------\n");
        writer.write("DROP TABLE IF EXISTS `" + table + "`;\n");
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SHOW CREATE TABLE `" + table + "`"))
        {
            if (rs.next())
            {
                writer.write(rs.getString(2));
                writer.write(";\n\n");
            }
        }
    }

    private long writeTableData(Connection conn, String table, BufferedWriter writer) throws SQLException, IOException
    {
        long rows = 0L;
        try (Statement st = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY))
        {
            try
            {
                // MySQL Connector/J 流式游标读取，避免大表备份一次性加载进堆内存导致 OOM
                st.setFetchSize(Integer.MIN_VALUE);
            }
            catch (SQLException ignored)
            {
                // 兼容 H2 等测试驱动
            }
            try (ResultSet rs = st.executeQuery("SELECT * FROM `" + table + "`"))
            {
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            StringBuilder columnHeader = new StringBuilder();
            columnHeader.append("INSERT INTO `").append(table).append("` (");
            for (int i = 1; i <= colCount; i++)
            {
                if (i > 1)
                {
                    columnHeader.append(", ");
                }
                columnHeader.append('`').append(meta.getColumnName(i)).append('`');
            }
            columnHeader.append(") VALUES\n");

            int batchCount = 0;
            StringBuilder batch = new StringBuilder();
            while (rs.next())
            {
                if (batchCount == 0)
                {
                    batch.setLength(0);
                    batch.append(columnHeader);
                }
                else
                {
                    batch.append(",\n");
                }
                batch.append("(");
                for (int i = 1; i <= colCount; i++)
                {
                    if (i > 1)
                    {
                        batch.append(", ");
                    }
                    batch.append(formatSqlValue(rs.getObject(i)));
                }
                batch.append(")");
                rows++;
                batchCount++;
                if (batchCount >= BATCH_INSERT_SIZE)
                {
                    batch.append(";\n");
                    writer.write(batch.toString());
                    batchCount = 0;
                }
            }
            if (batchCount > 0)
            {
                batch.append(";\n");
                writer.write(batch.toString());
            }
            if (rows > 0)
            {
                writer.write("\n");
            }
            }
        }
        return rows;
    }

    private String formatSqlValue(Object val)
    {
        if (val == null)
        {
            return "NULL";
        }
        if (val instanceof Boolean)
        {
            return ((Boolean) val) ? "1" : "0";
        }
        if (val instanceof Number)
        {
            return val.toString();
        }
        if (val instanceof byte[])
        {
            byte[] bytes = (byte[]) val;
            if (bytes.length == 0)
            {
                return "''";
            }
            StringBuilder hex = new StringBuilder("0x");
            for (byte b : bytes)
            {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        }
        String str = val.toString();
        StringBuilder sb = new StringBuilder(str.length() + 2);
        sb.append('\'');
        for (int i = 0; i < str.length(); i++)
        {
            char c = str.charAt(i);
            switch (c)
            {
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\'':
                    sb.append("\\'");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\0':
                    sb.append("\\0");
                    break;
                default:
                    sb.append(c);
            }
        }
        sb.append('\'');
        return sb.toString();
    }

    private String formatFileSize(long size)
    {
        if (size < 1024L)
        {
            return size + " B";
        }
        if (size < 1024L * 1024L)
        {
            return String.format("%.2f KB", size / 1024.0);
        }
        return String.format("%.2f MB", size / (1024.0 * 1024.0));
    }
}
