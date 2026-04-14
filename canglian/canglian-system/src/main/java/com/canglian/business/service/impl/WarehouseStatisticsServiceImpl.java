package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.WarehouseStatisticsCardData;
import com.canglian.business.domain.WarehouseStatisticsOperationItem;
import com.canglian.business.domain.WarehouseStatisticsOverview;
import com.canglian.business.domain.WarehouseStatisticsProductRankItem;
import com.canglian.business.domain.WarehouseStatisticsQuery;
import com.canglian.business.domain.WarehouseStatisticsStockData;
import com.canglian.business.mapper.WarehouseStatisticsMapper;
import com.canglian.business.service.IWarehouseStatisticsService;
import com.canglian.common.utils.DateUtils;
import com.canglian.common.utils.StringUtils;

/**
 * 仓库统计 服务层实现
 * 
 * @author canglian
 */
@Service
public class WarehouseStatisticsServiceImpl implements IWarehouseStatisticsService
{
    private static final String STATISTICS_VIEW_ALL = "all";

    private static final String STATISTICS_VIEW_INBOUND = "inbound";

    private static final String STATISTICS_VIEW_OUTBOUND = "outbound";

    private static final String STATISTICS_VIEW_INVENTORY = "inventory";

    private static final int DEFAULT_OPERATION_LIMIT = 10;

    private static final int DEFAULT_RANK_LIMIT = 10;

    @Autowired
    private WarehouseStatisticsMapper warehouseStatisticsMapper;

    /**
     * 查询仓库统计总览
     * 
     * @param warehouseStatisticsQuery 仓库统计查询参数
     * @return 仓库统计总览
     */
    @Override
    public WarehouseStatisticsOverview selectWarehouseStatisticsOverview(WarehouseStatisticsQuery warehouseStatisticsQuery)
    {
        WarehouseStatisticsQuery normalizedQuery = normalizeQuery(warehouseStatisticsQuery);
        WarehouseStatisticsCardData inboundCard = buildInboundCard(normalizedQuery);
        WarehouseStatisticsCardData outboundCard = buildOutboundCard(normalizedQuery);

        WarehouseStatisticsOverview warehouseStatisticsOverview = new WarehouseStatisticsOverview();
        warehouseStatisticsOverview.setStatisticsView(normalizedQuery.getStatisticsView());
        warehouseStatisticsOverview.setStartTime(normalizedQuery.getStartTime());
        warehouseStatisticsOverview.setEndTime(normalizedQuery.getEndTime());
        warehouseStatisticsOverview.setInboundCard(inboundCard);
        warehouseStatisticsOverview.setOutboundCard(outboundCard);
        warehouseStatisticsOverview.setInboundOperationList(buildInboundOperationList(normalizedQuery));
        warehouseStatisticsOverview.setOutboundOperationList(buildOutboundOperationList(normalizedQuery));
        warehouseStatisticsOverview.setStockData(buildStockData(normalizedQuery));
        warehouseStatisticsOverview.setProductRankList(buildProductRankList(normalizedQuery));
        return warehouseStatisticsOverview;
    }

    /**
     * 构建入库卡片
     * 
     * @param warehouseStatisticsQuery 仓库统计查询参数
     * @return 入库卡片
     */
    private WarehouseStatisticsCardData buildInboundCard(WarehouseStatisticsQuery warehouseStatisticsQuery)
    {
        WarehouseStatisticsCardData inboundCard = emptyCardData();
        if (isInboundIncluded(warehouseStatisticsQuery.getStatisticsView()))
        {
            mergeCardData(inboundCard, warehouseStatisticsMapper.selectInboundCardData(warehouseStatisticsQuery));
        }
        if (isInventoryIncluded(warehouseStatisticsQuery.getStatisticsView()))
        {
            mergeCardData(inboundCard, warehouseStatisticsMapper.selectInventoryInboundCardData(warehouseStatisticsQuery));
        }
        return inboundCard;
    }

    /**
     * 构建出库卡片
     * 
     * @param warehouseStatisticsQuery 仓库统计查询参数
     * @return 出库卡片
     */
    private WarehouseStatisticsCardData buildOutboundCard(WarehouseStatisticsQuery warehouseStatisticsQuery)
    {
        WarehouseStatisticsCardData outboundCard = emptyCardData();
        if (isOutboundIncluded(warehouseStatisticsQuery.getStatisticsView()))
        {
            mergeCardData(outboundCard, warehouseStatisticsMapper.selectOutboundCardData(warehouseStatisticsQuery));
        }
        if (isInventoryIncluded(warehouseStatisticsQuery.getStatisticsView()))
        {
            mergeCardData(outboundCard, warehouseStatisticsMapper.selectInventoryOutboundCardData(warehouseStatisticsQuery));
        }
        return outboundCard;
    }

    /**
     * 构建入库作业清单
     * 
     * @param warehouseStatisticsQuery 仓库统计查询参数
     * @return 入库作业清单
     */
    private List<WarehouseStatisticsOperationItem> buildInboundOperationList(WarehouseStatisticsQuery warehouseStatisticsQuery)
    {
        List<WarehouseStatisticsOperationItem> inboundOperationList = new ArrayList<>();
        if (isInboundIncluded(warehouseStatisticsQuery.getStatisticsView()))
        {
            inboundOperationList.addAll(warehouseStatisticsMapper.selectInboundOperationList(warehouseStatisticsQuery));
        }
        if (isInventoryIncluded(warehouseStatisticsQuery.getStatisticsView()))
        {
            inboundOperationList.addAll(warehouseStatisticsMapper.selectInventoryInboundOperationList(warehouseStatisticsQuery));
        }
        return sortAndLimitOperationList(inboundOperationList, warehouseStatisticsQuery.getOperationLimit());
    }

    /**
     * 构建出库作业清单
     * 
     * @param warehouseStatisticsQuery 仓库统计查询参数
     * @return 出库作业清单
     */
    private List<WarehouseStatisticsOperationItem> buildOutboundOperationList(WarehouseStatisticsQuery warehouseStatisticsQuery)
    {
        List<WarehouseStatisticsOperationItem> outboundOperationList = new ArrayList<>();
        if (isOutboundIncluded(warehouseStatisticsQuery.getStatisticsView()))
        {
            outboundOperationList.addAll(warehouseStatisticsMapper.selectOutboundOperationList(warehouseStatisticsQuery));
        }
        if (isInventoryIncluded(warehouseStatisticsQuery.getStatisticsView()))
        {
            outboundOperationList.addAll(warehouseStatisticsMapper.selectInventoryOutboundOperationList(warehouseStatisticsQuery));
        }
        return sortAndLimitOperationList(outboundOperationList, warehouseStatisticsQuery.getOperationLimit());
    }

    /**
     * 构建库存统计
     * 
     * @param warehouseStatisticsQuery 仓库统计查询参数
     * @return 库存统计
     */
    private WarehouseStatisticsStockData buildStockData(WarehouseStatisticsQuery warehouseStatisticsQuery)
    {
        WarehouseStatisticsStockData warehouseStatisticsStockData = warehouseStatisticsMapper
                .selectWarehouseStockData(warehouseStatisticsQuery);
        if (warehouseStatisticsStockData == null)
        {
            warehouseStatisticsStockData = new WarehouseStatisticsStockData();
        }
        warehouseStatisticsStockData.setTotalQuantity(defaultDecimal(warehouseStatisticsStockData.getTotalQuantity()));
        warehouseStatisticsStockData.setTotalAmount(defaultDecimal(warehouseStatisticsStockData.getTotalAmount()));
        return warehouseStatisticsStockData;
    }

    /**
     * 构建产品排行
     * 
     * @param warehouseStatisticsQuery 仓库统计查询参数
     * @return 产品排行
     */
    private List<WarehouseStatisticsProductRankItem> buildProductRankList(WarehouseStatisticsQuery warehouseStatisticsQuery)
    {
        List<WarehouseStatisticsProductRankItem> productRankList = warehouseStatisticsMapper
                .selectProductRankList(warehouseStatisticsQuery);
        if (productRankList == null)
        {
            return new ArrayList<>();
        }
        return productRankList;
    }

    /**
     * 标准化查询参数
     * 
     * @param warehouseStatisticsQuery 仓库统计查询参数
     * @return 标准化后的查询参数
     */
    private WarehouseStatisticsQuery normalizeQuery(WarehouseStatisticsQuery warehouseStatisticsQuery)
    {
        WarehouseStatisticsQuery normalizedQuery = warehouseStatisticsQuery;
        if (normalizedQuery == null)
        {
            normalizedQuery = new WarehouseStatisticsQuery();
        }
        normalizedQuery.setStatisticsView(normalizeStatisticsView(normalizedQuery.getStatisticsView()));
        normalizedQuery.setOperationLimit(normalizePositiveInteger(normalizedQuery.getOperationLimit(), DEFAULT_OPERATION_LIMIT));
        normalizedQuery.setRankLimit(normalizePositiveInteger(normalizedQuery.getRankLimit(), DEFAULT_RANK_LIMIT));
        normalizeDateRange(normalizedQuery);
        return normalizedQuery;
    }

    /**
     * 标准化统计视角
     * 
     * @param statisticsView 统计视角
     * @return 标准化后的统计视角
     */
    private String normalizeStatisticsView(String statisticsView)
    {
        if (StringUtils.isEmpty(statisticsView))
        {
            return STATISTICS_VIEW_ALL;
        }
        String lowerCaseView = statisticsView.toLowerCase();
        if (STATISTICS_VIEW_INBOUND.equals(lowerCaseView)
                || STATISTICS_VIEW_OUTBOUND.equals(lowerCaseView)
                || STATISTICS_VIEW_INVENTORY.equals(lowerCaseView))
        {
            return lowerCaseView;
        }
        return STATISTICS_VIEW_ALL;
    }

    /**
     * 标准化日期范围
     * 
     * @param warehouseStatisticsQuery 仓库统计查询参数
     */
    private void normalizeDateRange(WarehouseStatisticsQuery warehouseStatisticsQuery)
    {
        Date startTime = warehouseStatisticsQuery.getStartTime();
        Date endTime = warehouseStatisticsQuery.getEndTime();

        if (startTime == null && endTime == null)
        {
            LocalDateTime currentDateTime = LocalDateTime.now();
            startTime = DateUtils.toDate(currentDateTime.toLocalDate());
            endTime = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
        }
        else if (startTime != null && endTime == null)
        {
            LocalDateTime startDateTime = LocalDateTime.ofInstant(startTime.toInstant(), ZoneId.systemDefault());
            endTime = DateUtils.toDate(startDateTime.toLocalDate().atTime(LocalTime.MAX));
        }
        else if (startTime == null)
        {
            LocalDateTime endDateTime = LocalDateTime.ofInstant(endTime.toInstant(), ZoneId.systemDefault());
            startTime = DateUtils.toDate(endDateTime.toLocalDate());
        }

        if (startTime.after(endTime))
        {
            Date exchangeDate = startTime;
            startTime = endTime;
            endTime = exchangeDate;
        }

        warehouseStatisticsQuery.setStartTime(startTime);
        warehouseStatisticsQuery.setEndTime(endTime);
    }

    /**
     * 判断是否包含入库统计
     * 
     * @param statisticsView 统计视角
     * @return 是否包含入库统计
     */
    private boolean isInboundIncluded(String statisticsView)
    {
        return STATISTICS_VIEW_ALL.equals(statisticsView) || STATISTICS_VIEW_INBOUND.equals(statisticsView);
    }

    /**
     * 判断是否包含出库统计
     * 
     * @param statisticsView 统计视角
     * @return 是否包含出库统计
     */
    private boolean isOutboundIncluded(String statisticsView)
    {
        return STATISTICS_VIEW_ALL.equals(statisticsView) || STATISTICS_VIEW_OUTBOUND.equals(statisticsView);
    }

    /**
     * 判断是否包含库存调整统计
     * 
     * @param statisticsView 统计视角
     * @return 是否包含库存调整统计
     */
    private boolean isInventoryIncluded(String statisticsView)
    {
        return STATISTICS_VIEW_ALL.equals(statisticsView) || STATISTICS_VIEW_INVENTORY.equals(statisticsView);
    }

    /**
     * 创建空卡片数据
     * 
     * @return 空卡片数据
     */
    private WarehouseStatisticsCardData emptyCardData()
    {
        WarehouseStatisticsCardData warehouseStatisticsCardData = new WarehouseStatisticsCardData();
        warehouseStatisticsCardData.setBillCount(0L);
        warehouseStatisticsCardData.setTotalQuantity(BigDecimal.ZERO);
        warehouseStatisticsCardData.setTotalAmount(BigDecimal.ZERO);
        return warehouseStatisticsCardData;
    }

    /**
     * 合并卡片数据
     * 
     * @param targetCardData 目标卡片数据
     * @param sourceCardData 来源卡片数据
     */
    private void mergeCardData(WarehouseStatisticsCardData targetCardData, WarehouseStatisticsCardData sourceCardData)
    {
        if (sourceCardData == null)
        {
            return;
        }
        targetCardData.setBillCount(defaultLong(targetCardData.getBillCount()) + defaultLong(sourceCardData.getBillCount()));
        targetCardData.setTotalQuantity(defaultDecimal(targetCardData.getTotalQuantity()).add(defaultDecimal(sourceCardData.getTotalQuantity())));
        targetCardData.setTotalAmount(defaultDecimal(targetCardData.getTotalAmount()).add(defaultDecimal(sourceCardData.getTotalAmount())));
    }

    /**
     * 作业清单排序并截断
     * 
     * @param operationItemList 作业清单
     * @param operationLimit 返回条数
     * @return 排序并截断后的作业清单
     */
    private List<WarehouseStatisticsOperationItem> sortAndLimitOperationList(
            List<WarehouseStatisticsOperationItem> operationItemList, Integer operationLimit)
    {
        operationItemList.sort(Comparator.comparing(WarehouseStatisticsOperationItem::getBusinessTime,
                Comparator.nullsLast(Date::compareTo)).reversed());
        int endIndex = Math.min(operationItemList.size(), operationLimit);
        return new ArrayList<>(operationItemList.subList(0, endIndex));
    }

    /**
     * 默认数值
     * 
     * @param value 数值
     * @return 默认后的数值
     */
    private BigDecimal defaultDecimal(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO : value;
    }

    /**
     * 默认长整型
     * 
     * @param value 长整型
     * @return 默认后的长整型
     */
    private Long defaultLong(Long value)
    {
        return value == null ? 0L : value;
    }

    /**
     * 标准化正整数
     * 
     * @param value 参数值
     * @param defaultValue 默认值
     * @return 标准化后的值
     */
    private Integer normalizePositiveInteger(Integer value, Integer defaultValue)
    {
        if (value == null || value <= 0)
        {
            return defaultValue;
        }
        return value;
    }
}

