package com.canglian.business.service;

import java.util.List;
import java.util.Map;
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

    /** 反审批销售单据（仅限已审批且无下游单据；若已锁库则自动释放） */
    public int unapproveSalOrder(Long orderId, String operator);

    /** 强制关闭销售单据（若已锁库则自动释放剩余未出库锁库量） */
    public int closeSalOrder(Long orderId, String closeReason, String operator);

    public int pushQuoteToSale(Long orderId, String operator);

    /** 整单下推出库单（剩余全部未出库数量） */
    public int pushSaleOrderToOutbound(Long orderId, String operator);

    /** 分批下推出库单（pushItems 为空时按全部剩余数量下推） */
    public int pushSaleOrderToOutbound(Long orderId, List<Map<String, Object>> pushItems, String operator);

    /** 由下游出库单变更（审核/反审核/删除/红冲）回算销货订单履约进度与状态 */
    public void syncOrderProgressFromWms(Long orderId, String operator);

    public int deleteSalOrderById(Long orderId);

    public int deleteSalOrderByIds(Long[] orderIds);
}
