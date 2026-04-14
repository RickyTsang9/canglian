package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 调拨单
 * 
 * @author canglian
 */
public class WmsTransfer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long transferId;

    private String transferNo;

    private Long outWarehouseId;

    private Long inWarehouseId;

    private BigDecimal totalQty;

    private BigDecimal totalAmount;

    private String status;

    /**
     * 获取调拨单id
     * 
     * @return 调拨单id
     */
    public Long getTransferId()
    {
        return transferId;
    }

    /**
     * 设置调拨单id
     * 
     * @param transferId 调拨单id
     */
    public void setTransferId(Long transferId)
    {
        this.transferId = transferId;
    }

    /**
     * 获取调拨单号
     * 
     * @return 调拨单号
     */
    public String getTransferNo()
    {
        return transferNo;
    }

    /**
     * 设置调拨单号
     * 
     * @param transferNo 调拨单号
     */
    public void setTransferNo(String transferNo)
    {
        this.transferNo = transferNo;
    }

    /**
     * 获取调出仓库id
     * 
     * @return 调出仓库id
     */
    public Long getOutWarehouseId()
    {
        return outWarehouseId;
    }

    /**
     * 设置调出仓库id
     * 
     * @param outWarehouseId 调出仓库id
     */
    public void setOutWarehouseId(Long outWarehouseId)
    {
        this.outWarehouseId = outWarehouseId;
    }

    /**
     * 获取调入仓库id
     * 
     * @return 调入仓库id
     */
    public Long getInWarehouseId()
    {
        return inWarehouseId;
    }

    /**
     * 设置调入仓库id
     * 
     * @param inWarehouseId 调入仓库id
     */
    public void setInWarehouseId(Long inWarehouseId)
    {
        this.inWarehouseId = inWarehouseId;
    }

    /**
     * 获取总数量
     * 
     * @return 总数量
     */
    public BigDecimal getTotalQty()
    {
        return totalQty;
    }

    /**
     * 设置总数量
     * 
     * @param totalQty 总数量
     */
    public void setTotalQty(BigDecimal totalQty)
    {
        this.totalQty = totalQty;
    }

    /**
     * 获取总金额
     * 
     * @return 总金额
     */
    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    /**
     * 设置总金额
     * 
     * @param totalAmount 总金额
     */
    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
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
     * 获取调拨单字符串
     * 
     * @return 调拨单字符串
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("transferId", getTransferId())
            .append("transferNo", getTransferNo())
            .append("outWarehouseId", getOutWarehouseId())
            .append("inWarehouseId", getInWarehouseId())
            .append("totalQty", getTotalQty())
            .append("totalAmount", getTotalAmount())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

