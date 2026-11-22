package com.canglian.business.service;

import java.math.BigDecimal;
import java.util.List;
import com.canglian.business.domain.WmsStock;

/**
 * 库存 服务层
 *
 * @author canglian
 */
public interface IWmsStockService
{
    public WmsStock selectWmsStockById(Long stockId);

    public List<WmsStock> selectWmsStockList(WmsStock wmsStock);

    public String importWmsStock(List<WmsStock> stockList, Boolean updateSupport, String operator);

    public List<WmsStock> selectWmsStockWarningList(WmsStock wmsStock);

    public List<WmsStock> selectWmsStockRiskList(WmsStock wmsStock);

    /** 查询效期预警库存列表（已过期与临期批次） */
    public List<WmsStock> selectExpiryWarningList(WmsStock wmsStock);

    /** 查询 FEFO（先到期先出）推荐拣货批次列表（自动排除已过期批次） */
    public List<WmsStock> selectFefoBatchList(Long warehouseId, Long productId);

    /** 手工冻结可用库存（质检/待判/残次隔离） */
    public WmsStock freezeStock(Long stockId, BigDecimal freezeQty, String reason, String operator);

    /** 手工解冻已冻结库存 */
    public WmsStock unfreezeStock(Long stockId, BigDecimal unfreezeQty, String reason, String operator);

    /** 销货订单审批锁库（按 FEFO 顺序记录订单锁库台账并增加 locked_quantity） */
    public void lockStockForSaleOrder(Long warehouseId, Long productId, BigDecimal lockQty, Long orderId, String operator);

    /** 释放销货订单锁库（反审批/强制关闭/出库审核扣减时调用） */
    public void releaseStockLockForSaleOrder(Long warehouseId, Long productId, BigDecimal releaseQty, Long orderId, String operator);

    /** 按指定库位与批次优先释放该订单自有锁库（杜绝跨单窃锁与选批次死锁） */
    public void releaseStockLockForSaleOrder(Long warehouseId, Long productId, Long locationId, String batchNo, BigDecimal releaseQty, Long orderId, String operator);

    /** 释放指定销售订单尚余的全部锁库（用于订单关闭/反审批） */
    public void releaseAllRemainingLocksForOrder(Long orderId, String operator);

    public int insertWmsStock(WmsStock wmsStock);

    public int updateWmsStock(WmsStock wmsStock);

    public int deleteWmsStockById(Long stockId);

    public int deleteWmsStockByIds(Long[] stockIds);
}
