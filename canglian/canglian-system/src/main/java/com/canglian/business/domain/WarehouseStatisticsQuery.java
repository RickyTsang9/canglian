package com.canglian.business.domain;

import java.util.Date;

/**
 * 仓库统计查询对象
 * 
 * @author canglian
 */
public class WarehouseStatisticsQuery
{
    /** 仓库id */
    private Long warehouseId;

    /** 统计视角 */
    private String statisticsView;

    /** 开始时间 */
    private Date startTime;

    /** 结束时间 */
    private Date endTime;

    /** 作业清单返回条数 */
    private Integer operationLimit;

    /** 产品排行返回条数 */
    private Integer rankLimit;

    /**
     * 获取仓库id
     * 
     * @return 仓库id
     */
    public Long getWarehouseId()
    {
        return warehouseId;
    }

    /**
     * 设置仓库id
     * 
     * @param warehouseId 仓库id
     */
    public void setWarehouseId(Long warehouseId)
    {
        this.warehouseId = warehouseId;
    }

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
     * 获取作业清单返回条数
     * 
     * @return 作业清单返回条数
     */
    public Integer getOperationLimit()
    {
        return operationLimit;
    }

    /**
     * 设置作业清单返回条数
     * 
     * @param operationLimit 作业清单返回条数
     */
    public void setOperationLimit(Integer operationLimit)
    {
        this.operationLimit = operationLimit;
    }

    /**
     * 获取产品排行返回条数
     * 
     * @return 产品排行返回条数
     */
    public Integer getRankLimit()
    {
        return rankLimit;
    }

    /**
     * 设置产品排行返回条数
     * 
     * @param rankLimit 产品排行返回条数
     */
    public void setRankLimit(Integer rankLimit)
    {
        this.rankLimit = rankLimit;
    }
}

