package com.canglian.business.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.canglian.business.domain.SalOrderItem;

/**
 * 销售单据明细数据层
 *
 * @author canglian
 */
public interface SalOrderItemMapper
{
    public List<SalOrderItem> selectSalOrderItemListByOrderId(Long orderId);

    public SalOrderItem selectSalOrderItemById(Long orderItemId);

    public int insertSalOrderItem(SalOrderItem salOrderItem);

    public int updateShippedQty(@Param("orderItemId") Long orderItemId, @Param("shippedQty") BigDecimal shippedQty);

    public int deleteSalOrderItemByOrderId(Long orderId);
}
