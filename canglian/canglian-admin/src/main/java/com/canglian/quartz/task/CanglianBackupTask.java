package com.canglian.quartz.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.canglian.business.domain.BizDataBackupInfo;
import com.canglian.business.service.IBizDataBackupService;

/**
 * 仓链数据库自动定时备份任务（由 Quartz sys_job 调度）
 *
 * @author canglian
 */
@Component("canglianBackupTask")
public class CanglianBackupTask
{
    private static final Logger log = LoggerFactory.getLogger(CanglianBackupTask.class);

    @Autowired
    private IBizDataBackupService bizDataBackupService;

    /**
     * 定时自动备份并清理过期备份文件
     *
     * @param retainDays 保留天数（默认 30 天，且始终至少保留最新 3 份）
     */
    public void autoBackup(Integer retainDays)
    {
        int days = (retainDays == null || retainDays < 1) ? 30 : retainDays;
        log.info("开始执行仓链数据库定时自动备份，保留策略={}天", days);
        BizDataBackupInfo info = bizDataBackupService.createBackup("quartz_auto", true);
        int cleaned = bizDataBackupService.cleanExpiredBackups(days);
        log.info("仓链数据库定时自动备份完成：fileName={}, tables={}, rows={}, size={}, cleanedExpired={}",
            info.getFileName(), info.getTableCount(), info.getRowCount(), info.getFileSizeText(), cleaned);
    }

    /**
     * 无参默认自动备份（保留 30 天）
     */
    public void autoBackupNoParams()
    {
        autoBackup(30);
    }
}
