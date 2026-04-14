package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 盘点单
 * 
 * @author canglian
 */
public class WmsInventoryCheck extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long checkId;

    private String checkNo;

    private Long warehouseId;

    private String status;

    private BigDecimal totalDiffQty;

    private BigDecimal totalDiffAmount;

    /**
     * 获取盘点单id
     * 
     * @return 盘点单id
     */
    public Long getCheckId()
    {
        return checkId;
    }

    /**
     * 设置盘点单id
     * 
     * @param checkId 盘点单id
     */
    public void setCheckId(Long checkId)
    {
        this.checkId = checkId;
    }

    /**
     * 获取盘点单号
     * 
     * @return 盘点单号
     */
    public String getCheckNo()
    {
        return checkNo;
    }

    /**
     * 设置盘点单号
     * 
     * @param checkNo 盘点单号
     */
    public void setCheckNo(String checkNo)
    {
        this.checkNo = checkNo;
    }

    /**
     * 获取仓库id
     * 
     * @return 仓库id
     */
    public Long getWarehouseId()
    {
        return warehouseId;
    }

    /**
     * 设置仓库id
     * 
     * @param warehouseId 仓库id
     */
    public void setWarehouseId(Long warehouseId)
    {
        this.warehouseId = warehouseId;
    }

    /**
     * 获取状态
     * 
     * @return 状态
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * 设置状态
     * 
     * @param status 状态
     */
    public void setStatus(String status)
    {
        this.status = status;
    }

    /**
     * 获取差异数量合计
     * 
     * @return 差异数量合计
     */
    public BigDecimal getTotalDiffQty()
    {
        return totalDiffQty;
    }

    /**
     * 设置差异数量合计
     * 
     * @param totalDiffQty 差异数量合计
     */
    public void setTotalDiffQty(BigDecimal totalDiffQty)
    {
        this.totalDiffQty = totalDiffQty;
    }

    /**
     * 获取差异金额合计
     * 
     * @return 差异金额合计
     */
    public BigDecimal getTotalDiffAmount()
    {
        return totalDiffAmount;
    }

    /**
     * 设置差异金额合计
     * 
     * @param totalDiffAmount 差异金额合计
     */
    public void setTotalDiffAmount(BigDecimal totalDiffAmount)
    {
        this.totalDiffAmount = totalDiffAmount;
    }

    /**
     * 获取盘点单字符串
     * 
     * @return 盘点单字符串
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("checkId", getCheckId())
            .append("checkNo", getCheckNo())
            .append("warehouseId", getWarehouseId())
            .append("status", getStatus())
            .append("totalDiffQty", getTotalDiffQty())
            .append("totalDiffAmount", getTotalDiffAmount())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

