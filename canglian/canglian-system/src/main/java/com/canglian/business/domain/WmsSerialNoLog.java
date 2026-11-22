package com.canglian.business.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 商品序列号(SN)流转履历实体 wms_serial_no_log
 *
 * @author canglian
 */
public class WmsSerialNoLog
{
    private Long snLogId;

    private Long serialId;

    private String snCode;

    private String actionType;

    private String billType;

    private Long billId;

    private String billNo;

    private Long warehouseId;

    private Long counterpartyId;

    private String remark;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getSnLogId() { return snLogId; }
    public void setSnLogId(Long snLogId) { this.snLogId = snLogId; }
    public Long getSerialId() { return serialId; }
    public void setSerialId(Long serialId) { this.serialId = serialId; }
    public String getSnCode() { return snCode; }
    public void setSnCode(String snCode) { this.snCode = snCode; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getBillType() { return billType; }
    public void setBillType(String billType) { this.billType = billType; }
    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }
    public String getBillNo() { return billNo; }
    public void setBillNo(String billNo) { this.billNo = billNo; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public Long getCounterpartyId() { return counterpartyId; }
    public void setCounterpartyId(Long counterpartyId) { this.counterpartyId = counterpartyId; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
