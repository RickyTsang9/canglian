package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.PriceCustomerSpecial;

/**
 * 客户专属价格服务接口
 * 
 * @author canglian
 */
public interface IPriceCustomerSpecialService
{
    public PriceCustomerSpecial selectPriceCustomerSpecialById(Long specialPriceId);

    public List<PriceCustomerSpecial> selectPriceCustomerSpecialList(PriceCustomerSpecial priceCustomerSpecial);

    public int insertPriceCustomerSpecial(PriceCustomerSpecial priceCustomerSpecial);

    public int updatePriceCustomerSpecial(PriceCustomerSpecial priceCustomerSpecial);

    public int deletePriceCustomerSpecialById(Long specialPriceId);

    public int deletePriceCustomerSpecialByIds(Long[] specialPriceIds);
}
