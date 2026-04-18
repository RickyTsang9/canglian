package com.canglian.business.service;

import java.util.List;
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

    public int pushPurOrderToInbound(Long purchaseOrderId, String operator);

    public int deletePurOrderById(Long purchaseOrderId);

    public int deletePurOrderByIds(Long[] purchaseOrderIds);
}
