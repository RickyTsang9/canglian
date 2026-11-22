package com.canglian.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import com.canglian.business.service.impl.BizDataBackupServiceImpl;
import com.canglian.common.config.CanglianConfig;
import com.canglian.common.exception.ServiceException;

/**
 * 数据备份完整性校验、路径穿越防护与受控恢复防呆测试 (P4-2)
 */
class BizDataBackupSecurityTest
{
    @TempDir
    Path tempProfileDir;

    private String originalProfile;

    private BizDataBackupServiceImpl backupService;

    @BeforeEach
    void setUp()
    {
        originalProfile = CanglianConfig.getProfile();
        new CanglianConfig().setProfile(tempProfileDir.toString());
        backupService = new BizDataBackupServiceImpl();
    }

    @AfterEach
    void tearDown()
    {
        new CanglianConfig().setProfile(originalProfile);
    }

    @Test
    void shouldRejectPathTraversalFileNames()
    {
        assertThrows(ServiceException.class, () -> backupService.getBackupFile("../secret.sql"));
        assertThrows(ServiceException.class, () -> backupService.verifyBackup("..\\windows.sql"));
        assertThrows(ServiceException.class, () -> backupService.deleteBackup("evil.exe"));
    }

    @Test
    void shouldVerifyValidBackupAndDetectSingleByteTampering() throws Exception
    {
        Path backupDir = tempProfileDir.resolve("backup");
        Files.createDirectories(backupDir);
        Path sqlFile = backupDir.resolve("canglian_backup_20261122_010000.sql");
        byte[] content = "-- Canglian ERP SQL Backup\nSET NAMES utf8mb4;\n".getBytes(StandardCharsets.UTF_8);
        Files.write(sqlFile, content);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(content);
        StringBuilder hex = new StringBuilder();
        for (byte b : digest)
        {
            hex.append(String.format("%02x", b));
        }
        Files.write(backupDir.resolve("canglian_backup_20261122_010000.sql.sha256"),
            (hex.toString() + "  canglian_backup_20261122_010000.sql\n").getBytes(StandardCharsets.UTF_8));

        Map<String, Object> validRes = backupService.verifyBackup("canglian_backup_20261122_010000.sql");
        assertTrue(Boolean.TRUE.equals(validRes.get("verified")));
        assertEquals("valid", validRes.get("checksumStatus"));

        // 篡改 1 字节
        Files.write(sqlFile, "-- Canglian ERP SQL Backup\nSET NAMES utf8mb4; -- tampered\n".getBytes(StandardCharsets.UTF_8));
        Map<String, Object> tamperedRes = backupService.verifyBackup("canglian_backup_20261122_010000.sql");
        assertFalse(Boolean.TRUE.equals(tamperedRes.get("verified")));
        assertEquals("tampered", tamperedRes.get("checksumStatus"));
    }

    @Test
    void shouldRejectRestoreWithWrongConfirmPhrase() throws IOException
    {
        Path backupDir = tempProfileDir.resolve("backup");
        Files.createDirectories(backupDir);
        Files.write(backupDir.resolve("canglian_backup_20261122_020000.sql"),
            "-- Canglian ERP SQL Backup\nSELECT 1;\n".getBytes(StandardCharsets.UTF_8));

        assertThrows(ServiceException.class,
            () -> backupService.restoreBackup("canglian_backup_20261122_020000.sql", "WRONG_PHRASE", "admin"));
    }
}
