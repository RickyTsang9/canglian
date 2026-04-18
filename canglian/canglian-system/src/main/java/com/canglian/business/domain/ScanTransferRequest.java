package com.canglian.business.domain;

import java.util.List;

/**
 * 扫码调拨建单请求实体
 * 
 * @author canglian
 */
public class ScanTransferRequest
{
    private Long outWarehouseId;

    private Long inWarehouseId;

    private String outLocationCode;

    private String inLocationCode;

    private String remark;

    private List<ScanTransferItem> transferItemList;

    public Long getOutWarehouseId()
    {
        return outWarehouseId;
    }

    public void setOutWarehouseId(Long outWarehouseId)
    {
        this.outWarehouseId = outWarehouseId;
    }

    public Long getInWarehouseId()
    {
        return inWarehouseId;
    }

    public void setInWarehouseId(Long inWarehouseId)
    {
        this.inWarehouseId = inWarehouseId;
    }

    public String getOutLocationCode()
    {
        return outLocationCode;
    }

    public void setOutLocationCode(String outLocationCode)
    {
        this.outLocationCode = outLocationCode;
    }

    public String getInLocationCode()
    {
        return inLocationCode;
    }

    public void setInLocationCode(String inLocationCode)
    {
        this.inLocationCode = inLocationCode;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public List<ScanTransferItem> getTransferItemList()
    {
        return transferItemList;
    }

    public void setTransferItemList(List<ScanTransferItem> transferItemList)
    {
        this.transferItemList = transferItemList;
    }
}
