package com.canglian.business.domain;

import java.util.List;

/**
 * 扫码盘点建单请求实体
 * 
 * @author canglian
 */
public class ScanInventoryCheckRequest
{
    private Long warehouseId;

    private String locationCode;

    private String remark;

    private List<ScanInventoryCheckItem> inventoryCheckItemList;

    public Long getWarehouseId()
    {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId)
    {
        this.warehouseId = warehouseId;
    }

    public String getLocationCode()
    {
        return locationCode;
    }

    public void setLocationCode(String locationCode)
    {
        this.locationCode = locationCode;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public List<ScanInventoryCheckItem> getInventoryCheckItemList()
    {
        return inventoryCheckItemList;
    }

    public void setInventoryCheckItemList(List<ScanInventoryCheckItem> inventoryCheckItemList)
    {
        this.inventoryCheckItemList = inventoryCheckItemList;
    }
}
