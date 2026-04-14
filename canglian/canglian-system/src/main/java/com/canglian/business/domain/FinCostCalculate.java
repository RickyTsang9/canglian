package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 成本核算请求对象
 * 
 * @author canglian
 */
public class FinCostCalculate
{
    /** 成本算法 */
    private String costMethod;

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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("costMethod", getCostMethod())
            .append("productId", getProductId())
            .append("warehouseId", getWarehouseId())
            .append("billType", getBillType())
            .append("billId", getBillId())
            .append("inOut", getInOut())
            .append("quantity", getQuantity())
            .append("price", getPrice())
            .toString();
    }
}

