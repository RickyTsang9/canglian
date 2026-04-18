package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.PriceCustomerLevel;
import com.canglian.business.mapper.PriceCustomerLevelMapper;
import com.canglian.business.service.IPriceCustomerLevelService;

/**
 * 客户等级价格服务实现
 * 
 * @author canglian
 */
@Service
public class PriceCustomerLevelServiceImpl implements IPriceCustomerLevelService
{
    @Autowired
    private PriceCustomerLevelMapper priceCustomerLevelMapper;

    /**
     * 查询客户等级价格
     * 
     * @param levelPriceId 等级价格id
     * @return 客户等级价格
     */
    @Override
    public PriceCustomerLevel selectPriceCustomerLevelById(Long levelPriceId)
    {
        return priceCustomerLevelMapper.selectPriceCustomerLevelById(levelPriceId);
    }

    /**
     * 查询客户等级价格列表
     * 
     * @param priceCustomerLevel 客户等级价格
     * @return 客户等级价格集合
     */
    @Override
    public List<PriceCustomerLevel> selectPriceCustomerLevelList(PriceCustomerLevel priceCustomerLevel)
    {
        return priceCustomerLevelMapper.selectPriceCustomerLevelList(priceCustomerLevel);
    }

    /**
     * 新增客户等级价格
     * 
     * @param priceCustomerLevel 客户等级价格
     * @return 结果
     */
    @Override
    public int insertPriceCustomerLevel(PriceCustomerLevel priceCustomerLevel)
    {
        return priceCustomerLevelMapper.insertPriceCustomerLevel(priceCustomerLevel);
    }

    /**
     * 修改客户等级价格
     * 
     * @param priceCustomerLevel 客户等级价格
     * @return 结果
     */
    @Override
    public int updatePriceCustomerLevel(PriceCustomerLevel priceCustomerLevel)
    {
        return priceCustomerLevelMapper.updatePriceCustomerLevel(priceCustomerLevel);
    }

    /**
     * 删除客户等级价格
     * 
     * @param levelPriceId 等级价格id
     * @return 结果
     */
    @Override
    public int deletePriceCustomerLevelById(Long levelPriceId)
    {
        return priceCustomerLevelMapper.deletePriceCustomerLevelById(levelPriceId);
    }

    /**
     * 批量删除客户等级价格
     * 
     * @param levelPriceIds 等级价格id集合
     * @return 结果
     */
    @Override
    public int deletePriceCustomerLevelByIds(Long[] levelPriceIds)
    {
        return priceCustomerLevelMapper.deletePriceCustomerLevelByIds(levelPriceIds);
    }
}
