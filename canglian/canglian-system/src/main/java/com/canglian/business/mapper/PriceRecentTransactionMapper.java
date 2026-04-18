package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.PriceRecentTransaction;

/**
 * 最近成交价格数据层
 * 
 * @author canglian
 */
public interface PriceRecentTransactionMapper
{
    public PriceRecentTransaction selectPriceRecentTransactionById(Long recentPriceId);

    public List<PriceRecentTransaction> selectPriceRecentTransactionList(PriceRecentTransaction priceRecentTransaction);

    public int insertPriceRecentTransaction(PriceRecentTransaction priceRecentTransaction);

    public int updatePriceRecentTransaction(PriceRecentTransaction priceRecentTransaction);

    public int deletePriceRecentTransactionById(Long recentPriceId);

    public int deletePriceRecentTransactionByIds(Long[] recentPriceIds);
}
