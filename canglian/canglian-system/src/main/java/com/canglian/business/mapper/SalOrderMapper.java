package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.SalOrder;

/**
 * 销售单据数据层
 * 
 * @author canglian
 */
public interface SalOrderMapper
{
    public SalOrder selectSalOrderById(Long orderId);

    public List<SalOrder> selectSalOrderList(SalOrder salOrder);

    public int insertSalOrder(SalOrder salOrder);

    public int updateSalOrder(SalOrder salOrder);

    public int deleteSalOrderById(Long orderId);

    public int deleteSalOrderByIds(Long[] orderIds);

    public String selectMaxOrderNoByPrefix(String orderNoPrefix);
}
