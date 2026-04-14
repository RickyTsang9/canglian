package com.canglian.business.domain;

import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 仓库统计总览对象
 * 
 * @author canglian
 */
public class WarehouseStatisticsOverview
{
    /** 统计视角 */
    private String statisticsView;

    /** 开始时间 */
    private Date startTime;

    /** 结束时间 */
    private Date endTime;

    /** 入库统计卡片 */
    private WarehouseStatisticsCardData inboundCard;

    /** 出库统计卡片 */
    private WarehouseStatisticsCardData outboundCard;

    /** 库存统计 */
    private WarehouseStatisticsStockData stockData;

    /** 入库作业清单 */
    private List<WarehouseStatisticsOperationItem> inboundOperationList;

    /** 出库作业清单 */
    private List<WarehouseStatisticsOperationItem> outboundOperationList;

    /** 产品可用库存排行 */
    private List<WarehouseStatisticsProductRankItem> productRankList;

    /**
     * 获取统计视角
     * 
     * @return 统计视角
     */
    public String getStatisticsView()
    {
        return statisticsView;
    }

    /**
     * 设置统计视角
     * 
     * @param statisticsView 统计视角
     */
    public void setStatisticsView(String statisticsView)
    {
        this.statisticsView = statisticsView;
    }

    /**
     * 获取开始时间
     * 
     * @return 开始时间
     */
    public Date getStartTime()
    {
        return startTime;
    }

    /**
     * 设置开始时间
     * 
     * @param startTime 开始时间
     */
    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    /**
     * 获取结束时间
     * 
     * @return 结束时间
     */
    public Date getEndTime()
    {
        return endTime;
    }

    /**
     * 设置结束时间
     * 
     * @param endTime 结束时间
     */
    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    /**
     * 获取入库统计卡片
     * 
     * @return 入库统计卡片
     */
    public WarehouseStatisticsCardData getInboundCard()
    {
        return inboundCard;
    }

    /**
     * 设置入库统计卡片
     * 
     * @param inboundCard 入库统计卡片
     */
    public void setInboundCard(WarehouseStatisticsCardData inboundCard)
    {
        this.inboundCard = inboundCard;
    }

    /**
     * 获取出库统计卡片
     * 
     * @return 出库统计卡片
     */
    public WarehouseStatisticsCardData getOutboundCard()
    {
        return outboundCard;
    }

    /**
     * 设置出库统计卡片
     * 
     * @param outboundCard 出库统计卡片
     */
    public void setOutboundCard(WarehouseStatisticsCardData outboundCard)
    {
        this.outboundCard = outboundCard;
    }

    /**
     * 获取库存统计
     * 
     * @return 库存统计
     */
    public WarehouseStatisticsStockData getStockData()
    {
        return stockData;
    }

    /**
     * 设置库存统计
     * 
     * @param stockData 库存统计
     */
    public void setStockData(WarehouseStatisticsStockData stockData)
    {
        this.stockData = stockData;
    }

    /**
     * 获取入库作业清单
     * 
     * @return 入库作业清单
     */
    public List<WarehouseStatisticsOperationItem> getInboundOperationList()
    {
        return inboundOperationList;
    }

    /**
     * 设置入库作业清单
     * 
     * @param inboundOperationList 入库作业清单
     */
    public void setInboundOperationList(List<WarehouseStatisticsOperationItem> inboundOperationList)
    {
        this.inboundOperationList = inboundOperationList;
    }

    /**
     * 获取出库作业清单
     * 
     * @return 出库作业清单
     */
    public List<WarehouseStatisticsOperationItem> getOutboundOperationList()
    {
        return outboundOperationList;
    }

    /**
     * 设置出库作业清单
     * 
     * @param outboundOperationList 出库作业清单
     */
    public void setOutboundOperationList(List<WarehouseStatisticsOperationItem> outboundOperationList)
    {
        this.outboundOperationList = outboundOperationList;
    }

    /**
     * 获取产品可用库存排行
     * 
     * @return 产品可用库存排行
     */
    public List<WarehouseStatisticsProductRankItem> getProductRankList()
    {
        return productRankList;
    }

    /**
     * 设置产品可用库存排行
     * 
     * @param productRankList 产品可用库存排行
     */
    public void setProductRankList(List<WarehouseStatisticsProductRankItem> productRankList)
    {
        this.productRankList = productRankList;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("statisticsView", getStatisticsView())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("inboundCard", getInboundCard())
            .append("outboundCard", getOutboundCard())
            .append("stockData", getStockData())
            .append("inboundOperationList", getInboundOperationList())
            .append("outboundOperationList", getOutboundOperationList())
            .append("productRankList", getProductRankList())
            .toString();
    }
}

