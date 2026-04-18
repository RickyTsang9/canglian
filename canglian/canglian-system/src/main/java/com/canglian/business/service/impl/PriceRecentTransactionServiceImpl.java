package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.PriceRecentTransaction;
import com.canglian.business.mapper.PriceRecentTransactionMapper;
import com.canglian.business.service.IPriceRecentTransactionService;

/**
 * 最近成交价格服务实现
 * 
 * @author canglian
 */
@Service
public class PriceRecentTransactionServiceImpl implements IPriceRecentTransactionService
{
    @Autowired
    private PriceRecentTransactionMapper priceRecentTransactionMapper;

    /**
     * 查询最近成交价格
     * 
     * @param recentPriceId 最近成交价格id
     * @return 最近成交价格
     */
    @Override
    public PriceRecentTransaction selectPriceRecentTransactionById(Long recentPriceId)
    {
        return priceRecentTransactionMapper.selectPriceRecentTransactionById(recentPriceId);
    }

    /**
     * 查询最近成交价格列表
     * 
     * @param priceRecentTransaction 最近成交价格
     * @return 最近成交价格集合
     */
    @Override
    public List<PriceRecentTransaction> selectPriceRecentTransactionList(PriceRecentTransaction priceRecentTransaction)
    {
        return priceRecentTransactionMapper.selectPriceRecentTransactionList(priceRecentTransaction);
    }

    /**
     * 新增最近成交价格
     * 
     * @param priceRecentTransaction 最近成交价格
     * @return 结果
     */
    @Override
    public int insertPriceRecentTransaction(PriceRecentTransaction priceRecentTransaction)
    {
        return priceRecentTransactionMapper.insertPriceRecentTransaction(priceRecentTransaction);
    }

    /**
     * 修改最近成交价格
     * 
     * @param priceRecentTransaction 最近成交价格
     * @return 结果
     */
    @Override
    public int updatePriceRecentTransaction(PriceRecentTransaction priceRecentTransaction)
    {
        return priceRecentTransactionMapper.updatePriceRecentTransaction(priceRecentTransaction);
    }

    /**
     * 删除最近成交价格
     * 
     * @param recentPriceId 最近成交价格id
     * @return 结果
     */
    @Override
    public int deletePriceRecentTransactionById(Long recentPriceId)
    {
        return priceRecentTransactionMapper.deletePriceRecentTransactionById(recentPriceId);
    }

    /**
     * 批量删除最近成交价格
     * 
     * @param recentPriceIds 最近成交价格id集合
     * @return 结果
     */
    @Override
    public int deletePriceRecentTransactionByIds(Long[] recentPriceIds)
    {
        return priceRecentTransactionMapper.deletePriceRecentTransactionByIds(recentPriceIds);
    }
}
