package com.canglian.business.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.canglian.business.domain.PurOrderItem;

/**
 * 购货订单明细数据层
 *
 * @author canglian
 */
public interface PurOrderItemMapper
{
    public List<PurOrderItem> selectPurOrderItemListByOrderId(Long purchaseOrderId);

    public PurOrderItem selectPurOrderItemById(Long purchaseOrderItemId);

    public int insertPurOrderItem(PurOrderItem purOrderItem);

    public int updateReceivedQty(@Param("purchaseOrderItemId") Long purchaseOrderItemId, @Param("receivedQty") BigDecimal receivedQty);

    public int deletePurOrderItemByOrderId(Long purchaseOrderId);
}
