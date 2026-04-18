package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.PriceCustomerSpecial;
import com.canglian.business.mapper.PriceCustomerSpecialMapper;
import com.canglian.business.service.IPriceCustomerSpecialService;

/**
 * 客户专属价格服务实现
 * 
 * @author canglian
 */
@Service
public class PriceCustomerSpecialServiceImpl implements IPriceCustomerSpecialService
{
    @Autowired
    private PriceCustomerSpecialMapper priceCustomerSpecialMapper;

    /**
     * 查询客户专属价格
     * 
     * @param specialPriceId 专属价格id
     * @return 客户专属价格
     */
    @Override
    public PriceCustomerSpecial selectPriceCustomerSpecialById(Long specialPriceId)
    {
        return priceCustomerSpecialMapper.selectPriceCustomerSpecialById(specialPriceId);
    }

    /**
     * 查询客户专属价格列表
     * 
     * @param priceCustomerSpecial 客户专属价格
     * @return 客户专属价格集合
     */
    @Override
    public List<PriceCustomerSpecial> selectPriceCustomerSpecialList(PriceCustomerSpecial priceCustomerSpecial)
    {
        return priceCustomerSpecialMapper.selectPriceCustomerSpecialList(priceCustomerSpecial);
    }

    /**
     * 新增客户专属价格
     * 
     * @param priceCustomerSpecial 客户专属价格
     * @return 结果
     */
    @Override
    public int insertPriceCustomerSpecial(PriceCustomerSpecial priceCustomerSpecial)
    {
        return priceCustomerSpecialMapper.insertPriceCustomerSpecial(priceCustomerSpecial);
    }

    /**
     * 修改客户专属价格
     * 
     * @param priceCustomerSpecial 客户专属价格
     * @return 结果
     */
    @Override
    public int updatePriceCustomerSpecial(PriceCustomerSpecial priceCustomerSpecial)
    {
        return priceCustomerSpecialMapper.updatePriceCustomerSpecial(priceCustomerSpecial);
    }

    /**
     * 删除客户专属价格
     * 
     * @param specialPriceId 专属价格id
     * @return 结果
     */
    @Override
    public int deletePriceCustomerSpecialById(Long specialPriceId)
    {
        return priceCustomerSpecialMapper.deletePriceCustomerSpecialById(specialPriceId);
    }

    /**
     * 批量删除客户专属价格
     * 
     * @param specialPriceIds 专属价格id集合
     * @return 结果
     */
    @Override
    public int deletePriceCustomerSpecialByIds(Long[] specialPriceIds)
    {
        return priceCustomerSpecialMapper.deletePriceCustomerSpecialByIds(specialPriceIds);
    }
}
