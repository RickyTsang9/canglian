package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 库存流水
 * 
 * @author canglian
 */
public class WmsStockLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long stockLogId;

    private Long warehouseId;

    private Long productId;

    private Long locationId;

    private String batchNo;

    private String billType;

    private Long billId;

    private String inOut;

    private BigDecimal quantity;

    private BigDecimal price;

    private BigDecimal amount;

    private BigDecimal beforeQty;

    private BigDecimal afterQty;

    /**
     * 获取库存流水id
     * 
     * @return 库存流水id
     */
    public Long getStockLogId()
    {
        return stockLogId;
    }

    /**
     * 设置库存流水id
     * 
     * @param stockLogId 库存流水id
     */
    public void setStockLogId(Long stockLogId)
    {
        this.stockLogId = stockLogId;
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

    public Long getLocationId()
    {
        return locationId;
    }

    public void setLocationId(Long locationId)
    {
        this.locationId = locationId;
    }

    public String getBatchNo()
    {
        return batchNo;
    }

    public void setBatchNo(String batchNo)
    {
        this.batchNo = batchNo;
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
     * 获取变动前数量
     * 
     * @return 变动前数量
     */
    public BigDecimal getBeforeQty()
    {
        return beforeQty;
    }

    /**
     * 设置变动前数量
     * 
     * @param beforeQty 变动前数量
     */
    public void setBeforeQty(BigDecimal beforeQty)
    {
        this.beforeQty = beforeQty;
    }

    /**
     * 获取变动后数量
     * 
     * @return 变动后数量
     */
    public BigDecimal getAfterQty()
    {
        return afterQty;
    }

    /**
     * 设置变动后数量
     * 
     * @param afterQty 变动后数量
     */
    public void setAfterQty(BigDecimal afterQty)
    {
        this.afterQty = afterQty;
    }

    /**
     * 获取库存流水字符串
     * 
     * @return 库存流水字符串
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("stockLogId", getStockLogId())
            .append("warehouseId", getWarehouseId())
            .append("productId", getProductId())
            .append("locationId", getLocationId())
            .append("batchNo", getBatchNo())
            .append("billType", getBillType())
            .append("billId", getBillId())
            .append("inOut", getInOut())
            .append("quantity", getQuantity())
            .append("price", getPrice())
            .append("amount", getAmount())
            .append("beforeQty", getBeforeQty())
            .append("afterQty", getAfterQty())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}

