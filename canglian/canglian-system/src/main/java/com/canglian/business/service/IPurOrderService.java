package com.canglian.business.service;

import java.util.List;
import java.util.Map;
import com.canglian.business.domain.PurOrder;

/**
 * 购货订单服务接口
 *
 * @author canglian
 */
public interface IPurOrderService
{
    public PurOrder selectPurOrderById(Long purchaseOrderId);

    public List<PurOrder> selectPurOrderList(PurOrder purOrder);

    public int insertPurOrder(PurOrder purOrder);

    public int updatePurOrder(PurOrder purOrder);

    public int approvePurOrder(Long purchaseOrderId, String operator);

    /** 反审批购货订单（仅限已审批且无下游单据） */
    public int unapprovePurOrder(Long purchaseOrderId, String operator);

    /** 强制关闭购货订单 */
    public int closePurOrder(Long purchaseOrderId, String closeReason, String operator);

    /** 整单下推入库单（剩余全部未入库数量） */
    public int pushPurOrderToInbound(Long purchaseOrderId, String operator);

    /** 分批下推入库单（按明细指定数量；pushItems 为空时按全部剩余数量下推） */
    public int pushPurOrderToInbound(Long purchaseOrderId, List<Map<String, Object>> pushItems, String operator);

    /** 由下游入库单变更（审核/反审核/删除/红冲）回算采购订单履约进度与状态 */
    public void syncOrderProgressFromWms(Long purchaseOrderId, String operator);

    public int deletePurOrderById(Long purchaseOrderId);

    public int deletePurOrderByIds(Long[] purchaseOrderIds);
}
