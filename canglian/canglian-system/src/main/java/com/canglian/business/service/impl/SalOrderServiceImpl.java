package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.FinReceivable;
import com.canglian.business.domain.MdCustomer;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.MdWarehouse;
import com.canglian.business.domain.SalOrder;
import com.canglian.business.domain.SalOrderItem;
import com.canglian.business.domain.WmsOutbound;
import com.canglian.business.domain.WmsOutboundItem;
import com.canglian.business.mapper.FinReceivableMapper;
import com.canglian.business.mapper.MdCustomerMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.MdWarehouseMapper;
import com.canglian.business.mapper.SalOrderItemMapper;
import com.canglian.business.mapper.SalOrderMapper;
import com.canglian.business.mapper.WmsOutboundItemMapper;
import com.canglian.business.mapper.WmsOutboundMapper;
import com.canglian.business.service.IMdProductUnitService;
import com.canglian.business.service.IPriceResolverService;
import com.canglian.business.service.ISalOrderService;
import com.canglian.business.service.IWmsOutboundService;
import com.canglian.business.service.IWmsStockService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;
import com.canglian.system.service.ISysConfigService;

/**
 * 销售单据服务实现
 *
 * @author canglian
 */
@Service
public class SalOrderServiceImpl implements ISalOrderService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final String LOCK_ON_APPROVE_KEY = "canglian.stock.lockOnSaleApprove";

    @Autowired
    private SalOrderMapper salOrderMapper;

    @Autowired
    private SalOrderItemMapper salOrderItemMapper;

    @Autowired
    private MdCustomerMapper mdCustomerMapper;

    @Autowired(required = false)
    private MdProductMapper mdProductMapper;

    @Autowired(required = false)
    private MdWarehouseMapper mdWarehouseMapper;

    @Autowired
    private FinReceivableMapper finReceivableMapper;

    @Autowired
    private WmsOutboundMapper wmsOutboundMapper;

    @Autowired
    private WmsOutboundItemMapper wmsOutboundItemMapper;

    @Autowired
    private IPriceResolverService priceResolverService;

    @Autowired
    private IWmsOutboundService wmsOutboundService;

    @Autowired(required = false)
    private IWmsStockService wmsStockService;

    @Autowired(required = false)
    private IMdProductUnitService mdProductUnitService;

    @Autowired(required = false)
    private ISysConfigService sysConfigService;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Override
    public SalOrder selectSalOrderById(Long orderId)
    {
        SalOrder salOrder = salOrderMapper.selectSalOrderById(orderId);
        if (salOrder != null)
        {
            if (bizScopeHelper != null)
            {
                bizScopeHelper.assertRecordAccess(salOrder.getDeptId(), salOrder.getOwnerUserId(), salOrder.getWarehouseId());
            }
            salOrder.setOrderItemList(salOrderItemMapper.selectSalOrderItemListByOrderId(orderId));
        }
        return salOrder;
    }

    @Override
    public List<SalOrder> selectSalOrderList(SalOrder salOrder)
    {
        if (bizScopeHelper != null)
        {
            bizScopeHelper.applyScopeFilter(salOrder, "o", true, true);
        }
        return salOrderMapper.selectSalOrderList(salOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSalOrder(SalOrder salOrder)
    {
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(salOrder.getWarehouseId());
            if (salOrder.getDeptId() == null)
            {
                salOrder.setDeptId(bizScopeHelper.currentDeptId());
            }
            if (salOrder.getOwnerUserId() == null)
            {
                salOrder.setOwnerUserId(bizScopeHelper.currentUserId());
            }
        }
        prepareOrderForSave(salOrder, true);
        validateActiveMasterRefs(salOrder);
        int insertRows = salOrderMapper.insertSalOrder(salOrder);
        saveOrderItemList(salOrder);
        return insertRows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSalOrder(SalOrder salOrder)
    {
        SalOrder existingSalOrder = getExistingSalOrder(salOrder.getOrderId());
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(existingSalOrder.getDeptId(), existingSalOrder.getOwnerUserId(), existingSalOrder.getWarehouseId());
            bizScopeHelper.assertWarehouseAccess(salOrder.getWarehouseId());
        }
        validateEditable(existingSalOrder);
        preserveReadonlyFields(existingSalOrder, salOrder);
        prepareOrderForSave(salOrder, false);
        int updateRows = salOrderMapper.updateSalOrder(salOrder);
        salOrderItemMapper.deleteSalOrderItemByOrderId(salOrder.getOrderId());
        saveOrderItemList(salOrder);
        return updateRows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int approveSalOrder(Long orderId, String operator)
    {
        SalOrder salOrder = getExistingSalOrder(orderId);
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(salOrder.getDeptId(), salOrder.getOwnerUserId(), salOrder.getWarehouseId());
        }
        validateEditable(salOrder);
        if ("sale".equals(salOrder.getOrderType()) && salOrder.getCustomerId() == null)
        {
            throw new ServiceException("销货订单客户不能为空");
        }
        List<SalOrderItem> salOrderItemList = salOrderItemMapper.selectSalOrderItemListByOrderId(orderId);
        if (salOrderItemList == null || salOrderItemList.isEmpty())
        {
            throw new ServiceException("销售单据明细不能为空");
        }
        rebuildItemAmount(salOrder, salOrderItemList);
        salOrderItemMapper.deleteSalOrderItemByOrderId(orderId);
        salOrder.setOrderItemList(salOrderItemList);
        saveOrderItemList(salOrder);
        fillOrderTotal(salOrder, salOrderItemList);
        validateCustomerCreditLimit(salOrder);

        // P6-4：若开启销货订单审批锁库策略且指定了仓库，则对明细执行可用库存锁定并记录锁定标记
        if (shouldLockStockOnApprove() && "sale".equals(salOrder.getOrderType()) && salOrder.getWarehouseId() != null && wmsStockService != null)
        {
            for (SalOrderItem item : salOrderItemList)
            {
                BigDecimal lockQty = item.getBaseQuantity() != null && item.getBaseQuantity().compareTo(BigDecimal.ZERO) > 0
                    ? item.getBaseQuantity() : defaultAmount(item.getQuantity());
                wmsStockService.lockStockForSaleOrder(salOrder.getWarehouseId(), item.getProductId(), lockQty, salOrder.getOrderId(), operator);
            }
            salOrder.setStockLockedFlag("1");
        }
        else
        {
            salOrder.setStockLockedFlag("0");
        }

        salOrder.setStatus("1");
        salOrder.setBizStatus("approved");
        salOrder.setApproveBy(operator);
        salOrder.setApproveTime(new Date());
        salOrder.setUpdateBy(operator);
        return salOrderMapper.updateSalOrder(salOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unapproveSalOrder(Long orderId, String operator)
    {
        SalOrder salOrder = getExistingSalOrder(orderId);
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(salOrder.getDeptId(), salOrder.getOwnerUserId(), salOrder.getWarehouseId());
        }
        if (!"approved".equals(salOrder.getBizStatus()))
        {
            throw new ServiceException("仅已审批且尚未下推执行的销售单据允许反审批");
        }
        if ("quote".equals(salOrder.getOrderType()))
        {
            SalOrder q = new SalOrder();
            q.setSourceBillNo(salOrder.getOrderNo());
            List<SalOrder> derived = salOrderMapper.selectSalOrderList(q);
            if (derived != null)
            {
                for (SalOrder d : derived)
                {
                    if ("sale".equals(d.getOrderType()) && (salOrder.getOrderId().equals(d.getSourceBillId()) || salOrder.getOrderNo().equals(d.getSourceBillNo())))
                    {
                        throw new ServiceException("该报价单已下推销货订单，禁止反审批");
                    }
                }
            }
        }
        else
        {
            if (!findDownstreamOutbounds(salOrder).isEmpty())
            {
                throw new ServiceException("该销货订单已存在下游出库单，禁止反审批");
            }
            boolean wasLocked = "1".equals(salOrder.getStockLockedFlag()) || (salOrder.getStockLockedFlag() == null && shouldLockStockOnApprove());
            if (wasLocked && salOrder.getWarehouseId() != null && wmsStockService != null)
            {
                wmsStockService.releaseAllRemainingLocksForOrder(salOrder.getOrderId(), operator);
            }
            salOrder.setStockLockedFlag("0");
        }
        salOrder.setStatus("0");
        salOrder.setBizStatus("draft");
        salOrder.setApproveBy("");
        salOrder.setApproveTime(null);
        salOrder.setUpdateBy(operator);
        return salOrderMapper.updateSalOrder(salOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int closeSalOrder(Long orderId, String closeReason, String operator)
    {
        SalOrder salOrder = getExistingSalOrder(orderId);
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(salOrder.getDeptId(), salOrder.getOwnerUserId(), salOrder.getWarehouseId());
        }
        if (!"approved".equals(salOrder.getBizStatus()) && !"executing".equals(salOrder.getBizStatus()))
        {
            throw new ServiceException("仅审批通过或执行中的销售单据允许强制关闭");
        }
        boolean wasLocked = "1".equals(salOrder.getStockLockedFlag()) || (salOrder.getStockLockedFlag() == null && shouldLockStockOnApprove());
        if ("sale".equals(salOrder.getOrderType()) && wasLocked && salOrder.getWarehouseId() != null && wmsStockService != null)
        {
            List<SalOrderItem> items = salOrderItemMapper.selectSalOrderItemListByOrderId(orderId);
            if (items != null)
            {
                for (SalOrderItem item : items)
                {
                    BigDecimal remaining = defaultAmount(item.getQuantity()).subtract(defaultAmount(item.getShippedQty()));
                    if (remaining.compareTo(BigDecimal.ZERO) > 0)
                    {
                        BigDecimal ratio = item.getConvertRatio() != null && item.getConvertRatio().compareTo(BigDecimal.ZERO) > 0
                            ? item.getConvertRatio() : BigDecimal.ONE;
                        wmsStockService.releaseStockLockForSaleOrder(salOrder.getWarehouseId(), item.getProductId(),
                            remaining.multiply(ratio).setScale(4, RoundingMode.HALF_UP), salOrder.getOrderId(), operator);
                    }
                }
            }
        }
        salOrder.setStatus("2");
        salOrder.setBizStatus("closed");
        salOrder.setCloseReason(StringUtils.isNotEmpty(closeReason) ? closeReason.trim() : "业务手动强制关闭");
        salOrder.setUpdateBy(operator);
        return salOrderMapper.updateSalOrder(salOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int pushQuoteToSale(Long orderId, String operator)
    {
        SalOrder sourceSalOrder = getExistingSalOrder(orderId);
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(sourceSalOrder.getDeptId(), sourceSalOrder.getOwnerUserId(), sourceSalOrder.getWarehouseId());
        }
        if (!"quote".equals(sourceSalOrder.getOrderType()))
        {
            throw new ServiceException("仅报价单允许下推销货订单");
        }
        if (!"approved".equals(sourceSalOrder.getBizStatus()))
        {
            throw new ServiceException("报价单审批通过后才允许下推");
        }
        SalOrder querySalOrder = new SalOrder();
        querySalOrder.setSourceBillNo(sourceSalOrder.getOrderNo());
        List<SalOrder> pushedSalOrderList = salOrderMapper.selectSalOrderList(querySalOrder);
        for (SalOrder pushedSalOrder : pushedSalOrderList)
        {
            boolean sameSource = sourceSalOrder.getOrderId().equals(pushedSalOrder.getSourceBillId())
                || (StringUtils.isNotEmpty(sourceSalOrder.getOrderNo()) && sourceSalOrder.getOrderNo().equals(pushedSalOrder.getSourceBillNo()));
            if ("sale".equals(pushedSalOrder.getOrderType()) && sameSource)
            {
                throw new ServiceException("该报价单已下推销货订单");
            }
        }
        SalOrder targetSalOrder = buildDerivedSaleOrder(sourceSalOrder, operator);
        if (bizScopeHelper != null)
        {
            targetSalOrder.setDeptId(bizScopeHelper.currentDeptId());
            targetSalOrder.setOwnerUserId(bizScopeHelper.currentUserId());
        }
        salOrderMapper.insertSalOrder(targetSalOrder);
        saveOrderItemList(targetSalOrder);
        sourceSalOrder.setBizStatus("closed");
        sourceSalOrder.setStatus("2");
        sourceSalOrder.setUpdateBy(operator);
        salOrderMapper.updateSalOrder(sourceSalOrder);
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int pushSaleOrderToOutbound(Long orderId, String operator)
    {
        return pushSaleOrderToOutbound(orderId, null, operator);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int pushSaleOrderToOutbound(Long orderId, List<Map<String, Object>> pushItems, String operator)
    {
        SalOrder salOrder = getExistingSalOrder(orderId);
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(salOrder.getDeptId(), salOrder.getOwnerUserId(), salOrder.getWarehouseId());
        }
        if (!"sale".equals(salOrder.getOrderType()))
        {
            throw new ServiceException("仅销货订单允许下推出库单");
        }
        if (!"approved".equals(salOrder.getBizStatus()) && !"executing".equals(salOrder.getBizStatus()))
        {
            throw new ServiceException("销货订单处于审批通过或执行中状态才允许下推出库单");
        }
        if (salOrder.getWarehouseId() == null)
        {
            throw new ServiceException("销货订单出库仓库不能为空，请先完善仓库信息");
        }
        if (salOrder.getCustomerId() == null)
        {
            throw new ServiceException("销货订单客户不能为空");
        }
        List<SalOrderItem> salOrderItemList = salOrderItemMapper.selectSalOrderItemListByOrderId(orderId);
        if (salOrderItemList == null || salOrderItemList.isEmpty())
        {
            throw new ServiceException("销货订单明细不能为空");
        }

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
                Long itemId = parseLong(req.get("orderItemId"));
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

        List<WmsOutboundItem> itemsToCreate = new ArrayList<WmsOutboundItem>();
        Map<Long, BigDecimal> updatedShippedMap = new HashMap<Long, BigDecimal>();
        BigDecimal totalGrossAmount = BigDecimal.ZERO;
        for (SalOrderItem item : salOrderItemList)
        {
            totalGrossAmount = totalGrossAmount.add(defaultAmount(item.getAmount()));
        }
        BigDecimal orderDiscount = defaultAmount(salOrder.getDiscountAmount());
        Map<Long, BigDecimal> lineDiscountShare = new HashMap<Long, BigDecimal>();
        if (orderDiscount.compareTo(BigDecimal.ZERO) > 0 && totalGrossAmount.compareTo(BigDecimal.ZERO) > 0)
        {
            BigDecimal allocated = BigDecimal.ZERO;
            for (int idx = 0; idx < salOrderItemList.size(); idx++)
            {
                SalOrderItem it = salOrderItemList.get(idx);
                BigDecimal share;
                if (idx == salOrderItemList.size() - 1)
                {
                    share = orderDiscount.subtract(allocated).max(BigDecimal.ZERO);
                }
                else
                {
                    share = orderDiscount.multiply(defaultAmount(it.getAmount())).divide(totalGrossAmount, 2, RoundingMode.HALF_UP);
                    allocated = allocated.add(share);
                }
                lineDiscountShare.put(it.getOrderItemId(), share);
            }
        }
        BigDecimal pushHeaderDiscount = BigDecimal.ZERO;
        BigDecimal pushTaxTotal = BigDecimal.ZERO;

        for (SalOrderItem salOrderItem : salOrderItemList)
        {
            BigDecimal orderedQty = defaultAmount(salOrderItem.getQuantity());
            BigDecimal alreadyShipped = defaultAmount(salOrderItem.getShippedQty());
            BigDecimal remaining = orderedQty.subtract(alreadyShipped);
            if (remaining.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            BigDecimal pushQty;
            if (!partialMode)
            {
                pushQty = remaining;
            }
            else if (requestedByItemId.containsKey(salOrderItem.getOrderItemId()))
            {
                pushQty = requestedByItemId.get(salOrderItem.getOrderItemId());
            }
            else if (requestedByProductId.containsKey(salOrderItem.getProductId()))
            {
                pushQty = requestedByProductId.get(salOrderItem.getProductId());
            }
            else
            {
                continue;
            }
            if (pushQty.compareTo(remaining) > 0)
            {
                throw new ServiceException("商品 #" + salOrderItem.getProductId() + " 本次下推数量(" + pushQty + ")超过剩余可下推数量(" + remaining + ")");
            }
            BigDecimal lineShare = defaultAmount(lineDiscountShare.get(salOrderItem.getOrderItemId()));
            BigDecimal lineNetAmount = defaultAmount(salOrderItem.getAmount()).subtract(lineShare).max(BigDecimal.ZERO);
            BigDecimal effectiveUnitPrice = orderedQty.compareTo(BigDecimal.ZERO) > 0
                ? lineNetAmount.divide(orderedQty, 4, RoundingMode.HALF_UP)
                : defaultAmount(salOrderItem.getPrice());
            BigDecimal pushAmount = orderedQty.compareTo(pushQty) == 0
                ? lineNetAmount.setScale(2, RoundingMode.HALF_UP)
                : effectiveUnitPrice.multiply(pushQty).setScale(2, RoundingMode.HALF_UP);
            BigDecimal pushLineTax = orderedQty.compareTo(BigDecimal.ZERO) > 0
                ? defaultAmount(salOrderItem.getTaxAmount()).multiply(pushQty).divide(orderedQty, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
            BigDecimal pushLineDiscount = orderedQty.compareTo(BigDecimal.ZERO) > 0
                ? lineShare.multiply(pushQty).divide(orderedQty, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
            BigDecimal ratio = salOrderItem.getConvertRatio() != null && salOrderItem.getConvertRatio().compareTo(BigDecimal.ZERO) > 0
                ? salOrderItem.getConvertRatio() : BigDecimal.ONE;

            WmsOutboundItem wmsOutboundItem = new WmsOutboundItem();
            wmsOutboundItem.setSourceItemId(salOrderItem.getOrderItemId());
            wmsOutboundItem.setProductId(salOrderItem.getProductId());
            wmsOutboundItem.setLocationId(0L);
            wmsOutboundItem.setBatchNo("");
            wmsOutboundItem.setUnitName(salOrderItem.getUnitName());
            wmsOutboundItem.setConvertRatio(ratio);
            wmsOutboundItem.setBaseQuantity(pushQty.multiply(ratio).setScale(4, RoundingMode.HALF_UP));
            wmsOutboundItem.setQuantity(pushQty);
            wmsOutboundItem.setPrice(effectiveUnitPrice.setScale(2, RoundingMode.HALF_UP));
            wmsOutboundItem.setDiscountRate(salOrderItem.getDiscountRate() != null ? salOrderItem.getDiscountRate() : HUNDRED);
            wmsOutboundItem.setTaxRate(salOrderItem.getTaxRate() != null ? salOrderItem.getTaxRate() : BigDecimal.ZERO);
            wmsOutboundItem.setTaxAmount(pushLineTax);
            wmsOutboundItem.setAmount(pushAmount);
            itemsToCreate.add(wmsOutboundItem);
            pushHeaderDiscount = pushHeaderDiscount.add(pushLineDiscount);
            pushTaxTotal = pushTaxTotal.add(pushLineTax);
            updatedShippedMap.put(salOrderItem.getOrderItemId(), alreadyShipped.add(pushQty));
        }

        if (itemsToCreate.isEmpty())
        {
            throw new ServiceException("该销货订单已全部下推完毕，无可下推的剩余数量");
        }

        WmsOutbound wmsOutbound = new WmsOutbound();
        wmsOutbound.setOutboundType("sale_order");
        wmsOutbound.setCustomerId(salOrder.getCustomerId());
        wmsOutbound.setWarehouseId(salOrder.getWarehouseId());
        wmsOutbound.setSourceBillType("sale_order");
        wmsOutbound.setSourceBillId(salOrder.getOrderId());
        wmsOutbound.setSourceBillNo(salOrder.getOrderNo());
        wmsOutbound.setBusinessDate(new Date());
        wmsOutbound.setDiscountAmount(pushHeaderDiscount);
        wmsOutbound.setTaxTotalAmount(pushTaxTotal);
        wmsOutbound.setStatus("0");
        wmsOutbound.setBizStatus("draft");
        wmsOutbound.setDeliveryStatus("0");
        wmsOutbound.setCreateBy(operator);
        wmsOutbound.setRemark("由销货订单 " + salOrder.getOrderNo() + " 分批下推生成");
        wmsOutboundService.insertWmsOutbound(wmsOutbound);

        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (WmsOutboundItem item : itemsToCreate)
        {
            item.setOutboundId(wmsOutbound.getOutboundId());
            wmsOutboundItemMapper.insertWmsOutboundItem(item);
            totalQty = totalQty.add(defaultAmount(item.getQuantity()));
            totalAmount = totalAmount.add(defaultAmount(item.getAmount()));
        }
        wmsOutbound.setTotalQty(totalQty);
        wmsOutbound.setTotalAmount(totalAmount);
        wmsOutboundMapper.updateWmsOutbound(wmsOutbound);

        for (Map.Entry<Long, BigDecimal> entry : updatedShippedMap.entrySet())
        {
            salOrderItemMapper.updateShippedQty(entry.getKey(), entry.getValue());
        }

        salOrder.setBizStatus("executing");
        salOrder.setUpdateBy(operator);
        salOrderMapper.updateSalOrder(salOrder);
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncOrderProgressFromWms(Long orderId, String operator)
    {
        if (orderId == null)
        {
            return;
        }
        SalOrder salOrder = salOrderMapper.selectSalOrderById(orderId);
        if (salOrder == null || !"sale".equals(salOrder.getOrderType())
            || "closed".equals(salOrder.getBizStatus()) || "draft".equals(salOrder.getBizStatus()))
        {
            return;
        }
        List<SalOrderItem> orderItems = salOrderItemMapper.selectSalOrderItemListByOrderId(orderId);
        if (orderItems == null || orderItems.isEmpty())
        {
            return;
        }
        List<WmsOutbound> outbounds = findDownstreamOutbounds(salOrder);
        Map<Long, BigDecimal> pushedByItemId = new HashMap<Long, BigDecimal>();
        Map<Long, BigDecimal> pushedByProductId = new HashMap<Long, BigDecimal>();
        boolean hasDraftOutbound = false;
        boolean hasAnyActiveOutbound = false;

        for (WmsOutbound out : outbounds)
        {
            if ("1".equals(out.getIsReversal()) || "1".equals(out.getReversedFlag()) || "3".equals(out.getDeliveryStatus()))
            {
                continue;
            }
            hasAnyActiveOutbound = true;
            if (!"completed".equals(out.getBizStatus()))
            {
                hasDraftOutbound = true;
            }
            WmsOutboundItem q = new WmsOutboundItem();
            q.setOutboundId(out.getOutboundId());
            List<WmsOutboundItem> outItems = wmsOutboundItemMapper.selectWmsOutboundItemList(q);
            if (outItems != null)
            {
                for (WmsOutboundItem outItem : outItems)
                {
                    if (outItem.getSourceItemId() != null)
                    {
                        pushedByItemId.put(outItem.getSourceItemId(),
                            defaultAmount(pushedByItemId.get(outItem.getSourceItemId())).add(defaultAmount(outItem.getQuantity())));
                    }
                    else if (outItem.getProductId() != null)
                    {
                        pushedByProductId.put(outItem.getProductId(),
                            defaultAmount(pushedByProductId.get(outItem.getProductId())).add(defaultAmount(outItem.getQuantity())));
                    }
                }
            }
        }

        boolean allFulfilled = true;
        for (SalOrderItem item : orderItems)
        {
            BigDecimal shipped = defaultAmount(pushedByItemId.get(item.getOrderItemId()));
            if (shipped.compareTo(BigDecimal.ZERO) == 0 && pushedByProductId.containsKey(item.getProductId()))
            {
                shipped = defaultAmount(pushedByProductId.get(item.getProductId())).min(defaultAmount(item.getQuantity()));
            }
            salOrderItemMapper.updateShippedQty(item.getOrderItemId(), shipped);
            if (shipped.compareTo(defaultAmount(item.getQuantity())) < 0)
            {
                allFulfilled = false;
            }
        }

        if (!hasAnyActiveOutbound)
        {
            salOrder.setBizStatus("approved");
            salOrder.setStatus("1");
        }
        else if (allFulfilled && !hasDraftOutbound)
        {
            salOrder.setBizStatus("completed");
            salOrder.setStatus("2");
        }
        else
        {
            salOrder.setBizStatus("executing");
            salOrder.setStatus("1");
        }
        salOrder.setUpdateBy(operator);
        salOrderMapper.updateSalOrder(salOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalOrderById(Long orderId)
    {
        SalOrder salOrder = getExistingSalOrder(orderId);
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(salOrder.getDeptId(), salOrder.getOwnerUserId(), salOrder.getWarehouseId());
        }
        validateEditable(salOrder);
        salOrderItemMapper.deleteSalOrderItemByOrderId(orderId);
        int rows = salOrderMapper.deleteSalOrderById(orderId);
        reopenSourceQuoteIfDerived(salOrder);
        return rows;
    }

    private void reopenSourceQuoteIfDerived(SalOrder deletedSaleOrder)
    {
        if (deletedSaleOrder == null || !"sale".equals(deletedSaleOrder.getOrderType()))
        {
            return;
        }
        if ("quote".equals(deletedSaleOrder.getSourceBillType()) && deletedSaleOrder.getSourceBillId() != null)
        {
            SalOrder quote = salOrderMapper.selectSalOrderById(deletedSaleOrder.getSourceBillId());
            if (quote != null && "quote".equals(quote.getOrderType()) && "closed".equals(quote.getBizStatus()))
            {
                quote.setBizStatus("approved");
                quote.setStatus("1");
                salOrderMapper.updateSalOrder(quote);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalOrderByIds(Long[] orderIds)
    {
        for (Long orderId : orderIds)
        {
            SalOrder salOrder = getExistingSalOrder(orderId);
            if (bizScopeHelper != null)
            {
                bizScopeHelper.assertRecordAccess(salOrder.getDeptId(), salOrder.getOwnerUserId(), salOrder.getWarehouseId());
            }
            validateEditable(salOrder);
            salOrderItemMapper.deleteSalOrderItemByOrderId(orderId);
            reopenSourceQuoteIfDerived(salOrder);
        }
        return salOrderMapper.deleteSalOrderByIds(orderIds);
    }

    private boolean shouldLockStockOnApprove()
    {
        if (sysConfigService == null)
        {
            return false;
        }
        String cfg = sysConfigService.selectConfigByKey(LOCK_ON_APPROVE_KEY);
        return "true".equalsIgnoreCase(cfg) || "1".equals(cfg);
    }

    private List<WmsOutbound> findDownstreamOutbounds(SalOrder salOrder)
    {
        WmsOutbound query = new WmsOutbound();
        query.setSourceBillType("sale_order");
        query.setSourceBillId(salOrder.getOrderId());
        List<WmsOutbound> list = wmsOutboundMapper.selectWmsOutboundList(query);
        List<WmsOutbound> matched = new ArrayList<WmsOutbound>();
        if (list != null)
        {
            for (WmsOutbound out : list)
            {
                if (salOrder.getOrderId().equals(out.getSourceBillId())
                    || (StringUtils.isNotEmpty(salOrder.getOrderNo()) && salOrder.getOrderNo().equals(out.getSourceBillNo())))
                {
                    matched.add(out);
                }
            }
        }
        return matched;
    }

    private void prepareOrderForSave(SalOrder salOrder, boolean isNew)
    {
        if (StringUtils.isEmpty(salOrder.getOrderType()))
        {
            salOrder.setOrderType("sale");
        }
        if (StringUtils.isEmpty(salOrder.getOrderNo()))
        {
            salOrder.setOrderNo(generateOrderNo(salOrder.getOrderType()));
        }
        if (salOrder.getBusinessDate() == null)
        {
            salOrder.setBusinessDate(new Date());
        }
        if (salOrder.getDiscountAmount() == null)
        {
            salOrder.setDiscountAmount(BigDecimal.ZERO);
        }
        if (salOrder.getDiscountAmount().compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("整单优惠金额不能为负数");
        }
        if (isNew)
        {
            salOrder.setStatus("0");
            salOrder.setBizStatus("draft");
            salOrder.setApproveBy("");
            salOrder.setApproveTime(null);
        }
        List<SalOrderItem> salOrderItemList = normalizeOrderItemList(salOrder.getOrderItemList());
        rebuildItemAmount(salOrder, salOrderItemList);
        fillOrderTotal(salOrder, salOrderItemList);
        salOrder.setOrderItemList(salOrderItemList);
    }

    private void saveOrderItemList(SalOrder salOrder)
    {
        for (SalOrderItem salOrderItem : salOrder.getOrderItemList())
        {
            salOrderItem.setOrderId(salOrder.getOrderId());
            salOrderItemMapper.insertSalOrderItem(salOrderItem);
        }
    }

    private List<SalOrderItem> normalizeOrderItemList(List<SalOrderItem> orderItemList)
    {
        List<SalOrderItem> normalizedOrderItemList = orderItemList == null ? new ArrayList<SalOrderItem>() : orderItemList;
        if (normalizedOrderItemList.isEmpty())
        {
            throw new ServiceException("销售单据明细不能为空");
        }
        return normalizedOrderItemList;
    }

    private void rebuildItemAmount(SalOrder salOrder, List<SalOrderItem> salOrderItemList)
    {
        for (SalOrderItem salOrderItem : salOrderItemList)
        {
            if (salOrderItem.getProductId() == null)
            {
                throw new ServiceException("销售单据明细商品不能为空");
            }
            BigDecimal itemQuantity = defaultAmount(salOrderItem.getQuantity());
            if (itemQuantity.compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("销售单据明细数量必须大于0");
            }
            MdProduct mdProduct = mdProductMapper == null ? null : mdProductMapper.selectMdProductById(salOrderItem.getProductId());
            if (mdProduct != null && "1".equals(mdProduct.getStatus()))
            {
                throw new ServiceException("商品[" + mdProduct.getProductName() + "]已停用，禁止下单");
            }
            if (StringUtils.isEmpty(salOrderItem.getUnitName()) && mdProduct != null)
            {
                salOrderItem.setUnitName(StringUtils.isNotEmpty(mdProduct.getSaleUnit()) ? mdProduct.getSaleUnit()
                    : (StringUtils.isNotEmpty(mdProduct.getBaseUnit()) ? mdProduct.getBaseUnit() : mdProduct.getUnitName()));
            }
            BigDecimal ratio = mdProductUnitService != null && StringUtils.isNotEmpty(salOrderItem.getUnitName())
                ? mdProductUnitService.resolveConvertRatio(salOrderItem.getProductId(), salOrderItem.getUnitName())
                : (salOrderItem.getConvertRatio() != null && salOrderItem.getConvertRatio().compareTo(BigDecimal.ZERO) > 0
                    ? salOrderItem.getConvertRatio() : BigDecimal.ONE);
            salOrderItem.setConvertRatio(ratio);
            salOrderItem.setBaseQuantity(itemQuantity.multiply(ratio).setScale(4, RoundingMode.HALF_UP));

            if (salOrderItem.getPrice() == null || salOrderItem.getPrice().compareTo(BigDecimal.ZERO) <= 0)
            {
                Map<String, Object> priceDetail = priceResolverService.resolveSalePriceDetail(
                    salOrder.getCustomerId(), salOrderItem.getProductId(), salOrderItem.getUnitName());
                BigDecimal resolved = priceDetail != null && priceDetail.get("price") instanceof BigDecimal
                    ? (BigDecimal) priceDetail.get("price") : BigDecimal.ZERO;
                salOrderItem.setPrice(resolved.setScale(2, RoundingMode.HALF_UP));
            }
            BigDecimal discountRate = salOrderItem.getDiscountRate() == null ? HUNDRED : salOrderItem.getDiscountRate();
            if (discountRate.compareTo(BigDecimal.ZERO) <= 0 || discountRate.compareTo(HUNDRED) > 0)
            {
                throw new ServiceException("明细折扣率必须在 0 到 100 之间");
            }
            BigDecimal taxRate = salOrderItem.getTaxRate() == null ? BigDecimal.ZERO : salOrderItem.getTaxRate();
            if (taxRate.compareTo(BigDecimal.ZERO) < 0 || taxRate.compareTo(HUNDRED) > 0)
            {
                throw new ServiceException("明细税率必须在 0 到 100 之间");
            }
            salOrderItem.setDiscountRate(discountRate);
            salOrderItem.setTaxRate(taxRate);
            salOrderItem.setQuantity(itemQuantity);

            BigDecimal netAmount = defaultAmount(salOrderItem.getPrice())
                .multiply(itemQuantity)
                .multiply(discountRate)
                .divide(HUNDRED, 2, RoundingMode.HALF_UP);
            BigDecimal taxAmount = netAmount.multiply(taxRate).divide(HUNDRED, 2, RoundingMode.HALF_UP);
            salOrderItem.setTaxAmount(taxAmount);
            salOrderItem.setAmount(netAmount.add(taxAmount));
            if (salOrderItem.getShippedQty() == null)
            {
                salOrderItem.setShippedQty(BigDecimal.ZERO);
            }
        }
    }

    private void fillOrderTotal(SalOrder salOrder, List<SalOrderItem> salOrderItemList)
    {
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal taxTotalAmount = BigDecimal.ZERO;
        for (SalOrderItem salOrderItem : salOrderItemList)
        {
            totalQuantity = totalQuantity.add(defaultAmount(salOrderItem.getQuantity()));
            totalAmount = totalAmount.add(defaultAmount(salOrderItem.getAmount()));
            taxTotalAmount = taxTotalAmount.add(defaultAmount(salOrderItem.getTaxAmount()));
        }
        BigDecimal discount = defaultAmount(salOrder.getDiscountAmount());
        if (discount.compareTo(totalAmount) > 0)
        {
            throw new ServiceException("整单优惠金额不能超过订单总金额");
        }
        salOrder.setTotalQty(totalQuantity);
        salOrder.setTaxTotalAmount(taxTotalAmount);
        salOrder.setTotalAmount(totalAmount.subtract(discount));
    }

    private void validateActiveMasterRefs(SalOrder salOrder)
    {
        if (salOrder == null)
        {
            return;
        }
        if (salOrder.getCustomerId() != null && mdCustomerMapper != null)
        {
            MdCustomer c = mdCustomerMapper.selectMdCustomerById(salOrder.getCustomerId());
            if (c != null && "1".equals(c.getStatus()))
            {
                throw new ServiceException("客户[" + c.getCustomerName() + "]已停用，禁止下单");
            }
        }
        if (salOrder.getWarehouseId() != null && mdWarehouseMapper != null)
        {
            MdWarehouse w = mdWarehouseMapper.selectMdWarehouseById(salOrder.getWarehouseId());
            if (w != null && "1".equals(w.getStatus()))
            {
                throw new ServiceException("仓库[" + w.getWarehouseName() + "]已停用，禁止下单");
            }
        }
    }

    private void validateCustomerCreditLimit(SalOrder salOrder)
    {
        if (!"sale".equals(salOrder.getOrderType()) || salOrder.getCustomerId() == null)
        {
            return;
        }
        MdCustomer mdCustomer = mdCustomerMapper.selectMdCustomerById(salOrder.getCustomerId());
        if (mdCustomer == null || defaultAmount(mdCustomer.getCreditLimit()).compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }
        // 1. 统计所有已确认且未结清的应收账款净额（扣除已收金额与已核销坏账金额）
        FinReceivable finReceivableQuery = new FinReceivable();
        finReceivableQuery.setCustomerId(salOrder.getCustomerId());
        List<FinReceivable> finReceivableList = finReceivableMapper.selectFinReceivableList(finReceivableQuery);
        BigDecimal outstandingReceivableAmount = BigDecimal.ZERO;
        if (finReceivableList != null)
        {
            for (FinReceivable finReceivable : finReceivableList)
            {
                if (!"confirmed".equals(finReceivable.getBizStatus()))
                {
                    continue;
                }
                if ("2".equals(finReceivable.getStatus()))
                {
                    continue;
                }
                BigDecimal unsettledAmount = defaultAmount(finReceivable.getAmount())
                    .subtract(defaultAmount(finReceivable.getReceivedAmount()));
                if ("2".equals(finReceivable.getBillDirection()))
                {
                    // 红字应收可冲抵客户敞口
                    outstandingReceivableAmount = outstandingReceivableAmount.add(unsettledAmount);
                }
                else if (unsettledAmount.compareTo(BigDecimal.ZERO) > 0)
                {
                    outstandingReceivableAmount = outstandingReceivableAmount.add(unsettledAmount);
                }
            }
        }
        if (outstandingReceivableAmount.compareTo(BigDecimal.ZERO) < 0)
        {
            outstandingReceivableAmount = BigDecimal.ZERO;
        }

        // 2. 统计该客户所有有效出库单：按订单汇总已签收金额（已转AR），并计入未签收的直接出库敞口
        WmsOutbound outQuery = new WmsOutbound();
        outQuery.setCustomerId(salOrder.getCustomerId());
        List<WmsOutbound> customerOutbounds = wmsOutboundMapper.selectWmsOutboundList(outQuery);
        Map<Long, BigDecimal> signedAmountByOrderId = new HashMap<Long, BigDecimal>();
        if (customerOutbounds != null)
        {
            for (WmsOutbound ob : customerOutbounds)
            {
                if (!"1".equals(ob.getStatus()) || "1".equals(ob.getIsReversal()) || "1".equals(ob.getReversedFlag()) || "3".equals(ob.getDeliveryStatus()))
                {
                    continue;
                }
                if ("2".equals(ob.getDeliveryStatus()))
                {
                    if ("sale_order".equals(ob.getSourceBillType()) && ob.getSourceBillId() != null)
                    {
                        signedAmountByOrderId.put(ob.getSourceBillId(),
                            defaultAmount(signedAmountByOrderId.get(ob.getSourceBillId())).add(defaultAmount(ob.getTotalAmount())));
                    }
                }
                else if (ob.getSourceBillId() == null)
                {
                    // 已审核未签收的直接出库单也计入信用敞口
                    outstandingReceivableAmount = outstandingReceivableAmount.add(defaultAmount(ob.getTotalAmount()));
                }
            }
        }

        // 3. 对每个在途(approved/executing)销货订单，仅扣除已签收转AR部分，其余未转AR余额全部计入敞口（杜绝1%部分签收绕过信用额度）
        SalOrder openOrderQuery = new SalOrder();
        openOrderQuery.setOrderType("sale");
        openOrderQuery.setCustomerId(salOrder.getCustomerId());
        List<SalOrder> customerOrders = salOrderMapper.selectSalOrderList(openOrderQuery);
        if (customerOrders != null)
        {
            for (SalOrder otherOrder : customerOrders)
            {
                if (salOrder.getOrderId() != null && salOrder.getOrderId().equals(otherOrder.getOrderId()))
                {
                    continue;
                }
                if (!"approved".equals(otherOrder.getBizStatus()) && !"executing".equals(otherOrder.getBizStatus()))
                {
                    continue;
                }
                BigDecimal alreadyBilled = defaultAmount(signedAmountByOrderId.get(otherOrder.getOrderId()));
                BigDecimal remainingExposure = defaultAmount(otherOrder.getTotalAmount()).subtract(alreadyBilled);
                if (remainingExposure.compareTo(BigDecimal.ZERO) > 0)
                {
                    outstandingReceivableAmount = outstandingReceivableAmount.add(remainingExposure);
                }
            }
        }
        BigDecimal projectedReceivableAmount = outstandingReceivableAmount.add(defaultAmount(salOrder.getTotalAmount()));
        if (projectedReceivableAmount.compareTo(defaultAmount(mdCustomer.getCreditLimit())) > 0)
        {
            throw new ServiceException("客户信用额度不足（当前应收及在途敞口 " + outstandingReceivableAmount.setScale(2, RoundingMode.HALF_UP)
                + " + 本单 " + defaultAmount(salOrder.getTotalAmount()).setScale(2, RoundingMode.HALF_UP)
                + " > 授信额度 " + defaultAmount(mdCustomer.getCreditLimit()).setScale(2, RoundingMode.HALF_UP) + "）");
        }
    }

    private SalOrder getExistingSalOrder(Long orderId)
    {
        SalOrder salOrder = salOrderMapper.selectSalOrderById(orderId);
        if (salOrder == null)
        {
            throw new ServiceException("销售单据不存在");
        }
        return salOrder;
    }

    private void validateEditable(SalOrder salOrder)
    {
        if (!"draft".equals(salOrder.getBizStatus()))
        {
            throw new ServiceException("当前销售单据状态不允许编辑");
        }
    }

    private void preserveReadonlyFields(SalOrder existingSalOrder, SalOrder salOrder)
    {
        salOrder.setOrderNo(existingSalOrder.getOrderNo());
        salOrder.setOrderType(existingSalOrder.getOrderType());
        salOrder.setStatus(existingSalOrder.getStatus());
        salOrder.setBizStatus(existingSalOrder.getBizStatus());
        salOrder.setApproveBy(existingSalOrder.getApproveBy());
        salOrder.setApproveTime(existingSalOrder.getApproveTime());
    }

    private SalOrder buildDerivedSaleOrder(SalOrder sourceSalOrder, String operator)
    {
        SalOrder targetSalOrder = new SalOrder();
        targetSalOrder.setOrderNo(generateOrderNo("sale"));
        targetSalOrder.setOrderType("sale");
        targetSalOrder.setCustomerId(sourceSalOrder.getCustomerId());
        targetSalOrder.setWarehouseId(sourceSalOrder.getWarehouseId());
        targetSalOrder.setSourceBillType("quote");
        targetSalOrder.setSourceBillId(sourceSalOrder.getOrderId());
        targetSalOrder.setSourceBillNo(sourceSalOrder.getOrderNo());
        targetSalOrder.setBusinessDate(sourceSalOrder.getBusinessDate());
        targetSalOrder.setExpectedDate(sourceSalOrder.getExpectedDate());
        targetSalOrder.setDiscountAmount(defaultAmount(sourceSalOrder.getDiscountAmount()));
        targetSalOrder.setStatus("0");
        targetSalOrder.setBizStatus("draft");
        targetSalOrder.setCreateBy(operator);
        targetSalOrder.setRemark("由报价单下推生成");
        List<SalOrderItem> sourceOrderItemList = salOrderItemMapper.selectSalOrderItemListByOrderId(sourceSalOrder.getOrderId());
        List<SalOrderItem> targetOrderItemList = new ArrayList<SalOrderItem>();
        for (SalOrderItem sourceOrderItem : sourceOrderItemList)
        {
            SalOrderItem targetOrderItem = new SalOrderItem();
            targetOrderItem.setProductId(sourceOrderItem.getProductId());
            targetOrderItem.setUnitName(sourceOrderItem.getUnitName());
            targetOrderItem.setConvertRatio(sourceOrderItem.getConvertRatio());
            targetOrderItem.setBaseQuantity(sourceOrderItem.getBaseQuantity());
            targetOrderItem.setQuantity(sourceOrderItem.getQuantity());
            targetOrderItem.setPrice(sourceOrderItem.getPrice());
            targetOrderItem.setDiscountRate(sourceOrderItem.getDiscountRate());
            targetOrderItem.setTaxRate(sourceOrderItem.getTaxRate());
            targetOrderItem.setTaxAmount(sourceOrderItem.getTaxAmount());
            targetOrderItem.setAmount(sourceOrderItem.getAmount());
            targetOrderItem.setRemark(sourceOrderItem.getRemark());
            targetOrderItemList.add(targetOrderItem);
        }
        targetSalOrder.setOrderItemList(targetOrderItemList);
        fillOrderTotal(targetSalOrder, targetOrderItemList);
        return targetSalOrder;
    }

    private String generateOrderNo(String orderType)
    {
        String orderPrefix = ("quote".equals(orderType) ? "quo" : "sal") + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String currentMaxOrderNo = salOrderMapper.selectMaxOrderNoByPrefix(orderPrefix);
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
