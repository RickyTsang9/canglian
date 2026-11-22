package com.canglian.business.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import com.canglian.business.domain.BizDataBackupInfo;

/**
 * 数据库备份服务接口
 *
 * @author canglian
 */
public interface IBizDataBackupService
{
    /**
     * 查询历史备份文件列表
     *
     * @return 备份文件集合
     */
    List<BizDataBackupInfo> selectBackupList();

    /**
     * 执行在线 SQL 快照备份（默认明文 SQL）
     *
     * @param operator 操作人
     * @return 新建的备份文件信息
     */
    BizDataBackupInfo createBackup(String operator);

    /**
     * 执行在线 SQL 快照备份（可选 GZIP 压缩）
     *
     * @param operator 操作人
     * @param compress 是否压缩为 .sql.gz
     * @return 新建的备份文件信息
     */
    BizDataBackupInfo createBackup(String operator, boolean compress);

    /**
     * 获取备份文件对象（用于安全下载）
     *
     * @param fileName 备份文件名
     * @return 文件对象
     */
    File getBackupFile(String fileName);

    /**
     * 校验备份文件完整性（SHA-256 与文件头签名）
     *
     * @param fileName 备份文件名
     * @return 校验结果详情
     */
    Map<String, Object> verifyBackup(String fileName);

    /**
     * 清理超过保留天数的过期备份（至少保留最新 3 份）
     *
     * @param retainDays 保留天数
     * @return 清理删除的文件数
     */
    int cleanExpiredBackups(int retainDays);

    /**
     * 受控恢复备份快照（恢复前强制生成兜底快照并校验确认口令）
     *
     * @param fileName 备份文件名
     * @param confirmPhrase 二次确认口令（必须等于 "RESTORE:" + fileName）
     * @param operator 操作人
     * @return 恢复结果摘要
     */
    Map<String, Object> restoreBackup(String fileName, String confirmPhrase, String operator);

    /**
     * 删除指定备份文件
     *
     * @param fileName 备份文件名
     * @return 删除数量
     */
    int deleteBackup(String fileName);
}
