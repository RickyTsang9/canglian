package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinPayable;
import com.canglian.business.domain.FinReceivable;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.PurOrder;
import com.canglian.business.domain.SalOrder;
import com.canglian.business.domain.WmsInbound;
import com.canglian.business.domain.WmsOutbound;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.mapper.FinPayableMapper;
import com.canglian.business.mapper.FinReceivableMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.PurOrderMapper;
import com.canglian.business.mapper.SalOrderMapper;
import com.canglian.business.mapper.WmsInboundMapper;
import com.canglian.business.mapper.WmsOutboundMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IBusinessWorkbenchService;

/**
 * 业务工作台服务实现
 * 
 * @author canglian
 */
@Service
public class BusinessWorkbenchServiceImpl implements IBusinessWorkbenchService
{
    @Autowired
    private SalOrderMapper salOrderMapper;

    @Autowired
    private PurOrderMapper purOrderMapper;

    @Autowired
    private WmsInboundMapper wmsInboundMapper;

    @Autowired
    private WmsOutboundMapper wmsOutboundMapper;

    @Autowired
    private FinReceivableMapper finReceivableMapper;

    @Autowired
    private FinPayableMapper finPayableMapper;

    @Autowired
    private WmsStockMapper wmsStockMapper;

    @Autowired
    private MdProductMapper mdProductMapper;

    /**
     * 查询业务工作台汇总数据
     * 
     * @return 工作台汇总数据
     */
    @Override
    public Map<String, Object> selectWorkbenchSummary()
    {
        Map<String, Object> summaryData = new LinkedHashMap<String, Object>();
        List<SalOrder> salOrderList = salOrderMapper.selectSalOrderList(new SalOrder());
        List<PurOrder> purOrderList = purOrderMapper.selectPurOrderList(new PurOrder());
        List<WmsInbound> wmsInboundList = wmsInboundMapper.selectWmsInboundList(new WmsInbound());
        List<WmsOutbound> wmsOutboundList = wmsOutboundMapper.selectWmsOutboundList(new WmsOutbound());
        List<FinReceivable> finReceivableList = finReceivableMapper.selectFinReceivableList(new FinReceivable());
        List<FinPayable> finPayableList = finPayableMapper.selectFinPayableList(new FinPayable());
        List<WmsStock> wmsStockList = wmsStockMapper.selectWmsStockList(new WmsStock());
        List<Map<String, Object>> lowStockList = buildLowStockList(wmsStockList);
        int draftSaleOrderCount = countOrderByBizStatus(salOrderList, "draft");
        int draftPurOrderCount = countPurchaseOrderByBizStatus(purOrderList, "draft");
        int draftInboundCount = countInboundByBizStatus(wmsInboundList, "draft");
        int draftOutboundCount = countOutboundByBizStatus(wmsOutboundList, "draft");

        summaryData.put("draftSaleOrderCount", draftSaleOrderCount);
        summaryData.put("draftPurOrderCount", draftPurOrderCount);
        summaryData.put("draftInboundCount", draftInboundCount);
        summaryData.put("draftOutboundCount", draftOutboundCount);
        summaryData.put("pendingApproveCount", draftSaleOrderCount + draftPurOrderCount + draftInboundCount + draftOutboundCount);
        summaryData.put("todayInboundCount", countTodayInbound(wmsInboundList));
        summaryData.put("todayOutboundCount", countTodayOutbound(wmsOutboundList));
        summaryData.put("dueReceivableCount", countDueReceivable(finReceivableList));
        summaryData.put("duePayableCount", countDuePayable(finPayableList));
        summaryData.put("lowStockCount", countLowStock(wmsStockList));
        summaryData.put("lowStockList", lowStockList);
        summaryData.put("dueReceivableList", buildDueReceivableList(finReceivableList));
        summaryData.put("duePayableList", buildDuePayableList(finPayableList));
        summaryData.put("pendingApproveList", buildPendingApproveList(salOrderList, purOrderList, wmsInboundList, wmsOutboundList));
        return summaryData;
    }

    /**
     * 按业务状态统计销售单据数量
     * 
     * @param salOrderList 销售单据集合
     * @param bizStatus 业务状态
     * @return 单据数量
     */
    private int countOrderByBizStatus(List<SalOrder> salOrderList, String bizStatus)
    {
        int orderCount = 0;
        for (SalOrder salOrder : salOrderList)
        {
            if (bizStatus.equals(salOrder.getBizStatus()))
            {
                orderCount++;
            }
        }
        return orderCount;
    }

    /**
     * 按业务状态统计购货订单数量
     * 
     * @param purOrderList 购货订单集合
     * @param bizStatus 业务状态
     * @return 单据数量
     */
    private int countPurchaseOrderByBizStatus(List<PurOrder> purOrderList, String bizStatus)
    {
        int orderCount = 0;
        for (PurOrder purOrder : purOrderList)
        {
            if (bizStatus.equals(purOrder.getBizStatus()))
            {
                orderCount++;
            }
        }
        return orderCount;
    }

    /**
     * 按业务状态统计入库单数量
     * 
     * @param inboundList 入库单集合
     * @param bizStatus 业务状态
     * @return 单据数量
     */
    private int countInboundByBizStatus(List<WmsInbound> inboundList, String bizStatus)
    {
        int inboundCount = 0;
        for (WmsInbound wmsInbound : inboundList)
        {
            if (bizStatus.equals(wmsInbound.getBizStatus()))
            {
                inboundCount++;
            }
        }
        return inboundCount;
    }

    /**
     * 按业务状态统计出库单数量
     * 
     * @param outboundList 出库单集合
     * @param bizStatus 业务状态
     * @return 单据数量
     */
    private int countOutboundByBizStatus(List<WmsOutbound> outboundList, String bizStatus)
    {
        int outboundCount = 0;
        for (WmsOutbound wmsOutbound : outboundList)
        {
            if (bizStatus.equals(wmsOutbound.getBizStatus()))
            {
                outboundCount++;
            }
        }
        return outboundCount;
    }

    /**
     * 统计今日入库单数量
     * 
     * @param inboundList 入库单集合
     * @return 数量
     */
    private int countTodayInbound(List<WmsInbound> inboundList)
    {
        int inboundCount = 0;
        for (WmsInbound wmsInbound : inboundList)
        {
            if (isToday(wmsInbound.getBusinessDate()))
            {
                inboundCount++;
            }
        }
        return inboundCount;
    }

    /**
     * 统计今日出库单数量
     * 
     * @param outboundList 出库单集合
     * @return 数量
     */
    private int countTodayOutbound(List<WmsOutbound> outboundList)
    {
        int outboundCount = 0;
        for (WmsOutbound wmsOutbound : outboundList)
        {
            if (isToday(wmsOutbound.getBusinessDate()))
            {
                outboundCount++;
            }
        }
        return outboundCount;
    }

    /**
     * 统计即将到期应收数量
     * 
     * @param receivableList 应收集合
     * @return 数量
     */
    private int countDueReceivable(List<FinReceivable> receivableList)
    {
        int receivableCount = 0;
        for (FinReceivable finReceivable : receivableList)
        {
            if ("confirmed".equals(finReceivable.getBizStatus()) && !"2".equals(finReceivable.getStatus()) && isDueWithinDays(finReceivable.getDueDate(), 7))
            {
                receivableCount++;
            }
        }
        return receivableCount;
    }

    /**
     * 统计即将到期应付数量
     * 
     * @param payableList 应付集合
     * @return 数量
     */
    private int countDuePayable(List<FinPayable> payableList)
    {
        int payableCount = 0;
        for (FinPayable finPayable : payableList)
        {
            if ("confirmed".equals(finPayable.getBizStatus()) && !"2".equals(finPayable.getStatus()) && isDueWithinDays(finPayable.getDueDate(), 7))
            {
                payableCount++;
            }
        }
        return payableCount;
    }

    /**
     * 构建低库存提醒列表
     * 
     * @param warningStockList 预警库存集合
     * @return 低库存提醒列表
     */
    private List<Map<String, Object>> buildLowStockList(List<WmsStock> warningStockList)
    {
        List<Map<String, Object>> lowStockList = new ArrayList<Map<String, Object>>();
        if (warningStockList == null)
        {
            return lowStockList;
        }
        Map<Long, MdProduct> productCacheMap = new LinkedHashMap<Long, MdProduct>();
        for (WmsStock wmsStock : warningStockList)
        {
            BigDecimal warningMinQty = resolveWarningMinQty(wmsStock, productCacheMap);
            if (warningMinQty.compareTo(BigDecimal.ZERO) <= 0 || defaultAmount(wmsStock.getQuantity()).compareTo(warningMinQty) >= 0)
            {
                continue;
            }
            Map<String, Object> lowStockItem = new LinkedHashMap<String, Object>();
            lowStockItem.put("warehouseId", wmsStock.getWarehouseId());
            lowStockItem.put("productId", wmsStock.getProductId());
            lowStockItem.put("locationId", wmsStock.getLocationId());
            lowStockItem.put("quantity", wmsStock.getQuantity());
            lowStockItem.put("warningMinQty", warningMinQty);
            lowStockList.add(lowStockItem);
            if (lowStockList.size() >= 10)
            {
                break;
            }
        }
        return lowStockList;
    }

    /**
     * 构建应收到期提醒列表
     * 
     * @param receivableList 应收集合
     * @return 应收到期提醒列表
     */
    private List<Map<String, Object>> buildDueReceivableList(List<FinReceivable> receivableList)
    {
        List<Map<String, Object>> dueReceivableList = new ArrayList<Map<String, Object>>();
        for (FinReceivable finReceivable : receivableList)
        {
            if (!"confirmed".equals(finReceivable.getBizStatus()) || "2".equals(finReceivable.getStatus()) || !isDueWithinDays(finReceivable.getDueDate(), 7))
            {
                continue;
            }
            Map<String, Object> dueReceivableItem = new LinkedHashMap<String, Object>();
            dueReceivableItem.put("receivableId", finReceivable.getReceivableId());
            dueReceivableItem.put("receivableNo", finReceivable.getReceivableNo());
            dueReceivableItem.put("customerId", finReceivable.getCustomerId());
            dueReceivableItem.put("amount", finReceivable.getAmount());
            dueReceivableItem.put("receivedAmount", finReceivable.getReceivedAmount());
            dueReceivableItem.put("dueDate", finReceivable.getDueDate());
            dueReceivableList.add(dueReceivableItem);
            if (dueReceivableList.size() >= 10)
            {
                break;
            }
        }
        return dueReceivableList;
    }

    /**
     * 构建应付到期提醒列表
     * 
     * @param payableList 应付集合
     * @return 应付到期提醒列表
     */
    private List<Map<String, Object>> buildDuePayableList(List<FinPayable> payableList)
    {
        List<Map<String, Object>> duePayableList = new ArrayList<Map<String, Object>>();
        for (FinPayable finPayable : payableList)
        {
            if (!"confirmed".equals(finPayable.getBizStatus()) || "2".equals(finPayable.getStatus()) || !isDueWithinDays(finPayable.getDueDate(), 7))
            {
                continue;
            }
            Map<String, Object> duePayableItem = new LinkedHashMap<String, Object>();
            duePayableItem.put("payableId", finPayable.getPayableId());
            duePayableItem.put("payableNo", finPayable.getPayableNo());
            duePayableItem.put("supplierId", finPayable.getSupplierId());
            duePayableItem.put("amount", finPayable.getAmount());
            duePayableItem.put("paidAmount", finPayable.getPaidAmount());
            duePayableItem.put("dueDate", finPayable.getDueDate());
            duePayableList.add(duePayableItem);
            if (duePayableList.size() >= 10)
            {
                break;
            }
        }
        return duePayableList;
    }

    /**
     * 构建审批待办列表
     * 
     * @param salOrderList 销售单据集合
     * @param purOrderList 购货订单集合
     * @param inboundList 入库单集合
     * @param outboundList 出库单集合
     * @return 审批待办列表
     */
    private List<Map<String, Object>> buildPendingApproveList(List<SalOrder> salOrderList, List<PurOrder> purOrderList, List<WmsInbound> inboundList,
        List<WmsOutbound> outboundList)
    {
        List<Map<String, Object>> pendingApproveList = new ArrayList<Map<String, Object>>();
        appendPendingApproveSaleOrderList(pendingApproveList, salOrderList);
        appendPendingApprovePurOrderList(pendingApproveList, purOrderList);
        appendPendingApproveInboundList(pendingApproveList, inboundList);
        appendPendingApproveOutboundList(pendingApproveList, outboundList);
        return pendingApproveList;
    }

    /**
     * 追加销售审批待办
     * 
     * @param pendingApproveList 审批待办列表
     * @param salOrderList 销售单据集合
     */
    private void appendPendingApproveSaleOrderList(List<Map<String, Object>> pendingApproveList, List<SalOrder> salOrderList)
    {
        for (SalOrder salOrder : salOrderList)
        {
            if (!"draft".equals(salOrder.getBizStatus()))
            {
                continue;
            }
            appendPendingApproveItem(pendingApproveList, "saleOrder", "销售单据", salOrder.getOrderId(), salOrder.getOrderNo(), salOrder.getBusinessDate());
            if (pendingApproveList.size() >= 10)
            {
                return;
            }
        }
    }

    /**
     * 追加采购审批待办
     * 
     * @param pendingApproveList 审批待办列表
     * @param purOrderList 购货订单集合
     */
    private void appendPendingApprovePurOrderList(List<Map<String, Object>> pendingApproveList, List<PurOrder> purOrderList)
    {
        for (PurOrder purOrder : purOrderList)
        {
            if (!"draft".equals(purOrder.getBizStatus()))
            {
                continue;
            }
            appendPendingApproveItem(pendingApproveList, "purOrder", "购货订单", purOrder.getPurchaseOrderId(), purOrder.getPurchaseOrderNo(), purOrder.getBusinessDate());
            if (pendingApproveList.size() >= 10)
            {
                return;
            }
        }
    }

    /**
     * 追加入库审批待办
     * 
     * @param pendingApproveList 审批待办列表
     * @param inboundList 入库单集合
     */
    private void appendPendingApproveInboundList(List<Map<String, Object>> pendingApproveList, List<WmsInbound> inboundList)
    {
        for (WmsInbound wmsInbound : inboundList)
        {
            if (!"draft".equals(wmsInbound.getBizStatus()))
            {
                continue;
            }
            appendPendingApproveItem(pendingApproveList, "inbound", "入库单", wmsInbound.getInboundId(), wmsInbound.getInboundNo(), wmsInbound.getBusinessDate());
            if (pendingApproveList.size() >= 10)
            {
                return;
            }
        }
    }

    /**
     * 追加出库审批待办
     * 
     * @param pendingApproveList 审批待办列表
     * @param outboundList 出库单集合
     */
    private void appendPendingApproveOutboundList(List<Map<String, Object>> pendingApproveList, List<WmsOutbound> outboundList)
    {
        for (WmsOutbound wmsOutbound : outboundList)
        {
            if (!"draft".equals(wmsOutbound.getBizStatus()))
            {
                continue;
            }
            appendPendingApproveItem(pendingApproveList, "outbound", "出库单", wmsOutbound.getOutboundId(), wmsOutbound.getOutboundNo(), wmsOutbound.getBusinessDate());
            if (pendingApproveList.size() >= 10)
            {
                return;
            }
        }
    }

    /**
     * 追加审批待办项
     * 
     * @param pendingApproveList 审批待办列表
     * @param billType 单据类型
     * @param billTypeLabel 单据类型名称
     * @param billId 单据编号
     * @param billNo 单据单号
     * @param businessDate 业务日期
     */
    private void appendPendingApproveItem(List<Map<String, Object>> pendingApproveList, String billType, String billTypeLabel, Long billId, String billNo,
        Date businessDate)
    {
        Map<String, Object> pendingApproveItem = new LinkedHashMap<String, Object>();
        pendingApproveItem.put("billType", billType);
        pendingApproveItem.put("billTypeLabel", billTypeLabel);
        pendingApproveItem.put("billId", billId);
        pendingApproveItem.put("billNo", billNo);
        pendingApproveItem.put("businessDate", businessDate);
        pendingApproveList.add(pendingApproveItem);
    }

    /**
     * 统计低库存数量
     * 
     * @param wmsStockList 库存集合
     * @return 低库存数量
     */
    private int countLowStock(List<WmsStock> wmsStockList)
    {
        if (wmsStockList == null)
        {
            return 0;
        }
        int lowStockCount = 0;
        Map<Long, MdProduct> productCacheMap = new LinkedHashMap<Long, MdProduct>();
        for (WmsStock wmsStock : wmsStockList)
        {
            BigDecimal warningMinQty = resolveWarningMinQty(wmsStock, productCacheMap);
            if (warningMinQty.compareTo(BigDecimal.ZERO) > 0
                && defaultAmount(wmsStock.getQuantity()).compareTo(warningMinQty) < 0)
            {
                lowStockCount++;
            }
        }
        return lowStockCount;
    }

    /**
     * 解析库存最小预警值
     * 
     * @param wmsStock 库存信息
     * @param productCacheMap 商品缓存
     * @return 最小预警值
     */
    private BigDecimal resolveWarningMinQty(WmsStock wmsStock, Map<Long, MdProduct> productCacheMap)
    {
        if (wmsStock == null)
        {
            return BigDecimal.ZERO;
        }
        if (defaultAmount(wmsStock.getWarningMinQty()).compareTo(BigDecimal.ZERO) > 0)
        {
            return defaultAmount(wmsStock.getWarningMinQty());
        }
        MdProduct mdProduct = loadProduct(wmsStock.getProductId(), productCacheMap);
        if (mdProduct == null)
        {
            return BigDecimal.ZERO;
        }
        return defaultAmount(mdProduct.getWarningMinQty());
    }

    /**
     * 按商品编号加载商品信息
     * 
     * @param productId 商品编号
     * @param productCacheMap 商品缓存
     * @return 商品信息
     */
    private MdProduct loadProduct(Long productId, Map<Long, MdProduct> productCacheMap)
    {
        if (productId == null)
        {
            return null;
        }
        if (!productCacheMap.containsKey(productId))
        {
            productCacheMap.put(productId, mdProductMapper.selectMdProductById(productId));
        }
        return productCacheMap.get(productId);
    }

    /**
     * 判断是否今天
     * 
     * @param businessDate 业务日期
     * @return 判断结果
     */
    private boolean isToday(Date businessDate)
    {
        if (businessDate == null)
        {
            return false;
        }
        Calendar currentCalendar = Calendar.getInstance();
        Calendar businessDateCalendar = Calendar.getInstance();
        businessDateCalendar.setTime(businessDate);
        return currentCalendar.get(Calendar.YEAR) == businessDateCalendar.get(Calendar.YEAR)
            && currentCalendar.get(Calendar.DAY_OF_YEAR) == businessDateCalendar.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 判断是否在指定天数内到期
     * 
     * @param dueDate 到期日期
     * @param days 天数
     * @return 判断结果
     */
    private boolean isDueWithinDays(Date dueDate, int days)
    {
        if (dueDate == null)
        {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        long dueTime = dueDate.getTime();
        long maxInterval = days * 24L * 60L * 60L * 1000L;
        return dueTime >= currentTime && dueTime - currentTime <= maxInterval;
    }

    /**
     * 空金额转默认值
     * 
     * @param amount 金额
     * @return 默认金额
     */
    private BigDecimal defaultAmount(BigDecimal amount)
    {
        return amount == null ? BigDecimal.ZERO : amount;
    }
}
