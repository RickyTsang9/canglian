package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.annotation.Excel;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 盘点单
 *
 * @author canglian
 */
public class WmsInventoryCheck extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long checkId;

    @Excel(name = "盘点单号")
    private String checkNo;

    @Excel(name = "仓库ID")
    private Long warehouseId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "盘点日期", dateFormat = "yyyy-MM-dd")
    private Date businessDate;

    public Date getBusinessDate() { return businessDate; }
    public void setBusinessDate(Date businessDate) { this.businessDate = businessDate; }

    private Long deptId;

    private Long ownerUserId;

    @Excel(name = "状态", readConverterExp = "0=草稿,1=已审核")
    private String status;

    @Excel(name = "总差异数量")
    private BigDecimal totalDiffQty;

    @Excel(name = "总差异金额")
    private BigDecimal totalDiffAmount;

    private String isReversal;

    private Long originBillId;

    private String reversedFlag;

    private String unauditBy;

    private Date unauditTime;

    private List<WmsInventoryCheckItem> itemList;

    public List<WmsInventoryCheckItem> getItemList() { return itemList; }
    public void setItemList(List<WmsInventoryCheckItem> itemList) { this.itemList = itemList; }
    public Long getCheckId() { return checkId; }
    public void setCheckId(Long checkId) { this.checkId = checkId; }
    public String getCheckNo() { return checkNo; }
    public void setCheckNo(String checkNo) { this.checkNo = checkNo; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(Long ownerUserId) { this.ownerUserId = ownerUserId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getTotalDiffQty() { return totalDiffQty; }
    public void setTotalDiffQty(BigDecimal totalDiffQty) { this.totalDiffQty = totalDiffQty; }
    public BigDecimal getTotalDiffAmount() { return totalDiffAmount; }
    public void setTotalDiffAmount(BigDecimal totalDiffAmount) { this.totalDiffAmount = totalDiffAmount; }
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
            .append("checkId", getCheckId())
            .append("checkNo", getCheckNo())
            .append("warehouseId", getWarehouseId())
            .append("status", getStatus())
            .append("totalDiffQty", getTotalDiffQty())
            .append("totalDiffAmount", getTotalDiffAmount())
            .append("isReversal", getIsReversal())
            .append("originBillId", getOriginBillId())
            .append("reversedFlag", getReversedFlag())
            .toString();
    }
}
