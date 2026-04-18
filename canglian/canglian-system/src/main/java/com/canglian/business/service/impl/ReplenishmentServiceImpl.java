package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.PurOrder;
import com.canglian.business.domain.PurOrderItem;
import com.canglian.business.domain.ReplenishmentSuggestion;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsStockLog;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.PurOrderItemMapper;
import com.canglian.business.mapper.PurOrderMapper;
import com.canglian.business.mapper.WmsStockLogMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IReplenishmentService;

/**
 * 补货建议服务实现
 * 
 * @author canglian
 */
@Service
public class ReplenishmentServiceImpl implements IReplenishmentService
{
    @Autowired
    private WmsStockMapper wmsStockMapper;

    @Autowired
    private WmsStockLogMapper wmsStockLogMapper;

    @Autowired
    private PurOrderMapper purOrderMapper;

    @Autowired
    private PurOrderItemMapper purOrderItemMapper;

    @Autowired
    private MdProductMapper mdProductMapper;

    /**
     * 查询补货建议列表
     * 
     * @param warehouseId 仓库编号
     * @return 补货建议列表
     */
    @Override
    public List<ReplenishmentSuggestion> selectReplenishmentSuggestionList(Long warehouseId)
    {
        WmsStock queryWmsStock = new WmsStock();
        queryWmsStock.setWarehouseId(warehouseId);
        List<WmsStock> warningStockList = wmsStockMapper.selectWmsStockList(queryWmsStock);
        Map<String, ReplenishmentSuggestion> suggestionMap = new LinkedHashMap<String, ReplenishmentSuggestion>();
        Map<Long, MdProduct> productCacheMap = new LinkedHashMap<Long, MdProduct>();
        for (WmsStock wmsStock : warningStockList)
        {
            if (wmsStock.getProductId() == null)
            {
                continue;
            }
            MdProduct mdProduct = loadProduct(wmsStock.getProductId(), productCacheMap);
            BigDecimal warningMinQty = resolveWarningMinQty(wmsStock, mdProduct);
            if (warningMinQty.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            if (defaultAmount(wmsStock.getQuantity()).compareTo(warningMinQty) >= 0)
            {
                continue;
            }
            String suggestionKey = buildSuggestionKey(wmsStock.getWarehouseId(), wmsStock.getProductId());
            ReplenishmentSuggestion replenishmentSuggestion = suggestionMap.get(suggestionKey);
            if (replenishmentSuggestion == null)
            {
                replenishmentSuggestion = createSuggestion(wmsStock, mdProduct, warningMinQty);
                suggestionMap.put(suggestionKey, replenishmentSuggestion);
            }
            else
            {
                replenishmentSuggestion.setCurrentQty(defaultAmount(replenishmentSuggestion.getCurrentQty()).add(defaultAmount(wmsStock.getQuantity())));
                replenishmentSuggestion.setWarningMinQty(defaultAmount(replenishmentSuggestion.getWarningMinQty()).max(warningMinQty));
            }
        }

        List<ReplenishmentSuggestion> replenishmentSuggestionList = new ArrayList<ReplenishmentSuggestion>();
        for (ReplenishmentSuggestion replenishmentSuggestion : suggestionMap.values())
        {
            BigDecimal recentOutboundQty = calculateRecentOutboundQty(replenishmentSuggestion.getWarehouseId(), replenishmentSuggestion.getProductId());
            BigDecimal pendingPurchaseQty = calculatePendingPurchaseQty(replenishmentSuggestion.getWarehouseId(), replenishmentSuggestion.getProductId());
            if (pendingPurchaseQty.compareTo(BigDecimal.ZERO) > 0)
            {
                continue;
            }
            BigDecimal suggestedQty = recentOutboundQty.add(defaultAmount(replenishmentSuggestion.getWarningMinQty()))
                .subtract(defaultAmount(replenishmentSuggestion.getCurrentQty()));
            if (suggestedQty.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            replenishmentSuggestion.setRecentOutboundQty(recentOutboundQty);
            replenishmentSuggestion.setPendingPurchaseQty(pendingPurchaseQty);
            replenishmentSuggestion.setSuggestedQty(suggestedQty);
            replenishmentSuggestion.setSuggestionReason("近30天销量 + 最小库存阈值");
            replenishmentSuggestionList.add(replenishmentSuggestion);
        }
        return replenishmentSuggestionList;
    }

    /**
     * 创建补货建议
     * 
     * @param wmsStock 库存信息
     * @return 补货建议
     */
    private ReplenishmentSuggestion createSuggestion(WmsStock wmsStock, MdProduct mdProduct, BigDecimal warningMinQty)
    {
        ReplenishmentSuggestion replenishmentSuggestion = new ReplenishmentSuggestion();
        replenishmentSuggestion.setWarehouseId(wmsStock.getWarehouseId());
        replenishmentSuggestion.setProductId(wmsStock.getProductId());
        replenishmentSuggestion.setCurrentQty(defaultAmount(wmsStock.getQuantity()));
        replenishmentSuggestion.setWarningMinQty(defaultAmount(warningMinQty));
        if (mdProduct != null)
        {
            replenishmentSuggestion.setProductCode(mdProduct.getProductCode());
            replenishmentSuggestion.setProductName(mdProduct.getProductName());
        }
        return replenishmentSuggestion;
    }

    /**
     * 计算近30天出库量
     * 
     * @param warehouseId 仓库编号
     * @param productId 商品编号
     * @return 出库量
     */
    private BigDecimal calculateRecentOutboundQty(Long warehouseId, Long productId)
    {
        WmsStockLog queryWmsStockLog = new WmsStockLog();
        queryWmsStockLog.setWarehouseId(warehouseId);
        queryWmsStockLog.setProductId(productId);
        queryWmsStockLog.setBillType("outbound");
        queryWmsStockLog.setInOut("2");
        List<WmsStockLog> wmsStockLogList = wmsStockLogMapper.selectWmsStockLogList(queryWmsStockLog);
        BigDecimal outboundQty = BigDecimal.ZERO;
        long thirtyDaysAgo = System.currentTimeMillis() - 30L * 24L * 60L * 60L * 1000L;
        for (WmsStockLog wmsStockLog : wmsStockLogList)
        {
            Date createTime = wmsStockLog.getCreateTime();
            if (createTime != null && createTime.getTime() >= thirtyDaysAgo)
            {
                outboundQty = outboundQty.add(defaultAmount(wmsStockLog.getQuantity()));
            }
        }
        return outboundQty;
    }

    /**
     * 计算在途采购量
     * 
     * @param warehouseId 仓库编号
     * @param productId 商品编号
     * @return 在途采购量
     */
    private BigDecimal calculatePendingPurchaseQty(Long warehouseId, Long productId)
    {
        List<PurOrder> purOrderList = purOrderMapper.selectPurOrderList(new PurOrder());
        BigDecimal pendingPurchaseQty = BigDecimal.ZERO;
        for (PurOrder purOrder : purOrderList)
        {
            if (purOrder.getWarehouseId() == null || !purOrder.getWarehouseId().equals(warehouseId))
            {
                continue;
            }
            if (!"approved".equals(purOrder.getBizStatus()) && !"executing".equals(purOrder.getBizStatus()))
            {
                continue;
            }
            List<PurOrderItem> purOrderItemList = purOrderItemMapper.selectPurOrderItemListByOrderId(purOrder.getPurchaseOrderId());
            for (PurOrderItem purOrderItem : purOrderItemList)
            {
                if (productId.equals(purOrderItem.getProductId()))
                {
                    pendingPurchaseQty = pendingPurchaseQty.add(defaultAmount(purOrderItem.getQuantity()));
                }
            }
        }
        return pendingPurchaseQty;
    }

    /**
     * 构建建议主键
     * 
     * @param warehouseId 仓库编号
     * @param productId 商品编号
     * @return 建议主键
     */
    private String buildSuggestionKey(Long warehouseId, Long productId)
    {
        return warehouseId + "_" + productId;
    }

    /**
     * 解析商品最小预警值
     * 
     * @param wmsStock 库存信息
     * @param mdProduct 商品信息
     * @return 最小预警值
     */
    private BigDecimal resolveWarningMinQty(WmsStock wmsStock, MdProduct mdProduct)
    {
        if (defaultAmount(wmsStock.getWarningMinQty()).compareTo(BigDecimal.ZERO) > 0)
        {
            return defaultAmount(wmsStock.getWarningMinQty());
        }
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
