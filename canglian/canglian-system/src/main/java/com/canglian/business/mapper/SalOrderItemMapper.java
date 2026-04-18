package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.SalOrderItem;

/**
 * 销售单据明细数据层
 * 
 * @author canglian
 */
public interface SalOrderItemMapper
{
    public List<SalOrderItem> selectSalOrderItemListByOrderId(Long orderId);

    public int insertSalOrderItem(SalOrderItem salOrderItem);

    public int deleteSalOrderItemByOrderId(Long orderId);
}
