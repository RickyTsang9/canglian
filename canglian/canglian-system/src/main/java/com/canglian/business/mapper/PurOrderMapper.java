package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.PurOrder;

/**
 * 购货订单数据层
 * 
 * @author canglian
 */
public interface PurOrderMapper
{
    public PurOrder selectPurOrderById(Long purchaseOrderId);

    public List<PurOrder> selectPurOrderList(PurOrder purOrder);

    public int insertPurOrder(PurOrder purOrder);

    public int updatePurOrder(PurOrder purOrder);

    public int deletePurOrderById(Long purchaseOrderId);

    public int deletePurOrderByIds(Long[] purchaseOrderIds);

    public String selectMaxPurchaseOrderNoByPrefix(String purchaseOrderNoPrefix);
}
