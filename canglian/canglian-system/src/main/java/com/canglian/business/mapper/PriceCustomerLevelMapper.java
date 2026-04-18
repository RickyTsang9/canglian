package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.PriceCustomerLevel;

/**
 * 客户等级价格数据层
 * 
 * @author canglian
 */
public interface PriceCustomerLevelMapper
{
    public PriceCustomerLevel selectPriceCustomerLevelById(Long levelPriceId);

    public List<PriceCustomerLevel> selectPriceCustomerLevelList(PriceCustomerLevel priceCustomerLevel);

    public int insertPriceCustomerLevel(PriceCustomerLevel priceCustomerLevel);

    public int updatePriceCustomerLevel(PriceCustomerLevel priceCustomerLevel);

    public int deletePriceCustomerLevelById(Long levelPriceId);

    public int deletePriceCustomerLevelByIds(Long[] levelPriceIds);
}
