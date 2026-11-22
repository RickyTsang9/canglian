package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.canglian.business.domain.FinCostCalculate;
import com.canglian.business.domain.FinPayable;
import com.canglian.business.domain.MdLocation;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.MdSupplier;
import com.canglian.business.domain.MdWarehouse;
import com.canglian.business.domain.WmsInboundItem;
import com.canglian.business.domain.WmsPurchaseReturn;
import com.canglian.business.domain.WmsPurchaseReturnItem;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsStockLog;
import com.canglian.business.mapper.FinPayableMapper;
import com.canglian.business.mapper.MdLocationMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.MdSupplierMapper;
import com.canglian.business.mapper.MdWarehouseMapper;
import com.canglian.business.mapper.WmsInboundItemMapper;
import com.canglian.business.mapper.WmsPurchaseReturnItemMapper;
import com.canglian.business.mapper.WmsPurchaseReturnMapper;
import com.canglian.business.mapper.WmsStockLogMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IFinCostCalculateService;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IFinPayableService;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.business.service.IMdProductUnitService;
import com.canglian.business.service.IWmsPurchaseReturnService;
import com.canglian.business.service.IWmsSerialNoService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 采购退货 服务层实现
 *
 * @author canglian
 */
@Service
public class WmsPurchaseReturnServiceImpl implements IWmsPurchaseReturnService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private WmsPurchaseReturnMapper wmsPurchaseReturnMapper;

    @Autowired
    private WmsPurchaseReturnItemMapper wmsPurchaseReturnItemMapper;

    @Autowired
    private WmsStockMapper wmsStockMapper;

    @Autowired
    private WmsStockLogMapper wmsStockLogMapper;

    @Autowired
    private WmsInboundItemMapper wmsInboundItemMapper;

    @Autowired(required = false)
    private FinPayableMapper finPayableMapper;

    @Autowired(required = false)
    private MdProductMapper mdProductMapper;

    @Autowired
    private IFinGeneralLedgerService finGeneralLedgerService;

    @Autowired
    private IFinVoucherEventService finVoucherEventService;

    @Autowired
    private IFinCostCalculateService finCostCalculateService;

    @Autowired
    private IFinPayableService finPayableService;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Autowired(required = false)
    private MdSupplierMapper mdSupplierMapper;

    @Autowired(required = false)
    private MdWarehouseMapper mdWarehouseMapper;

    @Autowired(required = false)
    private MdLocationMapper mdLocationMapper;

    @Autowired(required = false)
    private IMdProductUnitService mdProductUnitService;

    @Autowired(required = false)
    private IWmsSerialNoService wmsSerialNoService;

    @Override
    public WmsPurchaseReturn selectWmsPurchaseReturnById(Long purchaseReturnId)
    {
        WmsPurchaseReturn doc = wmsPurchaseReturnMapper.selectWmsPurchaseReturnById(purchaseReturnId);
        if (doc != null)
        {
            if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(doc.getDeptId(), doc.getOwnerUserId(), doc.getWarehouseId());
            WmsPurchaseReturnItem query = new WmsPurchaseReturnItem();
            query.setPurchaseReturnId(purchaseReturnId);
            doc.setItemList(wmsPurchaseReturnItemMapper.selectWmsPurchaseReturnItemList(query));
        }
        return doc;
    }

    @Override
    public List<WmsPurchaseReturn> selectWmsPurchaseReturnList(WmsPurchaseReturn wmsPurchaseReturn)
    {
        if (bizScopeHelper != null) bizScopeHelper.applyScopeFilter(wmsPurchaseReturn, "", true, true);
        return wmsPurchaseReturnMapper.selectWmsPurchaseReturnList(wmsPurchaseReturn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWmsPurchaseReturn(WmsPurchaseReturn wmsPurchaseReturn)
    {
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(wmsPurchaseReturn.getWarehouseId());
            if (wmsPurchaseReturn.getDeptId() == null) wmsPurchaseReturn.setDeptId(bizScopeHelper.currentDeptId());
            if (wmsPurchaseReturn.getOwnerUserId() == null) wmsPurchaseReturn.setOwnerUserId(bizScopeHelper.currentUserId());
        }
        if (StringUtils.isEmpty(wmsPurchaseReturn.getReturnNo())) wmsPurchaseReturn.setReturnNo(generateReturnNo());
        if (wmsPurchaseReturn.getBusinessDate() == null) wmsPurchaseReturn.setBusinessDate(new Date());
        if (finGeneralLedgerService != null) finGeneralLedgerService.assertPeriodOpen(wmsPurchaseReturn.getBusinessDate());
        validateActiveMasterRefs(wmsPurchaseReturn);
        wmsPurchaseReturn.setTotalQty(BigDecimal.ZERO);
        wmsPurchaseReturn.setTotalAmount(BigDecimal.ZERO);
        wmsPurchaseReturn.setStatus("0");
        wmsPurchaseReturn.setBizStatus("draft");
        wmsPurchaseReturn.setIsReversal("0");
        wmsPurchaseReturn.setReversedFlag("0");
        wmsPurchaseReturn.setAuditBy(null);
        wmsPurchaseReturn.setAuditTime(null);
        int rows = wmsPurchaseReturnMapper.insertWmsPurchaseReturn(wmsPurchaseReturn);
        if (rows > 0 && wmsPurchaseReturn.getItemList() != null && !wmsPurchaseReturn.getItemList().isEmpty())
        {
            saveReturnItemsAndRecalculate(wmsPurchaseReturn);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWmsPurchaseReturn(WmsPurchaseReturn wmsPurchaseReturn)
    {
        WmsPurchaseReturn purchaseReturn = getExistingPurchaseReturn(wmsPurchaseReturn.getPurchaseReturnId());
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(purchaseReturn.getDeptId(), purchaseReturn.getOwnerUserId(), purchaseReturn.getWarehouseId());
            bizScopeHelper.assertWarehouseAccess(wmsPurchaseReturn.getWarehouseId());
        }
        validatePurchaseReturnEditable(purchaseReturn);
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(purchaseReturn.getBusinessDate());
            if (wmsPurchaseReturn.getBusinessDate() != null)
            {
                finGeneralLedgerService.assertPeriodOpen(wmsPurchaseReturn.getBusinessDate());
            }
        }
        if (wmsPurchaseReturn.getWarehouseId() == null) wmsPurchaseReturn.setWarehouseId(purchaseReturn.getWarehouseId());
        validateActiveMasterRefs(wmsPurchaseReturn);
        wmsPurchaseReturn.setStatus(purchaseReturn.getStatus());
        wmsPurchaseReturn.setAuditBy(purchaseReturn.getAuditBy());
        wmsPurchaseReturn.setAuditTime(purchaseReturn.getAuditTime());
        wmsPurchaseReturn.setTotalQty(purchaseReturn.getTotalQty());
        wmsPurchaseReturn.setTotalAmount(purchaseReturn.getTotalAmount());
        wmsPurchaseReturn.setBizStatus(purchaseReturn.getBizStatus());
        int rows = wmsPurchaseReturnMapper.updateWmsPurchaseReturn(wmsPurchaseReturn);
        if (rows > 0 && wmsPurchaseReturn.getItemList() != null)
        {
            wmsPurchaseReturnItemMapper.deleteWmsPurchaseReturnItemByPurchaseReturnId(wmsPurchaseReturn.getPurchaseReturnId());
            saveReturnItemsAndRecalculate(wmsPurchaseReturn);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsPurchaseReturnById(Long purchaseReturnId)
    {
        WmsPurchaseReturn doc = checkPurchaseReturnDeletable(purchaseReturnId);
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(doc.getDeptId(), doc.getOwnerUserId(), doc.getWarehouseId());
        if (finGeneralLedgerService != null) finGeneralLedgerService.assertPeriodOpen(doc.getBusinessDate());
        wmsPurchaseReturnItemMapper.deleteWmsPurchaseReturnItemByPurchaseReturnId(purchaseReturnId);
        return wmsPurchaseReturnMapper.deleteWmsPurchaseReturnById(purchaseReturnId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsPurchaseReturnByIds(Long[] purchaseReturnIds)
    {
        for (Long id : purchaseReturnIds)
        {
            WmsPurchaseReturn doc = checkPurchaseReturnDeletable(id);
            if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(doc.getDeptId(), doc.getOwnerUserId(), doc.getWarehouseId());
            if (finGeneralLedgerService != null) finGeneralLedgerService.assertPeriodOpen(doc.getBusinessDate());
        }
        for (Long id : purchaseReturnIds) wmsPurchaseReturnItemMapper.deleteWmsPurchaseReturnItemByPurchaseReturnId(id);
        return wmsPurchaseReturnMapper.deleteWmsPurchaseReturnByIds(purchaseReturnIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int auditWmsPurchaseReturn(Long purchaseReturnId, String operator)
    {
        WmsPurchaseReturn purchaseReturn = wmsPurchaseReturnMapper.selectWmsPurchaseReturnByIdForUpdate(purchaseReturnId);
        if (purchaseReturn == null) throw new ServiceException("采购退货单不存在");
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(purchaseReturn.getDeptId(), purchaseReturn.getOwnerUserId(), purchaseReturn.getWarehouseId());
        if (!"0".equals(purchaseReturn.getStatus())) throw new ServiceException("采购退货单状态已变更，无法审核");
        finGeneralLedgerService.assertPeriodOpen(purchaseReturn.getBusinessDate());
        if (purchaseReturn.getWarehouseId() == null) throw new ServiceException("退货仓库不能为空");
        validateActiveMasterRefs(purchaseReturn);
        WmsPurchaseReturnItem itemQuery = new WmsPurchaseReturnItem();
        itemQuery.setPurchaseReturnId(purchaseReturnId);
        List<WmsPurchaseReturnItem> itemList = wmsPurchaseReturnItemMapper.selectWmsPurchaseReturnItemList(itemQuery);
        if (itemList == null || itemList.isEmpty()) throw new ServiceException("采购退货明细不能为空");
        validateSourceInboundQty(purchaseReturn, itemList);

        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (WmsPurchaseReturnItem returnItem : itemList)
        {
            BigDecimal itemQuantity = defaultBigDecimal(returnItem.getQuantity());
            if (itemQuantity.compareTo(BigDecimal.ZERO) <= 0) throw new ServiceException("退货数量必须大于0");
            if (returnItem.getProductId() == null) throw new ServiceException("退货明细商品不能为空");
            validateLocationBelongsToWarehouse(returnItem.getLocationId(), purchaseReturn.getWarehouseId());
            MdProduct product = mdProductMapper != null ? mdProductMapper.selectMdProductById(returnItem.getProductId()) : null;
            BigDecimal baseQty = resolveBaseQuantity(returnItem);
            Long locationId = defaultLocationId(returnItem.getLocationId());
            String batchNo = defaultBatchNo(returnItem.getBatchNo());
            BigDecimal itemPrice = defaultBigDecimal(returnItem.getPrice());
            BigDecimal itemAmount = returnItem.getAmount();
            if (itemAmount == null || itemAmount.compareTo(BigDecimal.ZERO) <= 0)
            {
                itemAmount = itemPrice.multiply(itemQuantity).setScale(2, RoundingMode.HALF_UP);
            }
            BigDecimal baseUnitCost = baseQty.compareTo(BigDecimal.ZERO) > 0
                ? itemAmount.divide(baseQty, 4, RoundingMode.HALF_UP) : itemPrice;

            // 序列号强管控：启用序列号商品必须录满足额在库 SN 并置为已退供应商
            if (wmsSerialNoService != null)
            {
                wmsSerialNoService.handlePurchaseReturnSerials(product, purchaseReturn.getWarehouseId(),
                    purchaseReturnId, purchaseReturn.getReturnNo(), returnItem.getSnCodes(), baseQty, operator);
            }

            WmsStock stockQuery = new WmsStock();
            stockQuery.setWarehouseId(purchaseReturn.getWarehouseId());
            stockQuery.setProductId(returnItem.getProductId());
            stockQuery.setLocationId(locationId);
            stockQuery.setBatchNo(batchNo);
            WmsStock exactStock = wmsStockMapper.selectWmsStockByKeyForUpdate(stockQuery);
            if (exactStock != null && calculateAvailableQuantity(exactStock).compareTo(baseQty) >= 0)
            {
                deductSingleStockRow(exactStock, purchaseReturnId, baseQty, baseUnitCost, itemAmount, operator);
                JSONArray trace = new JSONArray();
                JSONObject entry = new JSONObject();
                entry.put("locationId", defaultLocationId(exactStock.getLocationId()));
                entry.put("batchNo", defaultBatchNo(exactStock.getBatchNo()));
                entry.put("qty", baseQty.toPlainString());
                trace.add(entry);
                returnItem.setDeductTrace(trace.toJSONString());
                wmsPurchaseReturnItemMapper.updateWmsPurchaseReturnItem(returnItem);
            }
            else if (locationId.equals(0L) && batchNo.isEmpty())
            {
                String traceJson = deductAcrossWarehouseStock(purchaseReturn.getWarehouseId(), returnItem.getProductId(),
                    purchaseReturnId, baseQty, baseUnitCost, operator);
                returnItem.setDeductTrace(traceJson);
                wmsPurchaseReturnItemMapper.updateWmsPurchaseReturnItem(returnItem);
            }
            else
            {
                throw new ServiceException("指定库位/批次的可用库存不足，无法退货");
            }
            recordReturnCost(purchaseReturn.getWarehouseId(), returnItem.getProductId(), purchaseReturnId, "purchase_return", "2", baseQty, baseUnitCost);
            totalQuantity = totalQuantity.add(itemQuantity);
            totalAmount = totalAmount.add(itemAmount);
        }
        purchaseReturn.setStatus("1");
        purchaseReturn.setAuditBy(operator);
        purchaseReturn.setAuditTime(new Date());
        purchaseReturn.setUpdateBy(operator);
        purchaseReturn.setTotalQty(totalQuantity);
        purchaseReturn.setTotalAmount(totalAmount);
        purchaseReturn.setBizStatus("completed");
        int rows = wmsPurchaseReturnMapper.updateWmsPurchaseReturn(purchaseReturn);
        if (rows > 0)
        {
            if (purchaseReturn.getSupplierId() != null && totalAmount.compareTo(BigDecimal.ZERO) > 0)
            {
                FinPayable redAp = finPayableService.createRedPayableForReturn(
                    purchaseReturn.getSupplierId(), "purchase_return", purchaseReturn.getPurchaseReturnId(), purchaseReturn.getReturnNo(),
                    purchaseReturn.getBusinessDate(), totalAmount, operator, "采购退货自动生成红字应付冲减单");
                if (redAp != null && redAp.getPayableId() != null)
                {
                    purchaseReturn.setPayableId(redAp.getPayableId());
                    wmsPurchaseReturnMapper.updateWmsPurchaseReturn(purchaseReturn);
                }
            }
            finVoucherEventService.recordVoucherEvent("purchase_return", purchaseReturn.getPurchaseReturnId(), purchaseReturn.getReturnNo(),
                "purchase_return_complete", purchaseReturn.getBusinessDate(), purchaseReturn.getTotalAmount(), operator, "采购退货完成待生成凭证");
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unauditWmsPurchaseReturn(Long purchaseReturnId, String operator)
    {
        WmsPurchaseReturn doc = wmsPurchaseReturnMapper.selectWmsPurchaseReturnByIdForUpdate(purchaseReturnId);
        if (doc == null) throw new ServiceException("采购退货单不存在");
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(doc.getDeptId(), doc.getOwnerUserId(), doc.getWarehouseId());
        if (!"1".equals(doc.getStatus())) throw new ServiceException("仅已审核采购退货单允许反审核");
        if ("1".equals(doc.getIsReversal())) throw new ServiceException("红冲单据不允许反审核");
        if ("1".equals(doc.getReversedFlag())) throw new ServiceException("该采购退货单已被红冲，禁止反审核");
        finGeneralLedgerService.assertPeriodOpen(doc.getBusinessDate());
        finVoucherEventService.cancelPendingVoucherEvents("purchase_return", purchaseReturnId);

        if (doc.getPayableId() != null && finPayableMapper != null)
        {
            FinPayable redAp = finPayableMapper.selectFinPayableById(doc.getPayableId());
            if (redAp != null)
            {
                if (defaultBigDecimal(redAp.getPaidAmount()).compareTo(BigDecimal.ZERO) != 0)
                {
                    throw new ServiceException("关联红字应付单已对冲结算，禁止反审核，请使用红冲功能");
                }
                finPayableMapper.deleteFinPayableById(redAp.getPayableId());
            }
        }

        WmsPurchaseReturnItem q = new WmsPurchaseReturnItem();
        q.setPurchaseReturnId(purchaseReturnId);
        List<WmsPurchaseReturnItem> items = wmsPurchaseReturnItemMapper.selectWmsPurchaseReturnItemList(q);
        if (items != null)
        {
            for (WmsPurchaseReturnItem item : items)
            {
                MdProduct product = mdProductMapper != null ? mdProductMapper.selectMdProductById(item.getProductId()) : null;
                if (wmsSerialNoService != null)
                {
                    wmsSerialNoService.rollbackPurchaseReturnSerials(product, doc.getWarehouseId(),
                        defaultLocationId(item.getLocationId()), purchaseReturnId, doc.getReturnNo(), item.getSnCodes(), operator);
                }
                restoreStockForReverse(doc.getWarehouseId(), item, "purchase_return_unaudit", purchaseReturnId, operator);
            }
        }

        doc.setStatus("0");
        doc.setBizStatus("draft");
        doc.setPayableId(null);
        doc.setAuditBy("");
        doc.setAuditTime(null);
        doc.setUnauditBy(operator);
        doc.setUnauditTime(new Date());
        doc.setUpdateBy(operator);
        return wmsPurchaseReturnMapper.updateWmsPurchaseReturn(doc);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsPurchaseReturn reverseWmsPurchaseReturn(Long purchaseReturnId, String operator)
    {
        WmsPurchaseReturn origin = wmsPurchaseReturnMapper.selectWmsPurchaseReturnByIdForUpdate(purchaseReturnId);
        if (origin == null) throw new ServiceException("采购退货单不存在");
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(origin.getDeptId(), origin.getOwnerUserId(), origin.getWarehouseId());
        if (!"1".equals(origin.getStatus())) throw new ServiceException("仅已审核采购退货单允许红冲");
        if ("1".equals(origin.getIsReversal())) throw new ServiceException("红冲单据不允许再次红冲");
        if ("1".equals(origin.getReversedFlag())) throw new ServiceException("该采购退货单已被红冲，请勿重复红冲");
        finGeneralLedgerService.assertPeriodOpen(new Date());

        WmsPurchaseReturnItem q = new WmsPurchaseReturnItem();
        q.setPurchaseReturnId(purchaseReturnId);
        List<WmsPurchaseReturnItem> originItems = wmsPurchaseReturnItemMapper.selectWmsPurchaseReturnItemList(q);
        if (originItems == null || originItems.isEmpty()) throw new ServiceException("原采购退货单无明细，无法红冲");

        WmsPurchaseReturn reversal = new WmsPurchaseReturn();
        reversal.setReturnNo(generateReturnNo());
        reversal.setReturnType(origin.getReturnType());
        reversal.setSupplierId(origin.getSupplierId());
        reversal.setWarehouseId(origin.getWarehouseId());
        reversal.setDeptId(origin.getDeptId());
        reversal.setOwnerUserId(origin.getOwnerUserId());
        reversal.setSourceBillType("purchase_return_reverse");
        reversal.setSourceBillId(origin.getPurchaseReturnId());
        reversal.setSourceBillNo(origin.getReturnNo());
        reversal.setBusinessDate(new Date());
        reversal.setTotalQty(defaultBigDecimal(origin.getTotalQty()).negate());
        reversal.setTotalAmount(defaultBigDecimal(origin.getTotalAmount()).negate());
        reversal.setStatus("1");
        reversal.setBizStatus("completed");
        reversal.setAuditBy(operator);
        reversal.setAuditTime(new Date());
        reversal.setIsReversal("1");
        reversal.setOriginBillId(origin.getPurchaseReturnId());
        reversal.setReversedFlag("0");
        reversal.setCreateBy(operator);
        reversal.setRemark("红冲原采购退货单：" + origin.getReturnNo());
        wmsPurchaseReturnMapper.insertWmsPurchaseReturn(reversal);

        for (WmsPurchaseReturnItem oi : originItems)
        {
            MdProduct product = mdProductMapper != null ? mdProductMapper.selectMdProductById(oi.getProductId()) : null;
            if (wmsSerialNoService != null)
            {
                wmsSerialNoService.rollbackPurchaseReturnSerials(product, origin.getWarehouseId(),
                    defaultLocationId(oi.getLocationId()), origin.getPurchaseReturnId(), reversal.getReturnNo(), oi.getSnCodes(), operator);
            }
            restoreStockForReverse(origin.getWarehouseId(), oi, "purchase_return_reverse", reversal.getPurchaseReturnId(), operator);
            WmsPurchaseReturnItem ri = new WmsPurchaseReturnItem();
            ri.setPurchaseReturnId(reversal.getPurchaseReturnId());
            ri.setProductId(oi.getProductId());
            ri.setLocationId(defaultLocationId(oi.getLocationId()));
            ri.setBatchNo(defaultBatchNo(oi.getBatchNo()));
            ri.setUnitName(oi.getUnitName());
            ri.setConvertRatio(oi.getConvertRatio());
            ri.setBaseQuantity(resolveBaseQuantity(oi).negate());
            ri.setQuantity(defaultBigDecimal(oi.getQuantity()).negate());
            ri.setPrice(oi.getPrice());
            ri.setAmount(defaultBigDecimal(oi.getAmount()).negate());
            ri.setSnCodes(oi.getSnCodes());
            wmsPurchaseReturnItemMapper.insertWmsPurchaseReturnItem(ri);
        }

        origin.setReversedFlag("1");
        origin.setUpdateBy(operator);
        wmsPurchaseReturnMapper.updateWmsPurchaseReturn(origin);

        // 红冲采购退货单时同步补回正数蓝字应付，抵消原退货单生成的红字应付
        if (origin.getSupplierId() != null && defaultBigDecimal(origin.getTotalAmount()).compareTo(BigDecimal.ZERO) > 0)
        {
            FinPayable compAp = new FinPayable();
            compAp.setSupplierId(origin.getSupplierId());
            compAp.setBillType("purchase_return_reverse");
            compAp.setBillId(reversal.getPurchaseReturnId());
            compAp.setSourceBillType("purchase_return_reverse");
            compAp.setSourceBillId(reversal.getPurchaseReturnId());
            compAp.setSourceBillNo(reversal.getReturnNo());
            compAp.setBusinessDate(new Date());
            compAp.setAmount(origin.getTotalAmount());
            compAp.setDueDate(resolvePayableDueDate(origin.getSupplierId(), new Date()));
            compAp.setCreateBy(operator);
            compAp.setRemark("红冲采购退货单 " + origin.getReturnNo() + " 自动补回蓝字应付");
            finPayableService.insertFinPayable(compAp);
            finPayableService.auditFinPayable(compAp.getPayableId(), operator);
            reversal.setPayableId(compAp.getPayableId());
            wmsPurchaseReturnMapper.updateWmsPurchaseReturn(reversal);
        }
        finVoucherEventService.recordVoucherEvent("purchase_return", reversal.getPurchaseReturnId(), reversal.getReturnNo(),
            "inbound_complete", new Date(), origin.getTotalAmount(), operator, "采购退货红冲待生成冲销凭证");
        return selectWmsPurchaseReturnById(reversal.getPurchaseReturnId());
    }

    private void restoreStockForReverse(Long warehouseId, WmsPurchaseReturnItem item, String billType, Long billId, String operator)
    {
        BigDecimal totalBaseQty = resolveBaseQuantity(item).abs();
        BigDecimal itemAmount = defaultBigDecimal(item.getAmount()).abs();
        BigDecimal baseUnitCost = totalBaseQty.compareTo(BigDecimal.ZERO) > 0
            ? itemAmount.divide(totalBaseQty, 4, RoundingMode.HALF_UP) : defaultBigDecimal(item.getPrice());

        // 若审核时记录了整仓扣减轨迹，则逐笔精准回补原库位与原批次，彻底杜绝幻影库存行
        if (StringUtils.isNotEmpty(item.getDeductTrace()))
        {
            try
            {
                JSONArray trace = JSON.parseArray(item.getDeductTrace());
                if (trace != null && !trace.isEmpty())
                {
                    for (int i = 0; i < trace.size(); i++)
                    {
                        JSONObject row = trace.getJSONObject(i);
                        Long locId = row.getLong("locationId") == null ? 0L : row.getLong("locationId");
                        String batchNo = defaultBatchNo(row.getString("batchNo"));
                        BigDecimal partQty = new BigDecimal(row.getString("qty")).abs();
                        if (partQty.compareTo(BigDecimal.ZERO) <= 0) continue;
                        restoreSingleStockRow(warehouseId, item.getProductId(), locId, batchNo, partQty, baseUnitCost, billType, billId, operator);
                    }
                    recordReturnCost(warehouseId, item.getProductId(), billId, billType, "1", totalBaseQty, baseUnitCost);
                    return;
                }
            }
            catch (Exception ignored)
            {
            }
        }
        Long locId = defaultLocationId(item.getLocationId());
        String batchNo = defaultBatchNo(item.getBatchNo());
        restoreSingleStockRow(warehouseId, item.getProductId(), locId, batchNo, totalBaseQty, baseUnitCost, billType, billId, operator);
        recordReturnCost(warehouseId, item.getProductId(), billId, billType, "1", totalBaseQty, baseUnitCost);
    }

    private void restoreSingleStockRow(Long warehouseId, Long productId, Long locId, String batchNo,
                                       BigDecimal qty, BigDecimal baseUnitCost, String billType, Long billId, String operator)
    {
        WmsStock sq = new WmsStock();
        sq.setWarehouseId(warehouseId);
        sq.setProductId(productId);
        sq.setLocationId(locId);
        sq.setBatchNo(batchNo);
        WmsStock stock = wmsStockMapper.selectWmsStockByKeyForUpdate(sq);
        BigDecimal beforeQty;
        if (stock == null)
        {
            stock = new WmsStock();
            stock.setWarehouseId(warehouseId);
            stock.setProductId(productId);
            stock.setLocationId(locId);
            stock.setBatchNo(batchNo);
            stock.setQuantity(qty);
            stock.setLockedQuantity(BigDecimal.ZERO);
            stock.setFrozenQuantity(BigDecimal.ZERO);
            if (mdProductMapper != null)
            {
                MdProduct p = mdProductMapper.selectMdProductById(productId);
                stock.setWarningMinQty(p == null ? BigDecimal.ZERO : defaultBigDecimal(p.getWarningMinQty()));
                stock.setWarningMaxQty(p == null ? BigDecimal.ZERO : defaultBigDecimal(p.getWarningMaxQty()));
            }
            stock.setVersion(0L);
            stock.setCreateBy(operator);
            wmsStockMapper.insertWmsStock(stock);
            beforeQty = BigDecimal.ZERO;
        }
        else
        {
            beforeQty = defaultBigDecimal(stock.getQuantity());
            stock.setQuantity(beforeQty.add(qty));
            stock.setUpdateBy(operator);
            wmsStockMapper.updateWmsStock(stock);
        }
        WmsStockLog log = new WmsStockLog();
        log.setWarehouseId(warehouseId);
        log.setProductId(productId);
        log.setLocationId(locId);
        log.setBatchNo(batchNo);
        log.setBillType(billType);
        log.setBillId(billId);
        log.setInOut("1");
        log.setQuantity(qty);
        log.setPrice(baseUnitCost);
        log.setAmount(baseUnitCost.multiply(qty).setScale(2, RoundingMode.HALF_UP));
        log.setBeforeQty(beforeQty);
        log.setAfterQty(beforeQty.add(qty));
        log.setCreateBy(operator);
        wmsStockLogMapper.insertWmsStockLog(log);
    }

    private BigDecimal defaultBigDecimal(BigDecimal value) { return value == null ? BigDecimal.ZERO : value; }

    private BigDecimal calculateAvailableQuantity(WmsStock stock)
    {
        return defaultBigDecimal(stock.getQuantity())
            .subtract(defaultBigDecimal(stock.getLockedQuantity()))
            .subtract(defaultBigDecimal(stock.getFrozenQuantity()));
    }

    private Long defaultLocationId(Long locationId) { return locationId == null ? 0L : locationId; }
    private String defaultBatchNo(String batchNo) { return batchNo == null ? "" : batchNo.trim(); }

    private void validateActiveMasterRefs(WmsPurchaseReturn doc)
    {
        if (doc.getSupplierId() != null && mdSupplierMapper != null)
        {
            MdSupplier sup = mdSupplierMapper.selectMdSupplierById(doc.getSupplierId());
            if (sup != null && "1".equals(sup.getStatus()))
            {
                throw new ServiceException("供应商[" + sup.getSupplierName() + "]已停用，禁止采购退货");
            }
        }
        if (doc.getWarehouseId() != null && mdWarehouseMapper != null)
        {
            MdWarehouse wh = mdWarehouseMapper.selectMdWarehouseById(doc.getWarehouseId());
            if (wh != null && "1".equals(wh.getStatus()))
            {
                throw new ServiceException("仓库[" + wh.getWarehouseName() + "]已停用，禁止采购退货");
            }
        }
    }

    private void validateLocationBelongsToWarehouse(Long locationId, Long warehouseId)
    {
        if (locationId == null || locationId <= 0L || warehouseId == null || mdLocationMapper == null) return;
        MdLocation loc = mdLocationMapper.selectMdLocationByLocationId(locationId);
        if (loc == null) throw new ServiceException("指定库位(#" + locationId + ")不存在");
        if (!warehouseId.equals(loc.getWarehouseId()))
        {
            throw new ServiceException("库位[" + loc.getLocationName() + "]不属于当前退货仓库");
        }
    }

    private BigDecimal resolveBaseQuantity(WmsPurchaseReturnItem item)
    {
        if (item.getBaseQuantity() != null && item.getBaseQuantity().compareTo(BigDecimal.ZERO) != 0)
        {
            return item.getBaseQuantity().abs();
        }
        BigDecimal qty = defaultBigDecimal(item.getQuantity()).abs();
        BigDecimal ratio = item.getConvertRatio();
        if (ratio == null || ratio.compareTo(BigDecimal.ZERO) <= 0)
        {
            ratio = mdProductUnitService != null
                ? mdProductUnitService.resolveConvertRatio(item.getProductId(), item.getUnitName())
                : BigDecimal.ONE;
        }
        return qty.multiply(ratio).setScale(4, RoundingMode.HALF_UP);
    }

    private Date resolvePayableDueDate(Long supplierId, Date businessDate)
    {
        MdSupplier sup = mdSupplierMapper != null ? mdSupplierMapper.selectMdSupplierById(supplierId) : null;
        int days = sup == null || sup.getPayableDays() == null ? 30 : sup.getPayableDays();
        Calendar cal = Calendar.getInstance();
        cal.setTime(businessDate == null ? new Date() : businessDate);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    private WmsPurchaseReturn getExistingPurchaseReturn(Long purchaseReturnId)
    {
        WmsPurchaseReturn purchaseReturn = wmsPurchaseReturnMapper.selectWmsPurchaseReturnById(purchaseReturnId);
        if (purchaseReturn == null) throw new ServiceException("采购退货单不存在");
        return purchaseReturn;
    }

    private String generateReturnNo()
    {
        String returnNoPrefix = "prt" + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String currentMaxReturnNo = wmsPurchaseReturnMapper.selectMaxReturnNoByPrefix(returnNoPrefix);
        int nextSequence = 1;
        if (StringUtils.isNotEmpty(currentMaxReturnNo) && currentMaxReturnNo.length() > returnNoPrefix.length())
        {
            String sequenceText = currentMaxReturnNo.substring(returnNoPrefix.length());
            if (StringUtils.isNumeric(sequenceText)) nextSequence = Integer.parseInt(sequenceText) + 1;
        }
        return returnNoPrefix + String.format("%03d", nextSequence);
    }

    private void validatePurchaseReturnEditable(WmsPurchaseReturn purchaseReturn)
    {
        if (!"0".equals(purchaseReturn.getStatus())) throw new ServiceException("采购退货单已审核，无法修改");
    }

    private void saveReturnItemsAndRecalculate(WmsPurchaseReturn doc)
    {
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (WmsPurchaseReturnItem item : doc.getItemList())
        {
            if (item.getProductId() == null) continue;
            BigDecimal qty = defaultBigDecimal(item.getQuantity());
            if (qty.compareTo(BigDecimal.ZERO) <= 0) throw new ServiceException("采购退货明细数量必须大于0");
            validateLocationBelongsToWarehouse(item.getLocationId(), doc.getWarehouseId());
            MdProduct product = mdProductMapper != null ? mdProductMapper.selectMdProductById(item.getProductId()) : null;
            if (StringUtils.isEmpty(item.getUnitName()) && product != null)
            {
                item.setUnitName(StringUtils.isNotEmpty(product.getPurchaseUnit()) ? product.getPurchaseUnit() : product.getUnitName());
            }
            BigDecimal ratio = mdProductUnitService != null
                ? mdProductUnitService.resolveConvertRatio(item.getProductId(), item.getUnitName())
                : (item.getConvertRatio() != null && item.getConvertRatio().compareTo(BigDecimal.ZERO) > 0 ? item.getConvertRatio() : BigDecimal.ONE);
            item.setConvertRatio(ratio);
            item.setBaseQuantity(qty.multiply(ratio).setScale(4, RoundingMode.HALF_UP));
            item.setLocationId(defaultLocationId(item.getLocationId()));
            item.setBatchNo(defaultBatchNo(item.getBatchNo()));
            item.setPurchaseReturnId(doc.getPurchaseReturnId());
            BigDecimal price = defaultBigDecimal(item.getPrice());
            BigDecimal amount = item.getAmount();
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            {
                amount = qty.multiply(price).setScale(2, RoundingMode.HALF_UP);
                item.setAmount(amount);
            }
            wmsPurchaseReturnItemMapper.insertWmsPurchaseReturnItem(item);
            totalQty = totalQty.add(qty);
            totalAmount = totalAmount.add(amount);
        }
        doc.setTotalQty(totalQty);
        doc.setTotalAmount(totalAmount);
        wmsPurchaseReturnMapper.updateWmsPurchaseReturn(doc);
    }

    private void validateSourceInboundQty(WmsPurchaseReturn doc, List<WmsPurchaseReturnItem> itemList)
    {
        if (!"inbound".equals(doc.getSourceBillType()) || doc.getSourceBillId() == null) return;
        WmsInboundItem q = new WmsInboundItem();
        q.setInboundId(doc.getSourceBillId());
        List<WmsInboundItem> sourceItems = wmsInboundItemMapper.selectWmsInboundItemList(q);
        if (sourceItems == null || sourceItems.isEmpty()) return;
        Map<Long, BigDecimal> sourceBaseMap = new HashMap<Long, BigDecimal>();
        for (WmsInboundItem si : sourceItems)
        {
            BigDecimal base = si.getBaseQuantity() != null && si.getBaseQuantity().compareTo(BigDecimal.ZERO) > 0
                ? si.getBaseQuantity() : defaultBigDecimal(si.getQuantity());
            sourceBaseMap.merge(si.getProductId(), base, BigDecimal::add);
        }
        // 汇总同一来源入库单所有已审核未红冲的历史退货基准数量 + 本次退货基准数量，严禁分次超退
        Map<Long, BigDecimal> returnBaseMap = new HashMap<Long, BigDecimal>();
        WmsPurchaseReturn histQ = new WmsPurchaseReturn();
        histQ.setSourceBillType("inbound");
        histQ.setSourceBillId(doc.getSourceBillId());
        histQ.setStatus("1");
        List<WmsPurchaseReturn> histReturns = wmsPurchaseReturnMapper.selectWmsPurchaseReturnList(histQ);
        if (histReturns != null)
        {
            for (WmsPurchaseReturn hr : histReturns)
            {
                if (doc.getPurchaseReturnId().equals(hr.getPurchaseReturnId())
                    || "1".equals(hr.getIsReversal()) || "1".equals(hr.getReversedFlag()))
                {
                    continue;
                }
                WmsPurchaseReturnItem hiq = new WmsPurchaseReturnItem();
                hiq.setPurchaseReturnId(hr.getPurchaseReturnId());
                List<WmsPurchaseReturnItem> hItems = wmsPurchaseReturnItemMapper.selectWmsPurchaseReturnItemList(hiq);
                if (hItems != null)
                {
                    for (WmsPurchaseReturnItem hi : hItems)
                    {
                        returnBaseMap.merge(hi.getProductId(), resolveBaseQuantity(hi), BigDecimal::add);
                    }
                }
            }
        }
        for (WmsPurchaseReturnItem ri : itemList)
        {
            returnBaseMap.merge(ri.getProductId(), resolveBaseQuantity(ri), BigDecimal::add);
        }
        for (Map.Entry<Long, BigDecimal> entry : returnBaseMap.entrySet())
        {
            BigDecimal allowed = sourceBaseMap.getOrDefault(entry.getKey(), BigDecimal.ZERO);
            if (entry.getValue().compareTo(allowed) > 0)
            {
                throw new ServiceException("商品 #" + entry.getKey() + " 累计采购退货基准数量(" + entry.getValue()
                    + ")超过来源入库单数量(" + allowed + ")，禁止超量退货");
            }
        }
    }

    private void deductSingleStockRow(WmsStock stock, Long purchaseReturnId, BigDecimal deductQty, BigDecimal itemPrice, BigDecimal itemAmount, String operator)
    {
        BigDecimal beforeQuantity = defaultBigDecimal(stock.getQuantity());
        BigDecimal afterQuantity = beforeQuantity.subtract(deductQty);
        if (afterQuantity.compareTo(BigDecimal.ZERO) < 0) throw new ServiceException("库存不足，无法退货");
        stock.setQuantity(afterQuantity);
        stock.setUpdateBy(operator);
        wmsStockMapper.updateWmsStock(stock);
        WmsStockLog stockLog = new WmsStockLog();
        stockLog.setWarehouseId(stock.getWarehouseId());
        stockLog.setProductId(stock.getProductId());
        stockLog.setLocationId(defaultLocationId(stock.getLocationId()));
        stockLog.setBatchNo(defaultBatchNo(stock.getBatchNo()));
        stockLog.setBillType("purchase_return");
        stockLog.setBillId(purchaseReturnId);
        stockLog.setInOut("2");
        stockLog.setQuantity(deductQty);
        stockLog.setPrice(itemPrice);
        stockLog.setAmount(itemAmount);
        stockLog.setBeforeQty(beforeQuantity);
        stockLog.setAfterQty(afterQuantity);
        stockLog.setCreateBy(operator);
        wmsStockLogMapper.insertWmsStockLog(stockLog);
    }

    private String deductAcrossWarehouseStock(Long warehouseId, Long productId, Long purchaseReturnId, BigDecimal requiredQty, BigDecimal itemPrice, String operator)
    {
        List<WmsStock> stockRows = wmsStockMapper.selectWmsStockByWarehouseAndProductForUpdate(warehouseId, productId);
        BigDecimal totalAvailable = BigDecimal.ZERO;
        if (stockRows != null)
        {
            for (WmsStock row : stockRows)
            {
                BigDecimal avail = calculateAvailableQuantity(row);
                if (avail.compareTo(BigDecimal.ZERO) > 0) totalAvailable = totalAvailable.add(avail);
            }
        }
        if (totalAvailable.compareTo(requiredQty) < 0) throw new ServiceException("可用库存不足，无法退货");
        BigDecimal remaining = requiredQty;
        JSONArray trace = new JSONArray();
        for (WmsStock row : stockRows)
        {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
            BigDecimal avail = calculateAvailableQuantity(row);
            if (avail.compareTo(BigDecimal.ZERO) <= 0) continue;
            BigDecimal take = avail.min(remaining);
            deductSingleStockRow(row, purchaseReturnId, take, itemPrice, itemPrice.multiply(take).setScale(2, RoundingMode.HALF_UP), operator);
            JSONObject entry = new JSONObject();
            entry.put("locationId", defaultLocationId(row.getLocationId()));
            entry.put("batchNo", defaultBatchNo(row.getBatchNo()));
            entry.put("qty", take.toPlainString());
            trace.add(entry);
            remaining = remaining.subtract(take);
        }
        return trace.toJSONString();
    }

    private void recordReturnCost(Long warehouseId, Long productId, Long billId, String billType, String inOut, BigDecimal quantity, BigDecimal price)
    {
        FinCostCalculate req = new FinCostCalculate();
        req.setWarehouseId(warehouseId);
        req.setProductId(productId);
        req.setBillType(billType);
        req.setBillId(billId);
        req.setInOut(inOut);
        req.setQuantity(quantity);
        req.setPrice(price.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : price);
        finCostCalculateService.calculateCost(req);
    }

    private WmsPurchaseReturn checkPurchaseReturnDeletable(Long purchaseReturnId)
    {
        WmsPurchaseReturn purchaseReturn = wmsPurchaseReturnMapper.selectWmsPurchaseReturnById(purchaseReturnId);
        if (purchaseReturn == null) throw new ServiceException("采购退货单不存在");
        if (!"0".equals(purchaseReturn.getStatus())) throw new ServiceException("采购退货单状态已变更，无法删除");
        return purchaseReturn;
    }
}
