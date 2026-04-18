package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.PriceCustomerSpecial;

/**
 * 客户专属价格数据层
 * 
 * @author canglian
 */
public interface PriceCustomerSpecialMapper
{
    public PriceCustomerSpecial selectPriceCustomerSpecialById(Long specialPriceId);

    public List<PriceCustomerSpecial> selectPriceCustomerSpecialList(PriceCustomerSpecial priceCustomerSpecial);

    public int insertPriceCustomerSpecial(PriceCustomerSpecial priceCustomerSpecial);

    public int updatePriceCustomerSpecial(PriceCustomerSpecial priceCustomerSpecial);

    public int deletePriceCustomerSpecialById(Long specialPriceId);

    public int deletePriceCustomerSpecialByIds(Long[] specialPriceIds);
}
