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
import com.canglian.business.domain.FinCostCalculate;
import com.canglian.business.domain.FinCostLog;
import com.canglian.business.domain.FinReceivable;
import com.canglian.business.domain.MdCustomer;
import com.canglian.business.domain.MdLocation;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.MdWarehouse;
import com.canglian.business.domain.WmsOutbound;
import com.canglian.business.domain.WmsOutboundItem;
import com.canglian.business.domain.WmsSaleReturn;
import com.canglian.business.domain.WmsSaleReturnItem;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsStockLog;
import com.canglian.business.mapper.FinReceivableMapper;
import com.canglian.business.mapper.MdCustomerMapper;
import com.canglian.business.mapper.MdLocationMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.MdWarehouseMapper;
import com.canglian.business.mapper.WmsOutboundItemMapper;
import com.canglian.business.mapper.WmsOutboundMapper;
import com.canglian.business.mapper.WmsSaleReturnItemMapper;
import com.canglian.business.mapper.WmsSaleReturnMapper;
import com.canglian.business.mapper.WmsStockLogMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IFinCostCalculateService;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IFinReceivableService;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.business.service.IMdProductUnitService;
import com.canglian.business.service.IWmsSaleReturnService;
import com.canglian.business.service.IWmsSerialNoService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 销售退货 服务层实现
 *
 * @author canglian
 */
@Service
public class WmsSaleReturnServiceImpl implements IWmsSaleReturnService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private WmsSaleReturnMapper wmsSaleReturnMapper;

    @Autowired
    private WmsSaleReturnItemMapper wmsSaleReturnItemMapper;

    @Autowired
    private WmsStockMapper wmsStockMapper;

    @Autowired
    private WmsStockLogMapper wmsStockLogMapper;

    @Autowired
    private MdProductMapper mdProductMapper;

    @Autowired
    private WmsOutboundMapper wmsOutboundMapper;

    @Autowired
    private WmsOutboundItemMapper wmsOutboundItemMapper;

    @Autowired(required = false)
    private FinReceivableMapper finReceivableMapper;

    @Autowired
    private IFinGeneralLedgerService finGeneralLedgerService;

    @Autowired
    private IFinVoucherEventService finVoucherEventService;

    @Autowired
    private IFinCostCalculateService finCostCalculateService;

    @Autowired
    private IFinReceivableService finReceivableService;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Autowired(required = false)
    private MdCustomerMapper mdCustomerMapper;

    @Autowired(required = false)
    private MdWarehouseMapper mdWarehouseMapper;

    @Autowired(required = false)
    private MdLocationMapper mdLocationMapper;

    @Autowired(required = false)
    private IMdProductUnitService mdProductUnitService;

    @Autowired(required = false)
    private IWmsSerialNoService wmsSerialNoService;

    @Override
    public WmsSaleReturn selectWmsSaleReturnById(Long saleReturnId)
    {
        WmsSaleReturn doc = wmsSaleReturnMapper.selectWmsSaleReturnById(saleReturnId);
        if (doc != null)
        {
            if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(doc.getDeptId(), doc.getOwnerUserId(), doc.getWarehouseId());
            WmsSaleReturnItem query = new WmsSaleReturnItem();
            query.setSaleReturnId(saleReturnId);
            doc.setItemList(wmsSaleReturnItemMapper.selectWmsSaleReturnItemList(query));
        }
        return doc;
    }

    @Override
    public List<WmsSaleReturn> selectWmsSaleReturnList(WmsSaleReturn wmsSaleReturn)
    {
        if (bizScopeHelper != null) bizScopeHelper.applyScopeFilter(wmsSaleReturn, "", true, true);
        return wmsSaleReturnMapper.selectWmsSaleReturnList(wmsSaleReturn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWmsSaleReturn(WmsSaleReturn wmsSaleReturn)
    {
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(wmsSaleReturn.getWarehouseId());
            if (wmsSaleReturn.getDeptId() == null) wmsSaleReturn.setDeptId(bizScopeHelper.currentDeptId());
            if (wmsSaleReturn.getOwnerUserId() == null) wmsSaleReturn.setOwnerUserId(bizScopeHelper.currentUserId());
        }
        if (StringUtils.isEmpty(wmsSaleReturn.getReturnNo())) wmsSaleReturn.setReturnNo(generateReturnNo());
        if (wmsSaleReturn.getBusinessDate() == null) wmsSaleReturn.setBusinessDate(new Date());
        if (finGeneralLedgerService != null) finGeneralLedgerService.assertPeriodOpen(wmsSaleReturn.getBusinessDate());
        validateActiveMasterRefs(wmsSaleReturn);
        wmsSaleReturn.setTotalQty(BigDecimal.ZERO);
        wmsSaleReturn.setTotalAmount(BigDecimal.ZERO);
        wmsSaleReturn.setStatus("0");
        wmsSaleReturn.setBizStatus("draft");
        wmsSaleReturn.setIsReversal("0");
        wmsSaleReturn.setReversedFlag("0");
        wmsSaleReturn.setAuditBy(null);
        wmsSaleReturn.setAuditTime(null);
        int rows = wmsSaleReturnMapper.insertWmsSaleReturn(wmsSaleReturn);
        if (rows > 0 && wmsSaleReturn.getItemList() != null && !wmsSaleReturn.getItemList().isEmpty())
        {
            saveReturnItemsAndRecalculate(wmsSaleReturn);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWmsSaleReturn(WmsSaleReturn wmsSaleReturn)
    {
        WmsSaleReturn saleReturn = getExistingSaleReturn(wmsSaleReturn.getSaleReturnId());
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(saleReturn.getDeptId(), saleReturn.getOwnerUserId(), saleReturn.getWarehouseId());
            bizScopeHelper.assertWarehouseAccess(wmsSaleReturn.getWarehouseId());
        }
        validateSaleReturnEditable(saleReturn);
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(saleReturn.getBusinessDate());
            if (wmsSaleReturn.getBusinessDate() != null)
            {
                finGeneralLedgerService.assertPeriodOpen(wmsSaleReturn.getBusinessDate());
            }
        }
        if (wmsSaleReturn.getWarehouseId() == null) wmsSaleReturn.setWarehouseId(saleReturn.getWarehouseId());
        validateActiveMasterRefs(wmsSaleReturn);
        wmsSaleReturn.setStatus(saleReturn.getStatus());
        wmsSaleReturn.setAuditBy(saleReturn.getAuditBy());
        wmsSaleReturn.setAuditTime(saleReturn.getAuditTime());
        wmsSaleReturn.setTotalQty(saleReturn.getTotalQty());
        wmsSaleReturn.setTotalAmount(saleReturn.getTotalAmount());
        wmsSaleReturn.setBizStatus(saleReturn.getBizStatus());
        int rows = wmsSaleReturnMapper.updateWmsSaleReturn(wmsSaleReturn);
        if (rows > 0 && wmsSaleReturn.getItemList() != null)
        {
            wmsSaleReturnItemMapper.deleteWmsSaleReturnItemBySaleReturnId(wmsSaleReturn.getSaleReturnId());
            saveReturnItemsAndRecalculate(wmsSaleReturn);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsSaleReturnById(Long saleReturnId)
    {
        WmsSaleReturn doc = checkSaleReturnDeletable(saleReturnId);
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(doc.getDeptId(), doc.getOwnerUserId(), doc.getWarehouseId());
        if (finGeneralLedgerService != null) finGeneralLedgerService.assertPeriodOpen(doc.getBusinessDate());
        wmsSaleReturnItemMapper.deleteWmsSaleReturnItemBySaleReturnId(saleReturnId);
        return wmsSaleReturnMapper.deleteWmsSaleReturnById(saleReturnId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsSaleReturnByIds(Long[] saleReturnIds)
    {
        for (Long id : saleReturnIds)
        {
            WmsSaleReturn doc = checkSaleReturnDeletable(id);
            if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(doc.getDeptId(), doc.getOwnerUserId(), doc.getWarehouseId());
            if (finGeneralLedgerService != null) finGeneralLedgerService.assertPeriodOpen(doc.getBusinessDate());
        }
        for (Long id : saleReturnIds) wmsSaleReturnItemMapper.deleteWmsSaleReturnItemBySaleReturnId(id);
        return wmsSaleReturnMapper.deleteWmsSaleReturnByIds(saleReturnIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int auditWmsSaleReturn(Long saleReturnId, String operator)
    {
        WmsSaleReturn saleReturn = wmsSaleReturnMapper.selectWmsSaleReturnByIdForUpdate(saleReturnId);
        if (saleReturn == null) throw new ServiceException("销售退货单不存在");
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(saleReturn.getDeptId(), saleReturn.getOwnerUserId(), saleReturn.getWarehouseId());
        if (!"0".equals(saleReturn.getStatus())) throw new ServiceException("销售退货单状态已变更，无法审核");
        finGeneralLedgerService.assertPeriodOpen(saleReturn.getBusinessDate());
        if (saleReturn.getWarehouseId() == null) throw new ServiceException("退货仓库不能为空");
        validateActiveMasterRefs(saleReturn);
        WmsSaleReturnItem itemQuery = new WmsSaleReturnItem();
        itemQuery.setSaleReturnId(saleReturnId);
        List<WmsSaleReturnItem> itemList = wmsSaleReturnItemMapper.selectWmsSaleReturnItemList(itemQuery);
        if (itemList == null || itemList.isEmpty()) throw new ServiceException("销售退货明细不能为空");
        validateSourceOutboundForReturn(saleReturn, itemList);

        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalCogsReturn = BigDecimal.ZERO;
        for (WmsSaleReturnItem returnItem : itemList)
        {
            BigDecimal itemQuantity = defaultBigDecimal(returnItem.getQuantity());
            if (itemQuantity.compareTo(BigDecimal.ZERO) <= 0) throw new ServiceException("退货数量必须大于0");
            if (returnItem.getProductId() == null) throw new ServiceException("退货明细商品不能为空");
            validateLocationBelongsToWarehouse(returnItem.getLocationId(), saleReturn.getWarehouseId());
            MdProduct product = mdProductMapper != null ? mdProductMapper.selectMdProductById(returnItem.getProductId()) : null;
            BigDecimal baseQty = resolveBaseQuantity(returnItem);
            Long locationId = defaultLocationId(returnItem.getLocationId());
            String batchNo = defaultBatchNo(returnItem.getBatchNo());

            // 序列号强管控：启用序列号的商品必须录满足额已出库 SN 并回库至在库
            if (wmsSerialNoService != null)
            {
                wmsSerialNoService.handleSaleReturnSerials(product, saleReturn.getWarehouseId(), locationId,
                    saleReturnId, saleReturn.getReturnNo(), returnItem.getSnCodes(), baseQty, operator);
            }

            WmsStock stockQuery = new WmsStock();
            stockQuery.setWarehouseId(saleReturn.getWarehouseId());
            stockQuery.setProductId(returnItem.getProductId());
            stockQuery.setLocationId(locationId);
            stockQuery.setBatchNo(batchNo);
            WmsStock stock = wmsStockMapper.selectWmsStockByKeyForUpdate(stockQuery);
            BigDecimal beforeQuantity;
            if (stock == null)
            {
                stock = new WmsStock();
                stock.setWarehouseId(saleReturn.getWarehouseId());
                stock.setProductId(returnItem.getProductId());
                stock.setLocationId(locationId);
                stock.setBatchNo(batchNo);
                stock.setProductionDate(returnItem.getProductionDate());
                stock.setExpiryDate(returnItem.getExpiryDate());
                stock.setQuantity(baseQty);
                stock.setLockedQuantity(BigDecimal.ZERO);
                stock.setFrozenQuantity(BigDecimal.ZERO);
                fillStockWarningQty(stock, returnItem.getProductId());
                stock.setVersion(0L);
                stock.setCreateBy(operator);
                wmsStockMapper.insertWmsStock(stock);
                beforeQuantity = BigDecimal.ZERO;
            }
            else
            {
                beforeQuantity = defaultBigDecimal(stock.getQuantity());
                stock.setQuantity(beforeQuantity.add(baseQty));
                if (stock.getProductionDate() == null && returnItem.getProductionDate() != null)
                {
                    stock.setProductionDate(returnItem.getProductionDate());
                }
                if (stock.getExpiryDate() == null && returnItem.getExpiryDate() != null)
                {
                    stock.setExpiryDate(returnItem.getExpiryDate());
                }
                stock.setUpdateBy(operator);
                wmsStockMapper.updateWmsStock(stock);
            }
            BigDecimal afterQuantity = beforeQuantity.add(baseQty);
            BigDecimal itemPrice = defaultBigDecimal(returnItem.getPrice());
            BigDecimal itemAmount = returnItem.getAmount();
            if (itemAmount == null || itemAmount.compareTo(BigDecimal.ZERO) <= 0)
            {
                itemAmount = itemPrice.multiply(itemQuantity).setScale(2, RoundingMode.HALF_UP);
            }
            // 按历史出库真实成本核算退货入库，避免将售价写入库存流水与成本账
            BigDecimal baseFallbackPrice = baseQty.compareTo(BigDecimal.ZERO) > 0
                ? itemAmount.divide(baseQty, 4, RoundingMode.HALF_UP) : itemPrice;
            FinCostLog costLog = recordReturnCost(saleReturn, saleReturnId, returnItem.getProductId(), "sale_return", "1", baseQty, baseFallbackPrice);
            BigDecimal returnUnitCost = costLog != null && defaultBigDecimal(costLog.getPrice()).compareTo(BigDecimal.ZERO) > 0
                ? costLog.getPrice() : baseFallbackPrice;
            BigDecimal returnCostAmount = costLog != null && defaultBigDecimal(costLog.getAmount()).compareTo(BigDecimal.ZERO) > 0
                ? costLog.getAmount().setScale(2, RoundingMode.HALF_UP)
                : returnUnitCost.multiply(baseQty).setScale(2, RoundingMode.HALF_UP);

            WmsStockLog stockLog = new WmsStockLog();
            stockLog.setWarehouseId(saleReturn.getWarehouseId());
            stockLog.setProductId(returnItem.getProductId());
            stockLog.setLocationId(locationId);
            stockLog.setBatchNo(batchNo);
            stockLog.setBillType("sale_return");
            stockLog.setBillId(saleReturnId);
            stockLog.setInOut("1");
            stockLog.setQuantity(baseQty);
            stockLog.setPrice(returnUnitCost);
            stockLog.setAmount(returnCostAmount);
            stockLog.setBeforeQty(beforeQuantity);
            stockLog.setAfterQty(afterQuantity);
            stockLog.setCreateBy(operator);
            wmsStockLogMapper.insertWmsStockLog(stockLog);

            totalQuantity = totalQuantity.add(itemQuantity);
            totalAmount = totalAmount.add(itemAmount);
            totalCogsReturn = totalCogsReturn.add(returnCostAmount);
        }
        saleReturn.setStatus("1");
        saleReturn.setAuditBy(operator);
        saleReturn.setAuditTime(new Date());
        saleReturn.setUpdateBy(operator);
        saleReturn.setTotalQty(totalQuantity);
        saleReturn.setTotalAmount(totalAmount);
        saleReturn.setBizStatus("completed");
        int rows = wmsSaleReturnMapper.updateWmsSaleReturn(saleReturn);
        if (rows > 0)
        {
            if (saleReturn.getCustomerId() != null && totalAmount.compareTo(BigDecimal.ZERO) > 0)
            {
                FinReceivable redAr = finReceivableService.createRedReceivableForReturn(
                    saleReturn.getCustomerId(), "sale_return", saleReturn.getSaleReturnId(), saleReturn.getReturnNo(),
                    saleReturn.getBusinessDate(), totalAmount, operator, "销售退货自动生成红字应收冲减单");
                if (redAr != null && redAr.getReceivableId() != null)
                {
                    saleReturn.setReceivableId(redAr.getReceivableId());
                    wmsSaleReturnMapper.updateWmsSaleReturn(saleReturn);
                }
            }
            finVoucherEventService.recordVoucherEvent("sale_return", saleReturn.getSaleReturnId(), saleReturn.getReturnNo(),
                "sale_return_complete", saleReturn.getBusinessDate(), saleReturn.getTotalAmount(), operator, "销售退货冲减收入待生成凭证");
            if (totalCogsReturn.compareTo(BigDecimal.ZERO) > 0)
            {
                finVoucherEventService.recordVoucherEvent("sale_return_cogs", saleReturn.getSaleReturnId(), saleReturn.getReturnNo(),
                    "outbound_cogs_return", saleReturn.getBusinessDate(), totalCogsReturn, operator, "销售退货冲回销售成本待生成凭证");
            }
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unauditWmsSaleReturn(Long saleReturnId, String operator)
    {
        WmsSaleReturn doc = wmsSaleReturnMapper.selectWmsSaleReturnByIdForUpdate(saleReturnId);
        if (doc == null) throw new ServiceException("销售退货单不存在");
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(doc.getDeptId(), doc.getOwnerUserId(), doc.getWarehouseId());
        if (!"1".equals(doc.getStatus())) throw new ServiceException("仅已审核销售退货单允许反审核");
        if ("1".equals(doc.getIsReversal())) throw new ServiceException("红冲单据不允许反审核");
        if ("1".equals(doc.getReversedFlag())) throw new ServiceException("该销售退货单已被红冲，禁止反审核");
        finGeneralLedgerService.assertPeriodOpen(doc.getBusinessDate());
        finVoucherEventService.cancelPendingVoucherEvents("sale_return", saleReturnId);
        finVoucherEventService.cancelPendingVoucherEvents("sale_return_cogs", saleReturnId);

        if (doc.getReceivableId() != null && finReceivableMapper != null)
        {
            FinReceivable redAr = finReceivableMapper.selectFinReceivableById(doc.getReceivableId());
            if (redAr != null)
            {
                if (defaultBigDecimal(redAr.getReceivedAmount()).compareTo(BigDecimal.ZERO) != 0)
                {
                    throw new ServiceException("关联红字应收单已对冲结算，禁止反审核，请使用红冲功能");
                }
                finReceivableMapper.deleteFinReceivableById(redAr.getReceivableId());
            }
        }

        WmsSaleReturnItem q = new WmsSaleReturnItem();
        q.setSaleReturnId(saleReturnId);
        List<WmsSaleReturnItem> items = wmsSaleReturnItemMapper.selectWmsSaleReturnItemList(q);
        if (items != null)
        {
            for (WmsSaleReturnItem item : items)
            {
                MdProduct product = mdProductMapper != null ? mdProductMapper.selectMdProductById(item.getProductId()) : null;
                if (wmsSerialNoService != null)
                {
                    wmsSerialNoService.rollbackSaleReturnSerials(product, saleReturnId, doc.getReturnNo(),
                        doc.getCustomerId(), item.getSnCodes(), operator);
                }
                deductStockForReverse(doc, item, "sale_return_unaudit", saleReturnId, operator);
            }
        }

        doc.setStatus("0");
        doc.setBizStatus("draft");
        doc.setReceivableId(null);
        doc.setAuditBy("");
        doc.setAuditTime(null);
        doc.setUnauditBy(operator);
        doc.setUnauditTime(new Date());
        doc.setUpdateBy(operator);
        return wmsSaleReturnMapper.updateWmsSaleReturn(doc);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsSaleReturn reverseWmsSaleReturn(Long saleReturnId, String operator)
    {
        WmsSaleReturn origin = wmsSaleReturnMapper.selectWmsSaleReturnByIdForUpdate(saleReturnId);
        if (origin == null) throw new ServiceException("销售退货单不存在");
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(origin.getDeptId(), origin.getOwnerUserId(), origin.getWarehouseId());
        if (!"1".equals(origin.getStatus())) throw new ServiceException("仅已审核销售退货单允许红冲");
        if ("1".equals(origin.getIsReversal())) throw new ServiceException("红冲单据不允许再次红冲");
        if ("1".equals(origin.getReversedFlag())) throw new ServiceException("该销售退货单已被红冲，请勿重复红冲");
        finGeneralLedgerService.assertPeriodOpen(new Date());

        WmsSaleReturnItem q = new WmsSaleReturnItem();
        q.setSaleReturnId(saleReturnId);
        List<WmsSaleReturnItem> originItems = wmsSaleReturnItemMapper.selectWmsSaleReturnItemList(q);
        if (originItems == null || originItems.isEmpty()) throw new ServiceException("原销售退货单无明细，无法红冲");

        WmsSaleReturn reversal = new WmsSaleReturn();
        reversal.setReturnNo(generateReturnNo());
        reversal.setReturnType(origin.getReturnType());
        reversal.setCustomerId(origin.getCustomerId());
        reversal.setWarehouseId(origin.getWarehouseId());
        reversal.setDeptId(origin.getDeptId());
        reversal.setOwnerUserId(origin.getOwnerUserId());
        reversal.setSourceBillType("sale_return_reverse");
        reversal.setSourceBillId(origin.getSaleReturnId());
        reversal.setSourceBillNo(origin.getReturnNo());
        reversal.setBusinessDate(new Date());
        reversal.setTotalQty(defaultBigDecimal(origin.getTotalQty()).negate());
        reversal.setTotalAmount(defaultBigDecimal(origin.getTotalAmount()).negate());
        reversal.setStatus("1");
        reversal.setBizStatus("completed");
        reversal.setAuditBy(operator);
        reversal.setAuditTime(new Date());
        reversal.setIsReversal("1");
        reversal.setOriginBillId(origin.getSaleReturnId());
        reversal.setReversedFlag("0");
        reversal.setCreateBy(operator);
        reversal.setRemark("红冲原销售退货单：" + origin.getReturnNo());
        wmsSaleReturnMapper.insertWmsSaleReturn(reversal);

        BigDecimal totalCogsAmt = BigDecimal.ZERO;
        for (WmsSaleReturnItem oi : originItems)
        {
            MdProduct product = mdProductMapper != null ? mdProductMapper.selectMdProductById(oi.getProductId()) : null;
            if (wmsSerialNoService != null)
            {
                wmsSerialNoService.rollbackSaleReturnSerials(product, origin.getSaleReturnId(), reversal.getReturnNo(),
                    origin.getCustomerId(), oi.getSnCodes(), operator);
            }
            BigDecimal lineCost = deductStockForReverse(origin, oi, "sale_return_reverse", reversal.getSaleReturnId(), operator);
            totalCogsAmt = totalCogsAmt.add(lineCost);
            WmsSaleReturnItem ri = new WmsSaleReturnItem();
            ri.setSaleReturnId(reversal.getSaleReturnId());
            ri.setProductId(oi.getProductId());
            ri.setLocationId(defaultLocationId(oi.getLocationId()));
            ri.setBatchNo(defaultBatchNo(oi.getBatchNo()));
            ri.setUnitName(oi.getUnitName());
            ri.setConvertRatio(oi.getConvertRatio());
            ri.setBaseQuantity(resolveBaseQuantity(oi).negate());
            ri.setProductionDate(oi.getProductionDate());
            ri.setExpiryDate(oi.getExpiryDate());
            ri.setQuantity(defaultBigDecimal(oi.getQuantity()).negate());
            ri.setPrice(oi.getPrice());
            ri.setAmount(defaultBigDecimal(oi.getAmount()).negate());
            ri.setSnCodes(oi.getSnCodes());
            wmsSaleReturnItemMapper.insertWmsSaleReturnItem(ri);
        }

        origin.setReversedFlag("1");
        origin.setUpdateBy(operator);
        wmsSaleReturnMapper.updateWmsSaleReturn(origin);

        // 红冲销售退货单时同步补回正数蓝字应收，抵消原退货单生成的红字应收
        if (origin.getCustomerId() != null && defaultBigDecimal(origin.getTotalAmount()).compareTo(BigDecimal.ZERO) > 0)
        {
            FinReceivable compAr = new FinReceivable();
            compAr.setCustomerId(origin.getCustomerId());
            compAr.setBillType("sale_return_reverse");
            compAr.setBillId(reversal.getSaleReturnId());
            compAr.setSourceBillType("sale_return_reverse");
            compAr.setSourceBillId(reversal.getSaleReturnId());
            compAr.setSourceBillNo(reversal.getReturnNo());
            compAr.setBusinessDate(new Date());
            compAr.setAmount(origin.getTotalAmount());
            compAr.setDueDate(resolveReceivableDueDate(origin.getCustomerId(), new Date()));
            compAr.setCreateBy(operator);
            compAr.setRemark("红冲销售退货单 " + origin.getReturnNo() + " 自动补回蓝字应收");
            finReceivableService.insertFinReceivable(compAr);
            finReceivableService.auditFinReceivable(compAr.getReceivableId(), operator);
            reversal.setReceivableId(compAr.getReceivableId());
            wmsSaleReturnMapper.updateWmsSaleReturn(reversal);
        }
        finVoucherEventService.recordVoucherEvent("sale_return", reversal.getSaleReturnId(), reversal.getReturnNo(),
            "outbound_complete", new Date(), origin.getTotalAmount(), operator, "销售退货红冲恢复收入凭证");
        if (totalCogsAmt.compareTo(BigDecimal.ZERO) > 0)
        {
            finVoucherEventService.recordVoucherEvent("sale_return_cogs", reversal.getSaleReturnId(), reversal.getReturnNo(),
                "outbound_cogs", new Date(), totalCogsAmt, operator, "销售退货红冲恢复销售成本凭证");
        }
        return selectWmsSaleReturnById(reversal.getSaleReturnId());
    }

    private BigDecimal deductStockForReverse(WmsSaleReturn doc, WmsSaleReturnItem item, String billType, Long billId, String operator)
    {
        BigDecimal baseQty = resolveBaseQuantity(item).abs();
        Long locId = defaultLocationId(item.getLocationId());
        String batchNo = defaultBatchNo(item.getBatchNo());
        WmsStock sq = new WmsStock();
        sq.setWarehouseId(doc.getWarehouseId());
        sq.setProductId(item.getProductId());
        sq.setLocationId(locId);
        sq.setBatchNo(batchNo);
        WmsStock stock = wmsStockMapper.selectWmsStockByKeyForUpdate(sq);
        if (stock == null) throw new ServiceException("退货入库库存已消耗，无法回退销售退货单");
        BigDecimal beforeQty = defaultBigDecimal(stock.getQuantity());
        BigDecimal avail = beforeQty.subtract(defaultBigDecimal(stock.getLockedQuantity())).subtract(defaultBigDecimal(stock.getFrozenQuantity()));
        if (avail.compareTo(baseQty) < 0) throw new ServiceException("可用库存不足，无法回退销售退货单");
        BigDecimal afterQty = beforeQty.subtract(baseQty);
        stock.setQuantity(afterQty);
        stock.setUpdateBy(operator);
        wmsStockMapper.updateWmsStock(stock);

        BigDecimal baseFallbackPrice = baseQty.compareTo(BigDecimal.ZERO) > 0
            ? defaultBigDecimal(item.getAmount()).abs().divide(baseQty, 4, RoundingMode.HALF_UP) : defaultBigDecimal(item.getPrice());
        FinCostLog costLog = recordReturnCost(doc, billId, item.getProductId(), billType, "2", baseQty, baseFallbackPrice);
        BigDecimal unitCost = costLog != null && defaultBigDecimal(costLog.getPrice()).compareTo(BigDecimal.ZERO) > 0
            ? costLog.getPrice() : baseFallbackPrice;
        BigDecimal costAmount = costLog != null && defaultBigDecimal(costLog.getAmount()).compareTo(BigDecimal.ZERO) > 0
            ? costLog.getAmount().setScale(2, RoundingMode.HALF_UP)
            : unitCost.multiply(baseQty).setScale(2, RoundingMode.HALF_UP);

        WmsStockLog log = new WmsStockLog();
        log.setWarehouseId(doc.getWarehouseId());
        log.setProductId(item.getProductId());
        log.setLocationId(locId);
        log.setBatchNo(batchNo);
        log.setBillType(billType);
        log.setBillId(billId);
        log.setInOut("2");
        log.setQuantity(baseQty);
        log.setPrice(unitCost);
        log.setAmount(costAmount);
        log.setBeforeQty(beforeQty);
        log.setAfterQty(afterQty);
        log.setCreateBy(operator);
        wmsStockLogMapper.insertWmsStockLog(log);
        return costAmount;
    }

    private BigDecimal defaultBigDecimal(BigDecimal value) { return value == null ? BigDecimal.ZERO : value; }
    private Long defaultLocationId(Long locationId) { return locationId == null ? 0L : locationId; }
    private String defaultBatchNo(String batchNo) { return batchNo == null ? "" : batchNo.trim(); }

    private void validateActiveMasterRefs(WmsSaleReturn doc)
    {
        if (doc.getCustomerId() != null && mdCustomerMapper != null)
        {
            MdCustomer cus = mdCustomerMapper.selectMdCustomerById(doc.getCustomerId());
            if (cus != null && "1".equals(cus.getStatus()))
            {
                throw new ServiceException("客户[" + cus.getCustomerName() + "]已停用，禁止销售退货");
            }
        }
        if (doc.getWarehouseId() != null && mdWarehouseMapper != null)
        {
            MdWarehouse wh = mdWarehouseMapper.selectMdWarehouseById(doc.getWarehouseId());
            if (wh != null && "1".equals(wh.getStatus()))
            {
                throw new ServiceException("仓库[" + wh.getWarehouseName() + "]已停用，禁止销售退货");
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

    private BigDecimal resolveBaseQuantity(WmsSaleReturnItem item)
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

    private Date resolveReceivableDueDate(Long customerId, Date businessDate)
    {
        MdCustomer cus = mdCustomerMapper != null ? mdCustomerMapper.selectMdCustomerById(customerId) : null;
        int days = cus == null || cus.getReceivableDays() == null ? 30 : cus.getReceivableDays();
        Calendar cal = Calendar.getInstance();
        cal.setTime(businessDate == null ? new Date() : businessDate);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    private void fillStockWarningQty(WmsStock stock, Long productId)
    {
        MdProduct mdProduct = mdProductMapper.selectMdProductById(productId);
        stock.setWarningMinQty(mdProduct == null ? BigDecimal.ZERO : defaultBigDecimal(mdProduct.getWarningMinQty()));
        stock.setWarningMaxQty(mdProduct == null ? BigDecimal.ZERO : defaultBigDecimal(mdProduct.getWarningMaxQty()));
    }

    private WmsSaleReturn getExistingSaleReturn(Long saleReturnId)
    {
        WmsSaleReturn saleReturn = wmsSaleReturnMapper.selectWmsSaleReturnById(saleReturnId);
        if (saleReturn == null) throw new ServiceException("销售退货单不存在");
        return saleReturn;
    }

    private String generateReturnNo()
    {
        String returnNoPrefix = "srt" + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String currentMaxReturnNo = wmsSaleReturnMapper.selectMaxReturnNoByPrefix(returnNoPrefix);
        int nextSequence = 1;
        if (StringUtils.isNotEmpty(currentMaxReturnNo) && currentMaxReturnNo.length() > returnNoPrefix.length())
        {
            String sequenceText = currentMaxReturnNo.substring(returnNoPrefix.length());
            if (StringUtils.isNumeric(sequenceText)) nextSequence = Integer.parseInt(sequenceText) + 1;
        }
        return returnNoPrefix + String.format("%03d", nextSequence);
    }

    private void validateSaleReturnEditable(WmsSaleReturn saleReturn)
    {
        if (!"0".equals(saleReturn.getStatus())) throw new ServiceException("销售退货单已审核，无法修改");
    }

    private void saveReturnItemsAndRecalculate(WmsSaleReturn doc)
    {
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (WmsSaleReturnItem item : doc.getItemList())
        {
            if (item.getProductId() == null) continue;
            BigDecimal qty = defaultBigDecimal(item.getQuantity());
            if (qty.compareTo(BigDecimal.ZERO) <= 0) throw new ServiceException("销售退货明细数量必须大于0");
            validateLocationBelongsToWarehouse(item.getLocationId(), doc.getWarehouseId());
            MdProduct product = mdProductMapper != null ? mdProductMapper.selectMdProductById(item.getProductId()) : null;
            if (StringUtils.isEmpty(item.getUnitName()) && product != null)
            {
                item.setUnitName(StringUtils.isNotEmpty(product.getSaleUnit()) ? product.getSaleUnit() : product.getUnitName());
            }
            BigDecimal ratio = mdProductUnitService != null
                ? mdProductUnitService.resolveConvertRatio(item.getProductId(), item.getUnitName())
                : (item.getConvertRatio() != null && item.getConvertRatio().compareTo(BigDecimal.ZERO) > 0 ? item.getConvertRatio() : BigDecimal.ONE);
            item.setConvertRatio(ratio);
            item.setBaseQuantity(qty.multiply(ratio).setScale(4, RoundingMode.HALF_UP));
            item.setLocationId(defaultLocationId(item.getLocationId()));
            item.setBatchNo(defaultBatchNo(item.getBatchNo()));
            item.setSaleReturnId(doc.getSaleReturnId());
            BigDecimal price = defaultBigDecimal(item.getPrice());
            BigDecimal amount = item.getAmount();
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            {
                amount = qty.multiply(price).setScale(2, RoundingMode.HALF_UP);
                item.setAmount(amount);
            }
            wmsSaleReturnItemMapper.insertWmsSaleReturnItem(item);
            totalQty = totalQty.add(qty);
            totalAmount = totalAmount.add(amount);
        }
        doc.setTotalQty(totalQty);
        doc.setTotalAmount(totalAmount);
        wmsSaleReturnMapper.updateWmsSaleReturn(doc);
    }

    private void validateSourceOutboundForReturn(WmsSaleReturn doc, List<WmsSaleReturnItem> itemList)
    {
        if (!"outbound".equals(doc.getSourceBillType()) || doc.getSourceBillId() == null) return;
        WmsOutbound outbound = wmsOutboundMapper.selectWmsOutboundById(doc.getSourceBillId());
        if (outbound != null)
        {
            if ("1".equals(outbound.getIsReversal()) || "1".equals(outbound.getReversedFlag()))
            {
                throw new ServiceException("关联出库单已红冲，禁止重复办理销售退货");
            }
            if ("3".equals(outbound.getDeliveryStatus()))
            {
                throw new ServiceException("关联出库单已办理整单退货，请勿重复退货");
            }
            if (!"2".equals(outbound.getDeliveryStatus()))
            {
                throw new ServiceException("仅已签收确认的销售出库单允许办理销售退货单；未签收出库请通过出库单反审核或整单退货处理");
            }
        }
        WmsOutboundItem q = new WmsOutboundItem();
        q.setOutboundId(doc.getSourceBillId());
        List<WmsOutboundItem> sourceItems = wmsOutboundItemMapper.selectWmsOutboundItemList(q);
        if (sourceItems == null || sourceItems.isEmpty()) return;
        Map<Long, BigDecimal> sourceBaseMap = new HashMap<Long, BigDecimal>();
        for (WmsOutboundItem si : sourceItems)
        {
            BigDecimal base = si.getBaseQuantity() != null && si.getBaseQuantity().compareTo(BigDecimal.ZERO) > 0
                ? si.getBaseQuantity() : defaultBigDecimal(si.getQuantity());
            sourceBaseMap.merge(si.getProductId(), base, BigDecimal::add);
        }
        // 汇总同一来源出库单所有已审核未红冲的历史退货基准数量 + 本次退货基准数量，严禁分次超退
        Map<Long, BigDecimal> returnBaseMap = new HashMap<Long, BigDecimal>();
        WmsSaleReturn histQ = new WmsSaleReturn();
        histQ.setSourceBillType("outbound");
        histQ.setSourceBillId(doc.getSourceBillId());
        histQ.setStatus("1");
        List<WmsSaleReturn> histReturns = wmsSaleReturnMapper.selectWmsSaleReturnList(histQ);
        if (histReturns != null)
        {
            for (WmsSaleReturn hr : histReturns)
            {
                if (doc.getSaleReturnId().equals(hr.getSaleReturnId())
                    || "1".equals(hr.getIsReversal()) || "1".equals(hr.getReversedFlag()))
                {
                    continue;
                }
                WmsSaleReturnItem hiq = new WmsSaleReturnItem();
                hiq.setSaleReturnId(hr.getSaleReturnId());
                List<WmsSaleReturnItem> hItems = wmsSaleReturnItemMapper.selectWmsSaleReturnItemList(hiq);
                if (hItems != null)
                {
                    for (WmsSaleReturnItem hi : hItems)
                    {
                        returnBaseMap.merge(hi.getProductId(), resolveBaseQuantity(hi), BigDecimal::add);
                    }
                }
            }
        }
        for (WmsSaleReturnItem ri : itemList)
        {
            returnBaseMap.merge(ri.getProductId(), resolveBaseQuantity(ri), BigDecimal::add);
        }
        for (Map.Entry<Long, BigDecimal> entry : returnBaseMap.entrySet())
        {
            BigDecimal allowed = sourceBaseMap.getOrDefault(entry.getKey(), BigDecimal.ZERO);
            if (entry.getValue().compareTo(allowed) > 0)
            {
                throw new ServiceException("商品 #" + entry.getKey() + " 累计销售退货基准数量(" + entry.getValue()
                    + ")超过来源出库单数量(" + allowed + ")，禁止超量退货");
            }
        }
    }

    private FinCostLog recordReturnCost(WmsSaleReturn saleReturn, Long costBillId, Long productId, String billType, String inOut, BigDecimal quantity, BigDecimal fallbackPrice)
    {
        BigDecimal unitCost = finCostCalculateService.resolveReturnUnitCost(
            saleReturn.getWarehouseId(), productId, saleReturn.getSourceBillType(), saleReturn.getSourceBillId(), fallbackPrice);
        FinCostCalculate req = new FinCostCalculate();
        req.setWarehouseId(saleReturn.getWarehouseId());
        req.setProductId(productId);
        req.setBillType(billType);
        req.setBillId(costBillId);
        req.setInOut(inOut);
        req.setQuantity(quantity);
        req.setPrice(unitCost.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : unitCost);
        return finCostCalculateService.calculateCost(req);
    }

    private WmsSaleReturn checkSaleReturnDeletable(Long saleReturnId)
    {
        WmsSaleReturn saleReturn = wmsSaleReturnMapper.selectWmsSaleReturnById(saleReturnId);
        if (saleReturn == null) throw new ServiceException("销售退货单不存在");
        if (!"0".equals(saleReturn.getStatus())) throw new ServiceException("销售退货单状态已变更，无法删除");
        return saleReturn;
    }
}
