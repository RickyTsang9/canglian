package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.List;

/**
 * 扫码开单请求实体
 * 
 * @author canglian
 */
public class ScanQuickSaleRequest
{
    private Long customerId;

    private Long warehouseId;

    private String barCode;

    private BigDecimal quantity;

    private String remark;

    private List<ScanQuickSaleItem> quickSaleItemList;

    public Long getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    public Long getWarehouseId()
    {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId)
    {
        this.warehouseId = warehouseId;
    }

    public String getBarCode()
    {
        return barCode;
    }

    public void setBarCode(String barCode)
    {
        this.barCode = barCode;
    }

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public List<ScanQuickSaleItem> getQuickSaleItemList()
    {
        return quickSaleItemList;
    }

    public void setQuickSaleItemList(List<ScanQuickSaleItem> quickSaleItemList)
    {
        this.quickSaleItemList = quickSaleItemList;
    }
}
