package com.canglian.web.controller.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.business.service.IBizDataBackupService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.enums.BusinessType;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.SecurityUtils;
import com.canglian.quartz.domain.SysJob;
import com.canglian.quartz.service.ISysJobService;

/**
 * 数据库备份控制器
 *
 * @author canglian
 */
@RestController
@RequestMapping("/business/dataBackup")
public class BizDataBackupController extends BaseController
{
    @Autowired
    private IBizDataBackupService bizDataBackupService;

    @Autowired(required = false)
    private ISysJobService sysJobService;

    @PreAuthorize("@ss.hasPermi('business:dataBackup:list')")
    @GetMapping("/list")
    public AjaxResult list()
    {
        return success(bizDataBackupService.selectBackupList());
    }

    @PreAuthorize("@ss.hasPermi('business:dataBackup:list')")
    @GetMapping("/schedule")
    public AjaxResult schedule()
    {
        Map<String, Object> info = new LinkedHashMap<String, Object>();
        info.put("configured", false);
        if (sysJobService != null)
        {
            SysJob query = new SysJob();
            query.setInvokeTarget("canglianBackupTask");
            List<SysJob> jobs = sysJobService.selectJobList(query);
            if (jobs != null && !jobs.isEmpty())
            {
                SysJob job = jobs.get(0);
                info.put("configured", true);
                info.put("jobId", job.getJobId());
                info.put("jobName", job.getJobName());
                info.put("invokeTarget", job.getInvokeTarget());
                info.put("cronExpression", job.getCronExpression());
                info.put("status", job.getStatus());
                info.put("nextValidTime", job.getNextValidTime());
            }
        }
        return success(info);
    }

    @PreAuthorize("@ss.hasPermi('business:dataBackup:add')")
    @Log(title = "数据备份", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public AjaxResult create(@RequestParam(defaultValue = "false") boolean compress)
    {
        return success(bizDataBackupService.createBackup(getUsername(), compress));
    }

    @PreAuthorize("@ss.hasPermi('business:dataBackup:verify')")
    @GetMapping("/verify/{fileName:.+}")
    public AjaxResult verify(@PathVariable String fileName)
    {
        return success(bizDataBackupService.verifyBackup(fileName));
    }

    @PreAuthorize("@ss.hasPermi('business:dataBackup:restore')")
    @Log(title = "数据备份受控恢复", businessType = BusinessType.UPDATE)
    @PostMapping("/restore")
    public AjaxResult restore(@RequestBody Map<String, String> payload)
    {
        if (!SecurityUtils.isAdmin(getUserId()))
        {
            throw new ServiceException("仅超级管理员允许执行数据库在线受控恢复");
        }
        String fileName = payload == null ? null : payload.get("fileName");
        String confirmPhrase = payload == null ? null : payload.get("confirmPhrase");
        return success(bizDataBackupService.restoreBackup(fileName, confirmPhrase, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:dataBackup:download')")
    @Log(title = "数据备份下载", businessType = BusinessType.EXPORT)
    @GetMapping("/download/{fileName:.+}")
    public void download(@PathVariable String fileName, HttpServletResponse response) throws IOException
    {
        File file = bizDataBackupService.getBackupFile(fileName);
        String encodedName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.name()).replace("+", "%20");
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedName + "\"");
        response.setContentLengthLong(file.length());
        try (FileInputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream())
        {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1)
            {
                out.write(buffer, 0, len);
            }
            out.flush();
        }
    }

    @PreAuthorize("@ss.hasPermi('business:dataBackup:remove')")
    @Log(title = "数据备份", businessType = BusinessType.DELETE)
    @DeleteMapping("/{fileName:.+}")
    public AjaxResult remove(@PathVariable String fileName)
    {
        return toAjax(bizDataBackupService.deleteBackup(fileName));
    }
}
