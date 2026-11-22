package com.canglian.business.domain;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 数据库备份文件信息
 *
 * @author canglian
 */
public class BizDataBackupInfo implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 备份文件名 */
    private String fileName;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 格式化文件大小 */
    private String fileSizeText;

    /** 备份时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 备份表数量 */
    private Integer tableCount;

    /** 备份记录总数 */
    private Long rowCount;

    /** SHA-256 摘要 */
    private String sha256;

    /** 是否 GZIP 压缩 */
    private Boolean compressed;

    /** 完整性校验状态：valid / missing / tampered */
    private String checksumStatus;

    /** 是否已通过校验 */
    private Boolean verified;

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public Long getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public String getFileSizeText()
    {
        return fileSizeText;
    }

    public void setFileSizeText(String fileSizeText)
    {
        this.fileSizeText = fileSizeText;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Integer getTableCount()
    {
        return tableCount;
    }

    public void setTableCount(Integer tableCount)
    {
        this.tableCount = tableCount;
    }

    public Long getRowCount()
    {
        return rowCount;
    }

    public void setRowCount(Long rowCount)
    {
        this.rowCount = rowCount;
    }

    public String getSha256()
    {
        return sha256;
    }

    public void setSha256(String sha256)
    {
        this.sha256 = sha256;
    }

    public Boolean getCompressed()
    {
        return compressed;
    }

    public void setCompressed(Boolean compressed)
    {
        this.compressed = compressed;
    }

    public String getChecksumStatus()
    {
        return checksumStatus;
    }

    public void setChecksumStatus(String checksumStatus)
    {
        this.checksumStatus = checksumStatus;
    }

    public Boolean getVerified()
    {
        return verified;
    }

    public void setVerified(Boolean verified)
    {
        this.verified = verified;
    }
}
