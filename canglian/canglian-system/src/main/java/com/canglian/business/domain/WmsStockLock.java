package com.canglian.business.domain;

import java.math.BigDecimal;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 销售订单库存锁库明细台账
 *
 * @author canglian
 */
public class WmsStockLock extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long lockId;

    private Long orderId;

    private Long orderItemId;

    private Long warehouseId;

    private Long productId;

    private Long locationId;

    private String batchNo;

    private Long stockId;

    private BigDecimal lockedQty;

    private BigDecimal releasedQty;

    /** 状态(0锁定中 1已全部释放) */
    private String status;

    public Long getLockId() { return lockId; }
    public void setLockId(Long lockId) { this.lockId = lockId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Long orderItemId) { this.orderItemId = orderItemId; }

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }

    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }

    public Long getStockId() { return stockId; }
    public void setStockId(Long stockId) { this.stockId = stockId; }

    public BigDecimal getLockedQty() { return lockedQty; }
    public void setLockedQty(BigDecimal lockedQty) { this.lockedQty = lockedQty; }

    public BigDecimal getReleasedQty() { return releasedQty; }
    public void setReleasedQty(BigDecimal releasedQty) { this.releasedQty = releasedQty; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getRemainingLockedQty()
    {
        BigDecimal l = lockedQty == null ? BigDecimal.ZERO : lockedQty;
        BigDecimal r = releasedQty == null ? BigDecimal.ZERO : releasedQty;
        BigDecimal rem = l.subtract(r);
        return rem.compareTo(BigDecimal.ZERO) > 0 ? rem : BigDecimal.ZERO;
    }
}
