package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 成本流水对象 fin_cost_log
 * 
 * @author canglian
 */
public class FinCostLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 成本流水id */
    private Long costLogId;

    /** 成本账户id */
    private Long costAccountId;

    /** 商品id */
    private Long productId;

    /** 仓库id */
    private Long warehouseId;

    /** 单据类型 */
    private String billType;

    /** 单据id */
    private Long billId;

    /** 出入库标识 */
    private String inOut;

    /** 数量 */
    private BigDecimal quantity;

    /** 单价 */
    private BigDecimal price;

    /** 金额 */
    private BigDecimal amount;

    /** 成本算法 */
    private String costMethod;

    /**
     * 获取成本流水id
     * 
     * @return 成本流水id
     */
    public Long getCostLogId()
    {
        return costLogId;
    }

    /**
     * 设置成本流水id
     * 
     * @param costLogId 成本流水id
     */
    public void setCostLogId(Long costLogId)
    {
        this.costLogId = costLogId;
    }

    /**
     * 获取成本账户id
     * 
     * @return 成本账户id
     */
    public Long getCostAccountId()
    {
        return costAccountId;
    }

    /**
     * 设置成本账户id
     * 
     * @param costAccountId 成本账户id
     */
    public void setCostAccountId(Long costAccountId)
    {
        this.costAccountId = costAccountId;
    }

    /**
     * 获取商品id
     * 
     * @return 商品id
     */
    public Long getProductId()
    {
        return productId;
    }

    /**
     * 设置商品id
     * 
     * @param productId 商品id
     */
    public void setProductId(Long productId)
    {
        this.productId = productId;
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
     * 获取单据类型
     * 
     * @return 单据类型
     */
    public String getBillType()
    {
        return billType;
    }

    /**
     * 设置单据类型
     * 
     * @param billType 单据类型
     */
    public void setBillType(String billType)
    {
        this.billType = billType;
    }

    /**
     * 获取单据id
     * 
     * @return 单据id
     */
    public Long getBillId()
    {
        return billId;
    }

    /**
     * 设置单据id
     * 
     * @param billId 单据id
     */
    public void setBillId(Long billId)
    {
        this.billId = billId;
    }

    /**
     * 获取出入库标识
     * 
     * @return 出入库标识
     */
    public String getInOut()
    {
        return inOut;
    }

    /**
     * 设置出入库标识
     * 
     * @param inOut 出入库标识
     */
    public void setInOut(String inOut)
    {
        this.inOut = inOut;
    }

    /**
     * 获取数量
     * 
     * @return 数量
     */
    public BigDecimal getQuantity()
    {
        return quantity;
    }

    /**
     * 设置数量
     * 
     * @param quantity 数量
     */
    public void setQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
    }

    /**
     * 获取单价
     * 
     * @return 单价
     */
    public BigDecimal getPrice()
    {
        return price;
    }

    /**
     * 设置单价
     * 
     * @param price 单价
     */
    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    /**
     * 获取金额
     * 
     * @return 金额
     */
    public BigDecimal getAmount()
    {
        return amount;
    }

    /**
     * 设置金额
     * 
     * @param amount 金额
     */
    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    /**
     * 获取成本算法
     * 
     * @return 成本算法
     */
    public String getCostMethod()
    {
        return costMethod;
    }

    /**
     * 设置成本算法
     * 
     * @param costMethod 成本算法
     */
    public void setCostMethod(String costMethod)
    {
        this.costMethod = costMethod;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("costLogId", getCostLogId())
            .append("costAccountId", getCostAccountId())
            .append("productId", getProductId())
            .append("warehouseId", getWarehouseId())
            .append("billType", getBillType())
            .append("billId", getBillId())
            .append("inOut", getInOut())
            .append("quantity", getQuantity())
            .append("price", getPrice())
            .append("amount", getAmount())
            .append("costMethod", getCostMethod())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}

