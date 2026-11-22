package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.PriceCustomerLevel;
import com.canglian.business.mapper.PriceCustomerLevelMapper;
import com.canglian.business.service.IPriceCustomerLevelService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

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

    @Override
    public PriceCustomerLevel selectPriceCustomerLevelById(Long levelPriceId)
    {
        return priceCustomerLevelMapper.selectPriceCustomerLevelById(levelPriceId);
    }

    @Override
    public List<PriceCustomerLevel> selectPriceCustomerLevelList(PriceCustomerLevel priceCustomerLevel)
    {
        return priceCustomerLevelMapper.selectPriceCustomerLevelList(priceCustomerLevel);
    }

    @Override
    public int insertPriceCustomerLevel(PriceCustomerLevel priceCustomerLevel)
    {
        validateLevelPrice(priceCustomerLevel, true);
        if (StringUtils.isEmpty(priceCustomerLevel.getStatus()))
        {
            priceCustomerLevel.setStatus("0");
        }
        return priceCustomerLevelMapper.insertPriceCustomerLevel(priceCustomerLevel);
    }

    @Override
    public int updatePriceCustomerLevel(PriceCustomerLevel priceCustomerLevel)
    {
        validateLevelPrice(priceCustomerLevel, false);
        return priceCustomerLevelMapper.updatePriceCustomerLevel(priceCustomerLevel);
    }

    @Override
    public int deletePriceCustomerLevelById(Long levelPriceId)
    {
        return priceCustomerLevelMapper.deletePriceCustomerLevelById(levelPriceId);
    }

    @Override
    public int deletePriceCustomerLevelByIds(Long[] levelPriceIds)
    {
        return priceCustomerLevelMapper.deletePriceCustomerLevelByIds(levelPriceIds);
    }

    private void validateLevelPrice(PriceCustomerLevel item, boolean isNew)
    {
        if (item == null || StringUtils.isEmpty(item.getCustomerLevel()) || item.getProductId() == null)
        {
            throw new ServiceException("客户等级与商品不能为空");
        }
        item.setCustomerLevel(item.getCustomerLevel().trim());
        if (item.getSalePrice() == null || item.getSalePrice().compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("等级售价不能为空且不能为负数");
        }
        if (item.getEffectiveDate() != null && item.getExpireDate() != null
            && item.getEffectiveDate().after(item.getExpireDate()))
        {
            throw new ServiceException("生效日期不能晚于失效日期");
        }
        PriceCustomerLevel query = new PriceCustomerLevel();
        query.setCustomerLevel(item.getCustomerLevel());
        query.setProductId(item.getProductId());
        List<PriceCustomerLevel> exists = priceCustomerLevelMapper.selectPriceCustomerLevelList(query);
        if (exists != null)
        {
            for (PriceCustomerLevel ex : exists)
            {
                if (isNew || !ex.getLevelPriceId().equals(item.getLevelPriceId()))
                {
                    throw new ServiceException("该客户等级与商品的价格记录已存在，请直接修改原记录");
                }
            }
        }
    }
}
