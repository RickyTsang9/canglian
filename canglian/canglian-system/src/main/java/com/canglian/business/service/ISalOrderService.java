package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.SalOrder;

/**
 * 销售单据服务接口
 * 
 * @author canglian
 */
public interface ISalOrderService
{
    public SalOrder selectSalOrderById(Long orderId);

    public List<SalOrder> selectSalOrderList(SalOrder salOrder);

    public int insertSalOrder(SalOrder salOrder);

    public int updateSalOrder(SalOrder salOrder);

    public int approveSalOrder(Long orderId, String operator);

    public int pushQuoteToSale(Long orderId, String operator);

    public int pushSaleOrderToOutbound(Long orderId, String operator);

    public int deleteSalOrderById(Long orderId);

    public int deleteSalOrderByIds(Long[] orderIds);
}
