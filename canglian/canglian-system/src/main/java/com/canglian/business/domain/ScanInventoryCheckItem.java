package com.canglian.business.domain;

import java.math.BigDecimal;

/**
 * 扫码盘点明细实体
 * 
 * @author canglian
 */
public class ScanInventoryCheckItem
{
    private String barCode;

    private Long productId;

    private String productCode;

    private String productName;

    private BigDecimal stockQty;

    private BigDecimal actualQty;

    private BigDecimal diffQty;

    public String getBarCode()
    {
        return barCode;
    }

    public void setBarCode(String barCode)
    {
        this.barCode = barCode;
    }

    public Long getProductId()
    {
        return productId;
    }

    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public String getProductCode()
    {
        return productCode;
    }

    public void setProductCode(String productCode)
    {
        this.productCode = productCode;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public BigDecimal getStockQty()
    {
        return stockQty;
    }

    public void setStockQty(BigDecimal stockQty)
    {
        this.stockQty = stockQty;
    }

    public BigDecimal getActualQty()
    {
        return actualQty;
    }

    public void setActualQty(BigDecimal actualQty)
    {
        this.actualQty = actualQty;
    }

    public BigDecimal getDiffQty()
    {
        return diffQty;
    }

    public void setDiffQty(BigDecimal diffQty)
    {
        this.diffQty = diffQty;
    }
}
