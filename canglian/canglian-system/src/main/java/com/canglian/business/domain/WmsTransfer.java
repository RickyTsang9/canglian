package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date businessDate;

    public Date getBusinessDate() { return businessDate; }
    public void setBusinessDate(Date businessDate) { this.businessDate = businessDate; }

    private Long deptId;

    private Long ownerUserId;

    private BigDecimal totalQty;

    private BigDecimal totalAmount;

    private String status;

    private String isReversal;

    private Long originBillId;

    private String reversedFlag;

    private String unauditBy;

    private Date unauditTime;

    private List<WmsTransferItem> itemList;

    public List<WmsTransferItem> getItemList() { return itemList; }
    public void setItemList(List<WmsTransferItem> itemList) { this.itemList = itemList; }
    public Long getTransferId() { return transferId; }
    public void setTransferId(Long transferId) { this.transferId = transferId; }
    public String getTransferNo() { return transferNo; }
    public void setTransferNo(String transferNo) { this.transferNo = transferNo; }
    public Long getOutWarehouseId() { return outWarehouseId; }
    public void setOutWarehouseId(Long outWarehouseId) { this.outWarehouseId = outWarehouseId; }
    public Long getInWarehouseId() { return inWarehouseId; }
    public void setInWarehouseId(Long inWarehouseId) { this.inWarehouseId = inWarehouseId; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(Long ownerUserId) { this.ownerUserId = ownerUserId; }
    public BigDecimal getTotalQty() { return totalQty; }
    public void setTotalQty(BigDecimal totalQty) { this.totalQty = totalQty; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getIsReversal() { return isReversal; }
    public void setIsReversal(String isReversal) { this.isReversal = isReversal; }
    public Long getOriginBillId() { return originBillId; }
    public void setOriginBillId(Long originBillId) { this.originBillId = originBillId; }
    public String getReversedFlag() { return reversedFlag; }
    public void setReversedFlag(String reversedFlag) { this.reversedFlag = reversedFlag; }
    public String getUnauditBy() { return unauditBy; }
    public void setUnauditBy(String unauditBy) { this.unauditBy = unauditBy; }
    public Date getUnauditTime() { return unauditTime; }
    public void setUnauditTime(Date unauditTime) { this.unauditTime = unauditTime; }

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
            .append("isReversal", getIsReversal())
            .append("originBillId", getOriginBillId())
            .append("reversedFlag", getReversedFlag())
            .toString();
    }
}
