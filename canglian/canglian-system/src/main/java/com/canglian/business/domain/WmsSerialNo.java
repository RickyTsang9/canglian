package com.canglian.business.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.annotation.Excel;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 商品序列号(SN)档案实体 wms_serial_no
 *
 * @author canglian
 */
public class WmsSerialNo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long serialId;

    @Excel(name = "序列号(SN)")
    private String snCode;

    @Excel(name = "商品ID")
    private Long productId;

    @Excel(name = "仓库ID")
    private Long warehouseId;

    @Excel(name = "库位ID")
    private Long locationId;

    @Excel(name = "批次号")
    private String batchNo;

    /** 状态：in_stock在库 / out_stock已出库 / returned退货回库 / scrapped报废 */
    @Excel(name = "状态", readConverterExp = "in_stock=在库,sold=已售,returned=已退货,loss=盘亏核销")
    private String status;

    private Long inboundId;

    private Long outboundId;

    private Long customerId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "保修截止日", dateFormat = "yyyy-MM-dd")
    private Date warrantyEndDate;

    public Long getSerialId() { return serialId; }
    public void setSerialId(Long serialId) { this.serialId = serialId; }
    public String getSnCode() { return snCode; }
    public void setSnCode(String snCode) { this.snCode = snCode; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getInboundId() { return inboundId; }
    public void setInboundId(Long inboundId) { this.inboundId = inboundId; }
    public Long getOutboundId() { return outboundId; }
    public void setOutboundId(Long outboundId) { this.outboundId = outboundId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Date getWarrantyEndDate() { return warrantyEndDate; }
    public void setWarrantyEndDate(Date warrantyEndDate) { this.warrantyEndDate = warrantyEndDate; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("serialId", getSerialId())
            .append("snCode", getSnCode())
            .append("productId", getProductId())
            .append("warehouseId", getWarehouseId())
            .append("status", getStatus())
            .toString();
    }
}
