package com.canglian.business.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 库位档案
 * 
 * @author canglian
 */
public class MdLocation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 库位id */
    private Long locationId;

    /** 仓库id */
    private Long warehouseId;

    /** 库位编码 */
    private String locationCode;

    /** 库位名称 */
    private String locationName;

    /** 状态（0正常 1停用） */
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public void setLocationId(Long locationId) 
    {
        this.locationId = locationId;
    }

    public Long getLocationId() 
    {
        return locationId;
    }

    public void setWarehouseId(Long warehouseId) 
    {
        this.warehouseId = warehouseId;
    }

    public Long getWarehouseId() 
    {
        return warehouseId;
    }

    public void setLocationCode(String locationCode) 
    {
        this.locationCode = locationCode;
    }

    public String getLocationCode() 
    {
        return locationCode;
    }

    public void setLocationName(String locationName) 
    {
        this.locationName = locationName;
    }

    public String getLocationName() 
    {
        return locationName;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("locationId", getLocationId())
            .append("warehouseId", getWarehouseId())
            .append("locationCode", getLocationCode())
            .append("locationName", getLocationName())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

