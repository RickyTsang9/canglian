package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.MdSupplier;
import com.canglian.business.domain.MdWarehouse;
import com.canglian.business.domain.PurOrder;
import com.canglian.business.domain.PurOrderItem;
import com.canglian.business.domain.WmsInbound;
import com.canglian.business.domain.WmsInboundItem;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.MdSupplierMapper;
import com.canglian.business.mapper.MdWarehouseMapper;
import com.canglian.business.mapper.PurOrderItemMapper;
import com.canglian.business.mapper.PurOrderMapper;
import com.canglian.business.mapper.WmsInboundItemMapper;
import com.canglian.business.mapper.WmsInboundMapper;
import com.canglian.business.service.IMdProductUnitService;
import com.canglian.business.service.IPriceResolverService;
import com.canglian.business.service.IPurOrderService;
import com.canglian.business.service.IWmsInboundService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 购货订单服务实现
 *
 * @author canglian
 */
@Service
public class PurOrderServiceImpl implements IPurOrderService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    @Autowired
    private PurOrderMapper purOrderMapper;

    @Autowired
    private PurOrderItemMapper purOrderItemMapper;

    @Autowired
    private WmsInboundMapper wmsInboundMapper;

    @Autowired
    private WmsInboundItemMapper wmsInboundItemMapper;

    @Autowired
    private MdProductMapper mdProductMapper;

    @Autowired(required = false)
    private MdSupplierMapper mdSupplierMapper;

    @Autowired(required = false)
    private MdWarehouseMapper mdWarehouseMapper;

    @Autowired
    private IWmsInboundService wmsInboundService;

    @Autowired(required = false)
    private IMdProductUnitService mdProductUnitService;

    @Autowired(required = false)
    private IPriceResolverService priceResolverService;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Override
    public PurOrder selectPurOrderById(Long purchaseOrderId)
    {
        PurOrder purOrder = purOrderMapper.selectPurOrderById(purchaseOrderId);
        if (purOrder != null)
        {
            if (bizScopeHelper != null)
            {
                bizScopeHelper.assertRecordAccess(purOrder.getDeptId(), purOrder.getOwnerUserId(), purOrder.getWarehouseId());
            }
            purOrder.setOrderItemList(purOrderItemMapper.selectPurOrderItemListByOrderId(purchaseOrderId));
        }
        return purOrder;
    }

    @Override
    public List<PurOrder> selectPurOrderList(PurOrder purOrder)
    {
        if (bizScopeHelper != null)
        {
            bizScopeHelper.applyScopeFilter(purOrder, "o", true, true);
        }
        return purOrderMapper.selectPurOrderList(purOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurOrder(PurOrder purOrder)
    {
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(purOrder.getWarehouseId());
            if (purOrder.getDeptId() == null)
            {
                purOrder.setDeptId(bizScopeHelper.currentDeptId());
            }
            if (purOrder.getOwnerUserId() == null)
            {
                purOrder.setOwnerUserId(bizScopeHelper.currentUserId());
            }
        }
        prepareOrderForSave(purOrder, true);
        validateActiveMasterRefs(purOrder);
        int insertRows = purOrderMapper.insertPurOrder(purOrder);
        saveOrderItemList(purOrder);
        return insertRows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurOrder(PurOrder purOrder)
    {
        PurOrder existingPurOrder = getExistingPurOrder(purOrder.getPurchaseOrderId());
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(existingPurOrder.getDeptId(), existingPurOrder.getOwnerUserId(), existingPurOrder.getWarehouseId());
            bizScopeHelper.assertWarehouseAccess(purOrder.getWarehouseId());
        }
        validateEditable(existingPurOrder);
        preserveReadonlyFields(existingPurOrder, purOrder);
        prepareOrderForSave(purOrder, false);
        int updateRows = purOrderMapper.updatePurOrder(purOrder);
        purOrderItemMapper.deletePurOrderItemByOrderId(purOrder.getPurchaseOrderId());
        saveOrderItemList(purOrder);
        return updateRows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int approvePurOrder(Long purchaseOrderId, String operator)
    {
        PurOrder purOrder = getExistingPurOrder(purchaseOrderId);
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(purOrder.getDeptId(), purOrder.getOwnerUserId(), purOrder.getWarehouseId());
        }
        validateEditable(purOrder);
        if (purOrder.getSupplierId() == null)
        {
            throw new ServiceException("购货订单供应商不能为空");
        }
        List<PurOrderItem> purOrderItemList = purOrderItemMapper.selectPurOrderItemListByOrderId(purchaseOrderId);
        if (purOrderItemList == null || purOrderItemList.isEmpty())
        {
            throw new ServiceException("购货订单明细不能为空");
        }
        rebuildItemAmount(purOrderItemList);
        purOrderItemMapper.deletePurOrderItemByOrderId(purchaseOrderId);
        purOrder.setOrderItemList(purOrderItemList);
        saveOrderItemList(purOrder);
        fillOrderTotal(purOrder, purOrderItemList);
        purOrder.setStatus("1");
        purOrder.setBizStatus("approved");
        purOrder.setApproveBy(operator);
        purOrder.setApproveTime(new Date());
        purOrder.setUpdateBy(operator);
        return purOrderMapper.updatePurOrder(purOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unapprovePurOrder(Long purchaseOrderId, String operator)
    {
        PurOrder purOrder = getExistingPurOrder(purchaseOrderId);
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(purOrder.getDeptId(), purOrder.getOwnerUserId(), purOrder.getWarehouseId());
        }
        if (!"approved".equals(purOrder.getBizStatus()))
        {
            throw new ServiceException("仅已审批且尚未下推执行的购货订单允许反审批");
        }
        List<WmsInbound> downstream = findDownstreamInbounds(purOrder);
        if (!downstream.isEmpty())
        {
            throw new ServiceException("该购货订单已存在下游入库单，禁止反审批");
        }
        purOrder.setStatus("0");
        purOrder.setBizStatus("draft");
        purOrder.setApproveBy("");
        purOrder.setApproveTime(null);
        purOrder.setUpdateBy(operator);
        return purOrderMapper.updatePurOrder(purOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int closePurOrder(Long purchaseOrderId, String closeReason, String operator)
    {
        PurOrder purOrder = getExistingPurOrder(purchaseOrderId);
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(purOrder.getDeptId(), purOrder.getOwnerUserId(), purOrder.getWarehouseId());
        }
        if (!"approved".equals(purOrder.getBizStatus()) && !"executing".equals(purOrder.getBizStatus()))
        {
            throw new ServiceException("仅审批通过或执行中的购货订单允许强制关闭");
        }
        purOrder.setStatus("2");
        purOrder.setBizStatus("closed");
        purOrder.setCloseReason(StringUtils.isNotEmpty(closeReason) ? closeReason.trim() : "业务手动强制关闭");
        purOrder.setUpdateBy(operator);
        return purOrderMapper.updatePurOrder(purOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int pushPurOrderToInbound(Long purchaseOrderId, String operator)
    {
        return pushPurOrderToInbound(purchaseOrderId, null, operator);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int pushPurOrderToInbound(Long purchaseOrderId, List<Map<String, Object>> pushItems, String operator)
    {
        PurOrder purOrder = getExistingPurOrder(purchaseOrderId);
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(purOrder.getDeptId(), purOrder.getOwnerUserId(), purOrder.getWarehouseId());
        }
        if (!"approved".equals(purOrder.getBizStatus()) && !"executing".equals(purOrder.getBizStatus()))
        {
            throw new ServiceException("购货订单处于审批通过或执行中状态才允许下推入库单");
        }
        if (purOrder.getWarehouseId() == null)
        {
            throw new ServiceException("购货订单入库仓库不能为空，请先完善仓库信息");
        }
        if (purOrder.getSupplierId() == null)
        {
            throw new ServiceException("购货订单供应商不能为空");
        }
        List<PurOrderItem> purOrderItemList = purOrderItemMapper.selectPurOrderItemListByOrderId(purchaseOrderId);
        if (purOrderItemList == null || purOrderItemList.isEmpty())
        {
            throw new ServiceException("购货订单明细不能为空");
        }

        // 解析本次下推明细数量（支持分批下推；为空时默认下推全部剩余数量）
        Map<Long, BigDecimal> requestedByItemId = new HashMap<Long, BigDecimal>();
        Map<Long, BigDecimal> requestedByProductId = new HashMap<Long, BigDecimal>();
        boolean partialMode = pushItems != null && !pushItems.isEmpty();
        if (partialMode)
        {
            for (Map<String, Object> req : pushItems)
            {
                BigDecimal qty = parseDecimal(req.get("pushQty") != null ? req.get("pushQty") : req.get("quantity"));
                if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0)
                {
                    continue;
                }
                Long itemId = parseLong(req.get("purchaseOrderItemId") != null ? req.get("purchaseOrderItemId") : req.get("orderItemId"));
                Long productId = parseLong(req.get("productId"));
                if (itemId != null)
                {
                    requestedByItemId.put(itemId, defaultAmount(requestedByItemId.get(itemId)).add(qty));
                }
                else if (productId != null)
                {
                    requestedByProductId.put(productId, defaultAmount(requestedByProductId.get(productId)).add(qty));
                }
            }
            if (requestedByItemId.isEmpty() && requestedByProductId.isEmpty())
            {
                throw new ServiceException("请填写本次下推数量（必须大于0）");
            }
        }

        List<WmsInboundItem> itemsToCreate = new ArrayList<WmsInboundItem>();
        Map<Long, BigDecimal> updatedReceivedMap = new HashMap<Long, BigDecimal>();
        BigDecimal totalGrossAmount = BigDecimal.ZERO;
        for (PurOrderItem item : purOrderItemList)
        {
            totalGrossAmount = totalGrossAmount.add(defaultAmount(item.getAmount()));
        }
        BigDecimal orderDiscount = defaultAmount(purOrder.getDiscountAmount());
        // 预先按比例分摊整单优惠，并将尾差归集到最后一行，消除 1 分钱误差
        Map<Long, BigDecimal> lineDiscountShare = new HashMap<Long, BigDecimal>();
        if (orderDiscount.compareTo(BigDecimal.ZERO) > 0 && totalGrossAmount.compareTo(BigDecimal.ZERO) > 0)
        {
            BigDecimal allocated = BigDecimal.ZERO;
            for (int idx = 0; idx < purOrderItemList.size(); idx++)
            {
                PurOrderItem it = purOrderItemList.get(idx);
                BigDecimal share;
                if (idx == purOrderItemList.size() - 1)
                {
                    share = orderDiscount.subtract(allocated).max(BigDecimal.ZERO);
                }
                else
                {
                    share = orderDiscount.multiply(defaultAmount(it.getAmount())).divide(totalGrossAmount, 2, RoundingMode.HALF_UP);
                    allocated = allocated.add(share);
                }
                lineDiscountShare.put(it.getPurchaseOrderItemId(), share);
            }
        }
        BigDecimal pushHeaderDiscount = BigDecimal.ZERO;
        BigDecimal pushTaxTotal = BigDecimal.ZERO;

        for (PurOrderItem purOrderItem : purOrderItemList)
        {
            BigDecimal orderedQty = defaultAmount(purOrderItem.getQuantity());
            BigDecimal alreadyReceived = defaultAmount(purOrderItem.getReceivedQty());
            BigDecimal remaining = orderedQty.subtract(alreadyReceived);
            if (remaining.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            BigDecimal pushQty;
            if (!partialMode)
            {
                pushQty = remaining;
            }
            else if (requestedByItemId.containsKey(purOrderItem.getPurchaseOrderItemId()))
            {
                pushQty = requestedByItemId.get(purOrderItem.getPurchaseOrderItemId());
            }
            else if (requestedByProductId.containsKey(purOrderItem.getProductId()))
            {
                pushQty = requestedByProductId.get(purOrderItem.getProductId());
            }
            else
            {
                continue;
            }
            if (pushQty.compareTo(remaining) > 0)
            {
                throw new ServiceException("商品 #" + purOrderItem.getProductId() + " 本次下推数量(" + pushQty + ")超过剩余可下推数量(" + remaining + ")");
            }
            BigDecimal lineShare = defaultAmount(lineDiscountShare.get(purOrderItem.getPurchaseOrderItemId()));
            BigDecimal lineNetAmount = defaultAmount(purOrderItem.getAmount()).subtract(lineShare).max(BigDecimal.ZERO);
            BigDecimal effectiveUnitPrice = orderedQty.compareTo(BigDecimal.ZERO) > 0
                ? lineNetAmount.divide(orderedQty, 4, RoundingMode.HALF_UP)
                : defaultAmount(purOrderItem.getPrice());
            BigDecimal pushAmount = orderedQty.compareTo(pushQty) == 0
                ? lineNetAmount.setScale(2, RoundingMode.HALF_UP)
                : effectiveUnitPrice.multiply(pushQty).setScale(2, RoundingMode.HALF_UP);
            BigDecimal pushLineTax = orderedQty.compareTo(BigDecimal.ZERO) > 0
                ? defaultAmount(purOrderItem.getTaxAmount()).multiply(pushQty).divide(orderedQty, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
            BigDecimal pushLineDiscount = orderedQty.compareTo(BigDecimal.ZERO) > 0
                ? lineShare.multiply(pushQty).divide(orderedQty, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
            BigDecimal ratio = purOrderItem.getConvertRatio() != null && purOrderItem.getConvertRatio().compareTo(BigDecimal.ZERO) > 0
                ? purOrderItem.getConvertRatio() : BigDecimal.ONE;

            WmsInboundItem wmsInboundItem = new WmsInboundItem();
            wmsInboundItem.setSourceItemId(purOrderItem.getPurchaseOrderItemId());
            wmsInboundItem.setProductId(purOrderItem.getProductId());
            wmsInboundItem.setLocationId(0L);
            wmsInboundItem.setBatchNo("");
            wmsInboundItem.setUnitName(purOrderItem.getUnitName());
            wmsInboundItem.setConvertRatio(ratio);
            wmsInboundItem.setBaseQuantity(pushQty.multiply(ratio).setScale(4, RoundingMode.HALF_UP));
            wmsInboundItem.setQuantity(pushQty);
            wmsInboundItem.setPrice(effectiveUnitPrice.setScale(2, RoundingMode.HALF_UP));
            wmsInboundItem.setDiscountRate(purOrderItem.getDiscountRate() != null ? purOrderItem.getDiscountRate() : HUNDRED);
            wmsInboundItem.setTaxRate(purOrderItem.getTaxRate() != null ? purOrderItem.getTaxRate() : BigDecimal.ZERO);
            wmsInboundItem.setTaxAmount(pushLineTax);
            wmsInboundItem.setAmount(pushAmount);
            itemsToCreate.add(wmsInboundItem);
            pushHeaderDiscount = pushHeaderDiscount.add(pushLineDiscount);
            pushTaxTotal = pushTaxTotal.add(pushLineTax);
            updatedReceivedMap.put(purOrderItem.getPurchaseOrderItemId(), alreadyReceived.add(pushQty));
        }

        if (itemsToCreate.isEmpty())
        {
            throw new ServiceException("该购货订单已全部下推完毕，无可下推的剩余数量");
        }

        WmsInbound wmsInbound = new WmsInbound();
        wmsInbound.setInboundType("purchase_order");
        wmsInbound.setSupplierId(purOrder.getSupplierId());
        wmsInbound.setWarehouseId(purOrder.getWarehouseId());
        wmsInbound.setSourceBillType("pur_order");
        wmsInbound.setSourceBillId(purOrder.getPurchaseOrderId());
        wmsInbound.setSourceBillNo(purOrder.getPurchaseOrderNo());
        wmsInbound.setBusinessDate(new Date());
        wmsInbound.setDiscountAmount(pushHeaderDiscount);
        wmsInbound.setTaxTotalAmount(pushTaxTotal);
        wmsInbound.setStatus("0");
        wmsInbound.setBizStatus("draft");
        wmsInbound.setCreateBy(operator);
        wmsInbound.setRemark("由购货订单 " + purOrder.getPurchaseOrderNo() + " 分批下推生成");
        wmsInboundService.insertWmsInbound(wmsInbound);

        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (WmsInboundItem item : itemsToCreate)
        {
            item.setInboundId(wmsInbound.getInboundId());
            wmsInboundItemMapper.insertWmsInboundItem(item);
            totalQty = totalQty.add(defaultAmount(item.getQuantity()));
            totalAmount = totalAmount.add(defaultAmount(item.getAmount()));
        }
        wmsInbound.setTotalQty(totalQty);
        wmsInbound.setTotalAmount(totalAmount);
        wmsInboundMapper.updateWmsInbound(wmsInbound);

        for (Map.Entry<Long, BigDecimal> entry : updatedReceivedMap.entrySet())
        {
            purOrderItemMapper.updateReceivedQty(entry.getKey(), entry.getValue());
        }

        purOrder.setBizStatus("executing");
        purOrder.setUpdateBy(operator);
        purOrderMapper.updatePurOrder(purOrder);
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncOrderProgressFromWms(Long purchaseOrderId, String operator)
    {
        if (purchaseOrderId == null)
        {
            return;
        }
        PurOrder purOrder = purOrderMapper.selectPurOrderById(purchaseOrderId);
        if (purOrder == null || "closed".equals(purOrder.getBizStatus()) || "draft".equals(purOrder.getBizStatus()))
        {
            return;
        }
        List<PurOrderItem> orderItems = purOrderItemMapper.selectPurOrderItemListByOrderId(purchaseOrderId);
        if (orderItems == null || orderItems.isEmpty())
        {
            return;
        }
        List<WmsInbound> inbounds = findDownstreamInbounds(purOrder);
        Map<Long, BigDecimal> pushedByItemId = new HashMap<Long, BigDecimal>();
        Map<Long, BigDecimal> pushedByProductId = new HashMap<Long, BigDecimal>();
        boolean hasDraftInbound = false;
        boolean hasAnyActiveInbound = false;

        for (WmsInbound inb : inbounds)
        {
            if ("1".equals(inb.getIsReversal()) || "1".equals(inb.getReversedFlag()))
            {
                continue;
            }
            hasAnyActiveInbound = true;
            if (!"completed".equals(inb.getBizStatus()))
            {
                hasDraftInbound = true;
            }
            WmsInboundItem q = new WmsInboundItem();
            q.setInboundId(inb.getInboundId());
            List<WmsInboundItem> inbItems = wmsInboundItemMapper.selectWmsInboundItemList(q);
            if (inbItems != null)
            {
                for (WmsInboundItem inbItem : inbItems)
                {
                    if (inbItem.getSourceItemId() != null)
                    {
                        pushedByItemId.put(inbItem.getSourceItemId(),
                            defaultAmount(pushedByItemId.get(inbItem.getSourceItemId())).add(defaultAmount(inbItem.getQuantity())));
                    }
                    else if (inbItem.getProductId() != null)
                    {
                        pushedByProductId.put(inbItem.getProductId(),
                            defaultAmount(pushedByProductId.get(inbItem.getProductId())).add(defaultAmount(inbItem.getQuantity())));
                    }
                }
            }
        }

        boolean allFulfilled = true;
        for (PurOrderItem item : orderItems)
        {
            BigDecimal rec = defaultAmount(pushedByItemId.get(item.getPurchaseOrderItemId()));
            if (rec.compareTo(BigDecimal.ZERO) == 0 && pushedByProductId.containsKey(item.getProductId()))
            {
                rec = defaultAmount(pushedByProductId.get(item.getProductId())).min(defaultAmount(item.getQuantity()));
            }
            purOrderItemMapper.updateReceivedQty(item.getPurchaseOrderItemId(), rec);
            if (rec.compareTo(defaultAmount(item.getQuantity())) < 0)
            {
                allFulfilled = false;
            }
        }

        if (!hasAnyActiveInbound)
        {
            purOrder.setBizStatus("approved");
            purOrder.setStatus("1");
        }
        else if (allFulfilled && !hasDraftInbound)
        {
            purOrder.setBizStatus("completed");
            purOrder.setStatus("2");
        }
        else
        {
            purOrder.setBizStatus("executing");
            purOrder.setStatus("1");
        }
        purOrder.setUpdateBy(operator);
        purOrderMapper.updatePurOrder(purOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurOrderById(Long purchaseOrderId)
    {
        PurOrder purOrder = getExistingPurOrder(purchaseOrderId);
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(purOrder.getDeptId(), purOrder.getOwnerUserId(), purOrder.getWarehouseId());
        }
        validateEditable(purOrder);
        purOrderItemMapper.deletePurOrderItemByOrderId(purchaseOrderId);
        return purOrderMapper.deletePurOrderById(purchaseOrderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurOrderByIds(Long[] purchaseOrderIds)
    {
        for (Long purchaseOrderId : purchaseOrderIds)
        {
            PurOrder purOrder = getExistingPurOrder(purchaseOrderId);
            if (bizScopeHelper != null)
            {
                bizScopeHelper.assertRecordAccess(purOrder.getDeptId(), purOrder.getOwnerUserId(), purOrder.getWarehouseId());
            }
            validateEditable(purOrder);
            purOrderItemMapper.deletePurOrderItemByOrderId(purchaseOrderId);
        }
        return purOrderMapper.deletePurOrderByIds(purchaseOrderIds);
    }

    private List<WmsInbound> findDownstreamInbounds(PurOrder purOrder)
    {
        WmsInbound query = new WmsInbound();
        query.setSourceBillType("pur_order");
        query.setSourceBillId(purOrder.getPurchaseOrderId());
        List<WmsInbound> list = wmsInboundMapper.selectWmsInboundList(query);
        List<WmsInbound> matched = new ArrayList<WmsInbound>();
        if (list != null)
        {
            for (WmsInbound inb : list)
            {
                if (purOrder.getPurchaseOrderId().equals(inb.getSourceBillId())
                    || (StringUtils.isNotEmpty(purOrder.getPurchaseOrderNo()) && purOrder.getPurchaseOrderNo().equals(inb.getSourceBillNo())))
                {
                    matched.add(inb);
                }
            }
        }
        return matched;
    }

    private void prepareOrderForSave(PurOrder purOrder, boolean isNew)
    {
        if (StringUtils.isEmpty(purOrder.getPurchaseOrderNo()))
        {
            purOrder.setPurchaseOrderNo(generateOrderNo());
        }
        if (purOrder.getBusinessDate() == null)
        {
            purOrder.setBusinessDate(new Date());
        }
        if (purOrder.getDiscountAmount() == null)
        {
            purOrder.setDiscountAmount(BigDecimal.ZERO);
        }
        if (purOrder.getDiscountAmount().compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("整单优惠金额不能为负数");
        }
        if (isNew)
        {
            purOrder.setStatus("0");
            purOrder.setBizStatus("draft");
            purOrder.setApproveBy("");
            purOrder.setApproveTime(null);
        }
        List<PurOrderItem> purOrderItemList = normalizeOrderItemList(purOrder.getOrderItemList());
        rebuildItemAmount(purOrderItemList);
        fillOrderTotal(purOrder, purOrderItemList);
        purOrder.setOrderItemList(purOrderItemList);
    }

    private void saveOrderItemList(PurOrder purOrder)
    {
        for (PurOrderItem purOrderItem : purOrder.getOrderItemList())
        {
            purOrderItem.setPurchaseOrderId(purOrder.getPurchaseOrderId());
            purOrderItemMapper.insertPurOrderItem(purOrderItem);
        }
    }

    private List<PurOrderItem> normalizeOrderItemList(List<PurOrderItem> orderItemList)
    {
        List<PurOrderItem> normalizedOrderItemList = orderItemList == null ? new ArrayList<PurOrderItem>() : orderItemList;
        if (normalizedOrderItemList.isEmpty())
        {
            throw new ServiceException("购货订单明细不能为空");
        }
        return normalizedOrderItemList;
    }

    private void rebuildItemAmount(List<PurOrderItem> purOrderItemList)
    {
        for (PurOrderItem purOrderItem : purOrderItemList)
        {
            if (purOrderItem.getProductId() == null)
            {
                throw new ServiceException("购货订单明细商品不能为空");
            }
            BigDecimal itemQuantity = defaultAmount(purOrderItem.getQuantity());
            if (itemQuantity.compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("购货订单明细数量必须大于0");
            }
            MdProduct mdProduct = mdProductMapper.selectMdProductById(purOrderItem.getProductId());
            if (mdProduct != null && "1".equals(mdProduct.getStatus()))
            {
                throw new ServiceException("商品[" + mdProduct.getProductName() + "]已停用，禁止下单");
            }
            if (StringUtils.isEmpty(purOrderItem.getUnitName()) && mdProduct != null)
            {
                purOrderItem.setUnitName(StringUtils.isNotEmpty(mdProduct.getPurchaseUnit()) ? mdProduct.getPurchaseUnit()
                    : (StringUtils.isNotEmpty(mdProduct.getBaseUnit()) ? mdProduct.getBaseUnit() : mdProduct.getUnitName()));
            }
            // 以服务端多单位配置为准核定换算率，杜绝前端篡改 convertRatio
            BigDecimal ratio = mdProductUnitService != null && StringUtils.isNotEmpty(purOrderItem.getUnitName())
                ? mdProductUnitService.resolveConvertRatio(purOrderItem.getProductId(), purOrderItem.getUnitName())
                : (purOrderItem.getConvertRatio() != null && purOrderItem.getConvertRatio().compareTo(BigDecimal.ZERO) > 0
                    ? purOrderItem.getConvertRatio() : BigDecimal.ONE);
            purOrderItem.setConvertRatio(ratio);
            purOrderItem.setBaseQuantity(itemQuantity.multiply(ratio).setScale(4, RoundingMode.HALF_UP));

            if (purOrderItem.getPrice() == null || purOrderItem.getPrice().compareTo(BigDecimal.ZERO) <= 0)
            {
                if (priceResolverService != null)
                {
                    purOrderItem.setPrice(priceResolverService.resolvePurchasePrice(purOrderItem.getProductId(), purOrderItem.getUnitName()));
                }
                else
                {
                    BigDecimal baseCost = mdProduct == null ? BigDecimal.ZERO : defaultAmount(mdProduct.getCostPrice());
                    purOrderItem.setPrice(baseCost.multiply(ratio).setScale(2, RoundingMode.HALF_UP));
                }
            }
            BigDecimal discountRate = purOrderItem.getDiscountRate() == null ? HUNDRED : purOrderItem.getDiscountRate();
            if (discountRate.compareTo(BigDecimal.ZERO) <= 0 || discountRate.compareTo(HUNDRED) > 0)
            {
                throw new ServiceException("明细折扣率必须在 0 到 100 之间");
            }
            BigDecimal taxRate = purOrderItem.getTaxRate() == null ? BigDecimal.ZERO : purOrderItem.getTaxRate();
            if (taxRate.compareTo(BigDecimal.ZERO) < 0 || taxRate.compareTo(HUNDRED) > 0)
            {
                throw new ServiceException("明细税率必须在 0 到 100 之间");
            }
            purOrderItem.setDiscountRate(discountRate);
            purOrderItem.setTaxRate(taxRate);
            purOrderItem.setQuantity(itemQuantity);

            BigDecimal netAmount = defaultAmount(purOrderItem.getPrice())
                .multiply(itemQuantity)
                .multiply(discountRate)
                .divide(HUNDRED, 2, RoundingMode.HALF_UP);
            BigDecimal taxAmount = netAmount.multiply(taxRate).divide(HUNDRED, 2, RoundingMode.HALF_UP);
            purOrderItem.setTaxAmount(taxAmount);
            purOrderItem.setAmount(netAmount.add(taxAmount));
            if (purOrderItem.getReceivedQty() == null)
            {
                purOrderItem.setReceivedQty(BigDecimal.ZERO);
            }
        }
    }

    private void fillOrderTotal(PurOrder purOrder, List<PurOrderItem> purOrderItemList)
    {
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal taxTotalAmount = BigDecimal.ZERO;
        for (PurOrderItem purOrderItem : purOrderItemList)
        {
            totalQuantity = totalQuantity.add(defaultAmount(purOrderItem.getQuantity()));
            totalAmount = totalAmount.add(defaultAmount(purOrderItem.getAmount()));
            taxTotalAmount = taxTotalAmount.add(defaultAmount(purOrderItem.getTaxAmount()));
        }
        BigDecimal discount = defaultAmount(purOrder.getDiscountAmount());
        if (discount.compareTo(totalAmount) > 0)
        {
            throw new ServiceException("整单优惠金额不能超过订单总金额");
        }
        purOrder.setTotalQty(totalQuantity);
        purOrder.setTaxTotalAmount(taxTotalAmount);
        purOrder.setTotalAmount(totalAmount.subtract(discount));
    }

    private void validateActiveMasterRefs(PurOrder purOrder)
    {
        if (purOrder == null)
        {
            return;
        }
        if (purOrder.getSupplierId() != null && mdSupplierMapper != null)
        {
            MdSupplier s = mdSupplierMapper.selectMdSupplierById(purOrder.getSupplierId());
            if (s != null && "1".equals(s.getStatus()))
            {
                throw new ServiceException("供应商[" + s.getSupplierName() + "]已停用，禁止下单");
            }
        }
        if (purOrder.getWarehouseId() != null && mdWarehouseMapper != null)
        {
            MdWarehouse w = mdWarehouseMapper.selectMdWarehouseById(purOrder.getWarehouseId());
            if (w != null && "1".equals(w.getStatus()))
            {
                throw new ServiceException("仓库[" + w.getWarehouseName() + "]已停用，禁止下单");
            }
        }
    }

    private PurOrder getExistingPurOrder(Long purchaseOrderId)
    {
        PurOrder purOrder = purOrderMapper.selectPurOrderById(purchaseOrderId);
        if (purOrder == null)
        {
            throw new ServiceException("购货订单不存在");
        }
        return purOrder;
    }

    private void validateEditable(PurOrder purOrder)
    {
        if (!"draft".equals(purOrder.getBizStatus()))
        {
            throw new ServiceException("当前购货订单状态不允许编辑");
        }
    }

    private void preserveReadonlyFields(PurOrder existingPurOrder, PurOrder purOrder)
    {
        purOrder.setPurchaseOrderNo(existingPurOrder.getPurchaseOrderNo());
        purOrder.setStatus(existingPurOrder.getStatus());
        purOrder.setBizStatus(existingPurOrder.getBizStatus());
        purOrder.setApproveBy(existingPurOrder.getApproveBy());
        purOrder.setApproveTime(existingPurOrder.getApproveTime());
    }

    private String generateOrderNo()
    {
        String orderPrefix = "pur" + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String currentMaxOrderNo = purOrderMapper.selectMaxPurchaseOrderNoByPrefix(orderPrefix);
        int nextSequence = 1;
        if (StringUtils.isNotEmpty(currentMaxOrderNo) && currentMaxOrderNo.length() > orderPrefix.length())
        {
            String sequenceText = currentMaxOrderNo.substring(orderPrefix.length());
            if (StringUtils.isNumeric(sequenceText))
            {
                nextSequence = Integer.parseInt(sequenceText) + 1;
            }
        }
        return orderPrefix + String.format("%03d", nextSequence);
    }

    private BigDecimal defaultAmount(BigDecimal amount)
    {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private BigDecimal parseDecimal(Object value)
    {
        if (value == null)
        {
            return null;
        }
        try
        {
            return new BigDecimal(String.valueOf(value).trim());
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    private Long parseLong(Object value)
    {
        if (value == null)
        {
            return null;
        }
        try
        {
            return Long.valueOf(String.valueOf(value).trim());
        }
        catch (Exception ex)
        {
            return null;
        }
    }
}
