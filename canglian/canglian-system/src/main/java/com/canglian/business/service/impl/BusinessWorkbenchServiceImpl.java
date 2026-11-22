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
import com.canglian.business.domain.FinBadDebt;
import com.canglian.business.domain.FinExpense;
import com.canglian.business.domain.FinInvoice;
import com.canglian.business.domain.FinPayable;
import com.canglian.business.domain.FinPayment;
import com.canglian.business.domain.FinReceipt;
import com.canglian.business.domain.FinReceivable;
import com.canglian.business.domain.FinWriteOff;
import com.canglian.business.domain.MdCustomer;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.MdSupplier;
import com.canglian.business.domain.MdWarehouse;
import com.canglian.business.domain.PurOrder;
import com.canglian.business.domain.SalOrder;
import com.canglian.business.domain.WmsInbound;
import com.canglian.business.domain.WmsInventoryCheck;
import com.canglian.business.domain.WmsOutbound;
import com.canglian.business.domain.WmsPurchaseReturn;
import com.canglian.business.domain.WmsSaleReturn;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsTransfer;
import com.canglian.business.mapper.FinBadDebtMapper;
import com.canglian.business.mapper.FinExpenseMapper;
import com.canglian.business.mapper.FinInvoiceMapper;
import com.canglian.business.mapper.FinPayableMapper;
import com.canglian.business.mapper.FinPaymentMapper;
import com.canglian.business.mapper.FinReceiptMapper;
import com.canglian.business.mapper.FinReceivableMapper;
import com.canglian.business.mapper.FinWriteOffMapper;
import com.canglian.business.mapper.MdCustomerMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.MdSupplierMapper;
import com.canglian.business.mapper.MdWarehouseMapper;
import com.canglian.business.mapper.PurOrderMapper;
import com.canglian.business.mapper.SalOrderMapper;
import com.canglian.business.mapper.WmsInboundMapper;
import com.canglian.business.mapper.WmsInventoryCheckMapper;
import com.canglian.business.mapper.WmsOutboundMapper;
import com.canglian.business.mapper.WmsPurchaseReturnMapper;
import com.canglian.business.mapper.WmsSaleReturnMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.mapper.WmsTransferMapper;
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

    @Autowired(required = false)
    private MdCustomerMapper mdCustomerMapper;

    @Autowired(required = false)
    private MdSupplierMapper mdSupplierMapper;

    @Autowired(required = false)
    private MdWarehouseMapper mdWarehouseMapper;

    @Autowired(required = false)
    private WmsTransferMapper wmsTransferMapper;

    @Autowired(required = false)
    private WmsInventoryCheckMapper wmsInventoryCheckMapper;

    @Autowired(required = false)
    private WmsPurchaseReturnMapper wmsPurchaseReturnMapper;

    @Autowired(required = false)
    private WmsSaleReturnMapper wmsSaleReturnMapper;

    @Autowired(required = false)
    private FinReceiptMapper finReceiptMapper;

    @Autowired(required = false)
    private FinPaymentMapper finPaymentMapper;

    @Autowired(required = false)
    private FinExpenseMapper finExpenseMapper;

    @Autowired(required = false)
    private FinInvoiceMapper finInvoiceMapper;

    @Autowired(required = false)
    private FinWriteOffMapper finWriteOffMapper;

    @Autowired(required = false)
    private FinBadDebtMapper finBadDebtMapper;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Override
    public Map<String, Object> selectWorkbenchSummary()
    {
        Map<String, Object> summaryData = new LinkedHashMap<String, Object>();

        SalOrder salQuery = new SalOrder();
        if (bizScopeHelper != null)
        {
            bizScopeHelper.applyScopeFilter(salQuery, "o", true, true);
        }
        List<SalOrder> salOrderList = safeList(salOrderMapper.selectSalOrderList(salQuery));

        PurOrder purQuery = new PurOrder();
        if (bizScopeHelper != null)
        {
            bizScopeHelper.applyScopeFilter(purQuery, "o", true, true);
        }
        List<PurOrder> purOrderList = safeList(purOrderMapper.selectPurOrderList(purQuery));

        WmsInbound inboundQuery = new WmsInbound();
        if (bizScopeHelper != null)
        {
            bizScopeHelper.applyScopeFilter(inboundQuery, "", true, true);
        }
        List<WmsInbound> wmsInboundList = safeList(wmsInboundMapper.selectWmsInboundList(inboundQuery));

        WmsOutbound outboundQuery = new WmsOutbound();
        if (bizScopeHelper != null)
        {
            bizScopeHelper.applyScopeFilter(outboundQuery, "", true, true);
        }
        List<WmsOutbound> wmsOutboundList = safeList(wmsOutboundMapper.selectWmsOutboundList(outboundQuery));

        List<FinReceivable> finReceivableList = safeList(finReceivableMapper.selectFinReceivableList(new FinReceivable()));
        List<FinPayable> finPayableList = safeList(finPayableMapper.selectFinPayableList(new FinPayable()));

        List<WmsStock> rawStocks = safeList(wmsStockMapper.selectWmsStockList(new WmsStock()));
        List<WmsStock> wmsStockList = new ArrayList<WmsStock>();
        for (WmsStock s : rawStocks)
        {
            if (bizScopeHelper == null || bizScopeHelper.canAccessRecord(null, null, s.getWarehouseId()))
            {
                wmsStockList.add(s);
            }
        }

        List<Map<String, Object>> allLowStocks = buildAggregatedLowStockList(wmsStockList);
        List<Map<String, Object>> topLowStocks = allLowStocks.size() > 10
            ? new ArrayList<Map<String, Object>>(allLowStocks.subList(0, 10)) : allLowStocks;

        List<Map<String, Object>> allPending = collectAllPendingApproveList(salOrderList, purOrderList, wmsInboundList, wmsOutboundList);
        List<Map<String, Object>> topPending = allPending.size() > 15
            ? new ArrayList<Map<String, Object>>(allPending.subList(0, 15)) : allPending;

        int draftSaleOrderCount = countOrderByBizStatus(salOrderList, "draft");
        int draftPurOrderCount = countPurchaseOrderByBizStatus(purOrderList, "draft");
        int draftInboundCount = countInboundByBizStatus(wmsInboundList, "draft");
        int draftOutboundCount = countOutboundByBizStatus(wmsOutboundList, "draft");

        summaryData.put("draftSaleOrderCount", draftSaleOrderCount);
        summaryData.put("draftPurOrderCount", draftPurOrderCount);
        summaryData.put("draftInboundCount", draftInboundCount);
        summaryData.put("draftOutboundCount", draftOutboundCount);
        summaryData.put("pendingApproveCount", allPending.size());
        summaryData.put("todayInboundCount", countTodayInbound(wmsInboundList));
        summaryData.put("todayOutboundCount", countTodayOutbound(wmsOutboundList));
        summaryData.put("dueReceivableCount", countDueReceivable(finReceivableList));
        summaryData.put("duePayableCount", countDuePayable(finPayableList));
        summaryData.put("lowStockCount", allLowStocks.size());
        summaryData.put("lowStockList", topLowStocks);
        summaryData.put("dueReceivableList", buildDueReceivableList(finReceivableList));
        summaryData.put("duePayableList", buildDuePayableList(finPayableList));
        summaryData.put("pendingApproveList", topPending);
        return summaryData;
    }

    private <T> List<T> safeList(List<T> list)
    {
        return list == null ? new ArrayList<T>() : list;
    }

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
     * 仅统计今日已审核且非红冲、未被红冲的有效入库单
     */
    private int countTodayInbound(List<WmsInbound> inboundList)
    {
        int inboundCount = 0;
        for (WmsInbound wmsInbound : inboundList)
        {
            if ("1".equals(wmsInbound.getStatus())
                && !"1".equals(wmsInbound.getIsReversal())
                && !"1".equals(wmsInbound.getReversedFlag())
                && isToday(wmsInbound.getBusinessDate()))
            {
                inboundCount++;
            }
        }
        return inboundCount;
    }

    /**
     * 仅统计今日已审核且非红冲、未被红冲的有效出库单
     */
    private int countTodayOutbound(List<WmsOutbound> outboundList)
    {
        int outboundCount = 0;
        for (WmsOutbound wmsOutbound : outboundList)
        {
            if ("1".equals(wmsOutbound.getStatus())
                && !"1".equals(wmsOutbound.getIsReversal())
                && !"1".equals(wmsOutbound.getReversedFlag())
                && isToday(wmsOutbound.getBusinessDate()))
            {
                outboundCount++;
            }
        }
        return outboundCount;
    }

    private boolean isDueOpenReceivable(FinReceivable finReceivable)
    {
        if (finReceivable == null || !"confirmed".equals(finReceivable.getBizStatus()) || "2".equals(finReceivable.getStatus()))
        {
            return false;
        }
        if ("2".equals(finReceivable.getBillDirection()) || defaultAmount(finReceivable.getAmount()).compareTo(BigDecimal.ZERO) <= 0)
        {
            return false;
        }
        BigDecimal unsettled = defaultAmount(finReceivable.getAmount())
            .subtract(defaultAmount(finReceivable.getReceivedAmount()))
            .subtract(defaultAmount(finReceivable.getBadDebtAmount()));
        return unsettled.compareTo(BigDecimal.ZERO) > 0 && isDueWithinDays(finReceivable.getDueDate(), 7);
    }

    private boolean isDueOpenPayable(FinPayable finPayable)
    {
        if (finPayable == null || !"confirmed".equals(finPayable.getBizStatus()) || "2".equals(finPayable.getStatus()))
        {
            return false;
        }
        if ("2".equals(finPayable.getBillDirection()) || defaultAmount(finPayable.getAmount()).compareTo(BigDecimal.ZERO) <= 0)
        {
            return false;
        }
        BigDecimal unsettled = defaultAmount(finPayable.getAmount())
            .subtract(defaultAmount(finPayable.getPaidAmount()));
        return unsettled.compareTo(BigDecimal.ZERO) > 0 && isDueWithinDays(finPayable.getDueDate(), 7);
    }

    private int countDueReceivable(List<FinReceivable> receivableList)
    {
        int receivableCount = 0;
        for (FinReceivable finReceivable : receivableList)
        {
            if (isDueOpenReceivable(finReceivable))
            {
                receivableCount++;
            }
        }
        return receivableCount;
    }

    private int countDuePayable(List<FinPayable> payableList)
    {
        int payableCount = 0;
        for (FinPayable finPayable : payableList)
        {
            if (isDueOpenPayable(finPayable))
            {
                payableCount++;
            }
        }
        return payableCount;
    }

    /**
     * 按（仓库, 商品）维度汇总全部批次/库位的可用库存（quantity - locked - frozen），再与预警下限比对
     */
    private List<Map<String, Object>> buildAggregatedLowStockList(List<WmsStock> stockList)
    {
        List<Map<String, Object>> lowStockList = new ArrayList<Map<String, Object>>();
        if (stockList == null || stockList.isEmpty())
        {
            return lowStockList;
        }
        Map<String, BigDecimal> availableByWhProd = new LinkedHashMap<String, BigDecimal>();
        Map<String, BigDecimal> totalQtyByWhProd = new LinkedHashMap<String, BigDecimal>();
        Map<String, BigDecimal> minWarnByWhProd = new LinkedHashMap<String, BigDecimal>();
        Map<String, Long[]> keyIds = new LinkedHashMap<String, Long[]>();
        Map<Long, MdProduct> productCacheMap = new LinkedHashMap<Long, MdProduct>();
        Map<Long, MdWarehouse> warehouseCacheMap = new LinkedHashMap<Long, MdWarehouse>();

        for (WmsStock s : stockList)
        {
            if (s.getWarehouseId() == null || s.getProductId() == null)
            {
                continue;
            }
            String key = s.getWarehouseId() + "#" + s.getProductId();
            BigDecimal qty = defaultAmount(s.getQuantity());
            BigDecimal avail = qty.subtract(defaultAmount(s.getLockedQuantity())).subtract(defaultAmount(s.getFrozenQuantity()));
            totalQtyByWhProd.put(key, defaultAmount(totalQtyByWhProd.get(key)).add(qty));
            availableByWhProd.put(key, defaultAmount(availableByWhProd.get(key)).add(avail));
            keyIds.put(key, new Long[] { s.getWarehouseId(), s.getProductId() });

            BigDecimal rowWarn = defaultAmount(s.getWarningMinQty());
            BigDecimal prevWarn = defaultAmount(minWarnByWhProd.get(key));
            if (rowWarn.compareTo(prevWarn) > 0)
            {
                minWarnByWhProd.put(key, rowWarn);
            }
        }

        for (Map.Entry<String, Long[]> entry : keyIds.entrySet())
        {
            String key = entry.getKey();
            Long warehouseId = entry.getValue()[0];
            Long productId = entry.getValue()[1];
            MdProduct product = loadProduct(productId, productCacheMap);
            BigDecimal warnMin = defaultAmount(minWarnByWhProd.get(key));
            if (warnMin.compareTo(BigDecimal.ZERO) <= 0 && product != null)
            {
                warnMin = defaultAmount(product.getWarningMinQty());
            }
            BigDecimal availableQty = defaultAmount(availableByWhProd.get(key));
            if (warnMin.compareTo(BigDecimal.ZERO) <= 0 || availableQty.compareTo(warnMin) >= 0)
            {
                continue;
            }
            MdWarehouse warehouse = loadWarehouse(warehouseId, warehouseCacheMap);
            Map<String, Object> item = new LinkedHashMap<String, Object>();
            item.put("warehouseId", warehouseId);
            item.put("warehouseName", warehouse != null ? warehouse.getWarehouseName() : ("仓库#" + warehouseId));
            item.put("productId", productId);
            item.put("productName", product != null ? product.getProductName() : ("商品#" + productId));
            item.put("quantity", defaultAmount(totalQtyByWhProd.get(key)));
            item.put("availableQuantity", availableQty);
            item.put("warningMinQty", warnMin);
            lowStockList.add(item);
        }
        return lowStockList;
    }

    private List<Map<String, Object>> buildDueReceivableList(List<FinReceivable> receivableList)
    {
        List<Map<String, Object>> dueReceivableList = new ArrayList<Map<String, Object>>();
        Map<Long, MdCustomer> customerCache = new LinkedHashMap<Long, MdCustomer>();
        for (FinReceivable finReceivable : receivableList)
        {
            if (!isDueOpenReceivable(finReceivable))
            {
                continue;
            }
            MdCustomer customer = loadCustomer(finReceivable.getCustomerId(), customerCache);
            BigDecimal unsettled = defaultAmount(finReceivable.getAmount())
                .subtract(defaultAmount(finReceivable.getReceivedAmount()))
                .subtract(defaultAmount(finReceivable.getBadDebtAmount()));
            Map<String, Object> dueReceivableItem = new LinkedHashMap<String, Object>();
            dueReceivableItem.put("receivableId", finReceivable.getReceivableId());
            dueReceivableItem.put("receivableNo", finReceivable.getReceivableNo());
            dueReceivableItem.put("customerId", finReceivable.getCustomerId());
            dueReceivableItem.put("customerName", customer != null ? customer.getCustomerName() : ("客户#" + finReceivable.getCustomerId()));
            dueReceivableItem.put("amount", finReceivable.getAmount());
            dueReceivableItem.put("receivedAmount", finReceivable.getReceivedAmount());
            dueReceivableItem.put("unsettledAmount", unsettled);
            dueReceivableItem.put("dueDate", finReceivable.getDueDate());
            dueReceivableList.add(dueReceivableItem);
            if (dueReceivableList.size() >= 10)
            {
                break;
            }
        }
        return dueReceivableList;
    }

    private List<Map<String, Object>> buildDuePayableList(List<FinPayable> payableList)
    {
        List<Map<String, Object>> duePayableList = new ArrayList<Map<String, Object>>();
        Map<Long, MdSupplier> supplierCache = new LinkedHashMap<Long, MdSupplier>();
        for (FinPayable finPayable : payableList)
        {
            if (!isDueOpenPayable(finPayable))
            {
                continue;
            }
            MdSupplier supplier = loadSupplier(finPayable.getSupplierId(), supplierCache);
            BigDecimal unsettled = defaultAmount(finPayable.getAmount())
                .subtract(defaultAmount(finPayable.getPaidAmount()));
            Map<String, Object> duePayableItem = new LinkedHashMap<String, Object>();
            duePayableItem.put("payableId", finPayable.getPayableId());
            duePayableItem.put("payableNo", finPayable.getPayableNo());
            duePayableItem.put("supplierId", finPayable.getSupplierId());
            duePayableItem.put("supplierName", supplier != null ? supplier.getSupplierName() : ("供应商#" + finPayable.getSupplierId()));
            duePayableItem.put("amount", finPayable.getAmount());
            duePayableItem.put("paidAmount", finPayable.getPaidAmount());
            duePayableItem.put("unsettledAmount", unsettled);
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
     * 汇总全部 12 类单据的草稿待审待办，按业务日期降序排列并附带跳转路由
     */
    private List<Map<String, Object>> collectAllPendingApproveList(List<SalOrder> salOrderList, List<PurOrder> purOrderList,
        List<WmsInbound> inboundList, List<WmsOutbound> outboundList)
    {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (SalOrder o : salOrderList)
        {
            if ("draft".equals(o.getBizStatus()))
            {
                appendPendingApproveItem(list, "saleOrder", "销售单据", o.getOrderId(), o.getOrderNo(), o.getBusinessDate(), "/business/saleOrder");
            }
        }
        for (PurOrder o : purOrderList)
        {
            if ("draft".equals(o.getBizStatus()))
            {
                appendPendingApproveItem(list, "purOrder", "购货订单", o.getPurchaseOrderId(), o.getPurchaseOrderNo(), o.getBusinessDate(), "/business/purOrder");
            }
        }
        for (WmsInbound o : inboundList)
        {
            if ("draft".equals(o.getBizStatus()))
            {
                appendPendingApproveItem(list, "inbound", "入库单", o.getInboundId(), o.getInboundNo(), o.getBusinessDate(), "/wms/inbound");
            }
        }
        for (WmsOutbound o : outboundList)
        {
            if ("draft".equals(o.getBizStatus()))
            {
                appendPendingApproveItem(list, "outbound", "出库单", o.getOutboundId(), o.getOutboundNo(), o.getBusinessDate(), "/wms/outbound");
            }
        }
        if (wmsTransferMapper != null)
        {
            WmsTransfer q = new WmsTransfer();
            q.setStatus("0");
            if (bizScopeHelper != null)
            {
                bizScopeHelper.applyScopeFilter(q, "", true, false);
            }
            for (WmsTransfer t : safeList(wmsTransferMapper.selectWmsTransferList(q)))
            {
                if (bizScopeHelper == null || (bizScopeHelper.canAccessRecord(t.getDeptId(), t.getOwnerUserId(), t.getOutWarehouseId())
                    && bizScopeHelper.canAccessRecord(t.getDeptId(), t.getOwnerUserId(), t.getInWarehouseId())))
                {
                    appendPendingApproveItem(list, "transfer", "调拨单", t.getTransferId(), t.getTransferNo(),
                        t.getBusinessDate() != null ? t.getBusinessDate() : t.getCreateTime(), "/wms/transfer");
                }
            }
        }
        if (wmsInventoryCheckMapper != null)
        {
            WmsInventoryCheck q = new WmsInventoryCheck();
            q.setStatus("0");
            if (bizScopeHelper != null)
            {
                bizScopeHelper.applyScopeFilter(q, "", true, true);
            }
            for (WmsInventoryCheck ic : safeList(wmsInventoryCheckMapper.selectWmsInventoryCheckList(q)))
            {
                appendPendingApproveItem(list, "inventoryCheck", "盘点单", ic.getCheckId(), ic.getCheckNo(),
                    ic.getBusinessDate() != null ? ic.getBusinessDate() : ic.getCreateTime(), "/wms/inventoryCheck");
            }
        }
        if (wmsPurchaseReturnMapper != null)
        {
            WmsPurchaseReturn q = new WmsPurchaseReturn();
            q.setStatus("0");
            if (bizScopeHelper != null)
            {
                bizScopeHelper.applyScopeFilter(q, "", true, true);
            }
            for (WmsPurchaseReturn pr : safeList(wmsPurchaseReturnMapper.selectWmsPurchaseReturnList(q)))
            {
                appendPendingApproveItem(list, "purchaseReturn", "采购退货", pr.getPurchaseReturnId(), pr.getReturnNo(),
                    pr.getBusinessDate(), "/wms/purchaseReturn");
            }
        }
        if (wmsSaleReturnMapper != null)
        {
            WmsSaleReturn q = new WmsSaleReturn();
            q.setStatus("0");
            if (bizScopeHelper != null)
            {
                bizScopeHelper.applyScopeFilter(q, "", true, true);
            }
            for (WmsSaleReturn sr : safeList(wmsSaleReturnMapper.selectWmsSaleReturnList(q)))
            {
                appendPendingApproveItem(list, "saleReturn", "销售退货", sr.getSaleReturnId(), sr.getReturnNo(),
                    sr.getBusinessDate(), "/wms/saleReturn");
            }
        }
        if (finReceiptMapper != null)
        {
            FinReceipt q = new FinReceipt();
            q.setStatus("0");
            if (bizScopeHelper != null)
            {
                bizScopeHelper.applyScopeFilter(q, "", true, false);
            }
            for (FinReceipt r : safeList(finReceiptMapper.selectFinReceiptList(q)))
            {
                appendPendingApproveItem(list, "receipt", "收款单", r.getReceiptId(), r.getReceiptNo(),
                    r.getReceiptDate(), "/finance/receipt");
            }
        }
        if (finPaymentMapper != null)
        {
            FinPayment q = new FinPayment();
            q.setStatus("0");
            if (bizScopeHelper != null)
            {
                bizScopeHelper.applyScopeFilter(q, "", true, false);
            }
            for (FinPayment p : safeList(finPaymentMapper.selectFinPaymentList(q)))
            {
                appendPendingApproveItem(list, "payment", "付款单", p.getPaymentId(), p.getPaymentNo(),
                    p.getPaymentDate(), "/finance/payment");
            }
        }
        if (finExpenseMapper != null)
        {
            FinExpense q = new FinExpense();
            q.setStatus("0");
            for (FinExpense e : safeList(finExpenseMapper.selectFinExpenseList(q)))
            {
                appendPendingApproveItem(list, "expense", "费用单", e.getExpenseId(), e.getExpenseNo(),
                    e.getExpenseDate(), "/finance/expense");
            }
        }
        if (finInvoiceMapper != null)
        {
            FinInvoice q = new FinInvoice();
            q.setStatus("0");
            if (bizScopeHelper != null)
            {
                bizScopeHelper.applyScopeFilter(q, "", true, false);
            }
            for (FinInvoice inv : safeList(finInvoiceMapper.selectFinInvoiceList(q)))
            {
                String invoiceRoute = "purchase".equals(inv.getInvoiceType()) ? "/finance/purchaseInvoice" : "/finance/salesInvoice";
                appendPendingApproveItem(list, "invoice", "发票单", inv.getInvoiceId(), inv.getInvoiceNo(),
                    inv.getInvoiceDate() != null ? inv.getInvoiceDate() : inv.getBusinessDate(), invoiceRoute);
            }
        }
        if (finWriteOffMapper != null)
        {
            FinWriteOff q = new FinWriteOff();
            q.setStatus("0");
            for (FinWriteOff w : safeList(finWriteOffMapper.selectFinWriteOffList(q)))
            {
                appendPendingApproveItem(list, "writeOff", "核销单", w.getWriteOffId(), w.getWriteOffNo(),
                    w.getWriteOffDate(), "/finance/writeOff");
            }
        }
        if (finBadDebtMapper != null)
        {
            FinBadDebt q = new FinBadDebt();
            q.setStatus("0");
            if (bizScopeHelper != null)
            {
                bizScopeHelper.applyScopeFilter(q, "", true, false);
            }
            for (FinBadDebt bd : safeList(finBadDebtMapper.selectFinBadDebtList(q)))
            {
                appendPendingApproveItem(list, "badDebt", "坏账单", bd.getBadDebtId(), bd.getBadDebtNo(),
                    bd.getBadDebtDate(), "/finance/badDebt");
            }
        }

        list.sort((a, b) -> {
            Date da = (Date) a.get("businessDate");
            Date db = (Date) b.get("businessDate");
            if (da == null && db == null)
            {
                return 0;
            }
            if (da == null)
            {
                return 1;
            }
            if (db == null)
            {
                return -1;
            }
            return db.compareTo(da);
        });
        return list;
    }

    private void appendPendingApproveItem(List<Map<String, Object>> pendingApproveList, String billType,
        String billTypeLabel, Long billId, String billNo, Date businessDate, String route)
    {
        Map<String, Object> pendingApproveItem = new LinkedHashMap<String, Object>();
        pendingApproveItem.put("billType", billType);
        pendingApproveItem.put("billTypeLabel", billTypeLabel);
        pendingApproveItem.put("billId", billId);
        pendingApproveItem.put("billNo", billNo);
        pendingApproveItem.put("businessDate", businessDate);
        pendingApproveItem.put("route", route);
        pendingApproveList.add(pendingApproveItem);
    }

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

    private MdWarehouse loadWarehouse(Long warehouseId, Map<Long, MdWarehouse> warehouseCacheMap)
    {
        if (warehouseId == null || mdWarehouseMapper == null)
        {
            return null;
        }
        if (!warehouseCacheMap.containsKey(warehouseId))
        {
            warehouseCacheMap.put(warehouseId, mdWarehouseMapper.selectMdWarehouseById(warehouseId));
        }
        return warehouseCacheMap.get(warehouseId);
    }

    private MdCustomer loadCustomer(Long customerId, Map<Long, MdCustomer> customerCache)
    {
        if (customerId == null || mdCustomerMapper == null)
        {
            return null;
        }
        if (!customerCache.containsKey(customerId))
        {
            customerCache.put(customerId, mdCustomerMapper.selectMdCustomerById(customerId));
        }
        return customerCache.get(customerId);
    }

    private MdSupplier loadSupplier(Long supplierId, Map<Long, MdSupplier> supplierCache)
    {
        if (supplierId == null || mdSupplierMapper == null)
        {
            return null;
        }
        if (!supplierCache.containsKey(supplierId))
        {
            supplierCache.put(supplierId, mdSupplierMapper.selectMdSupplierById(supplierId));
        }
        return supplierCache.get(supplierId);
    }

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

    private boolean isDueWithinDays(Date dueDate, int days)
    {
        if (dueDate == null)
        {
            return false;
        }
        Calendar maxCalendar = Calendar.getInstance();
        maxCalendar.add(Calendar.DAY_OF_MONTH, days);
        maxCalendar.set(Calendar.HOUR_OF_DAY, 23);
        maxCalendar.set(Calendar.MINUTE, 59);
        maxCalendar.set(Calendar.SECOND, 59);
        maxCalendar.set(Calendar.MILLISECOND, 999);
        return dueDate.getTime() <= maxCalendar.getTimeInMillis();
    }

    private BigDecimal defaultAmount(BigDecimal amount)
    {
        return amount == null ? BigDecimal.ZERO : amount;
    }
}
