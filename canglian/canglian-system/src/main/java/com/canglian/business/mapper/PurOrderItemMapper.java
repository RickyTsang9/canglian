package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.PurOrderItem;

/**
 * 购货订单明细数据层
 * 
 * @author canglian
 */
public interface PurOrderItemMapper
{
    public List<PurOrderItem> selectPurOrderItemListByOrderId(Long purchaseOrderId);

    public int insertPurOrderItem(PurOrderItem purOrderItem);

    public int deletePurOrderItemByOrderId(Long purchaseOrderId);
}
