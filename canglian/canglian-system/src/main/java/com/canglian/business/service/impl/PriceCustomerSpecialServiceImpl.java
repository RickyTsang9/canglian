package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.PriceCustomerSpecial;
import com.canglian.business.mapper.PriceCustomerSpecialMapper;
import com.canglian.business.service.IPriceCustomerSpecialService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

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

    @Override
    public PriceCustomerSpecial selectPriceCustomerSpecialById(Long specialPriceId)
    {
        return priceCustomerSpecialMapper.selectPriceCustomerSpecialById(specialPriceId);
    }

    @Override
    public List<PriceCustomerSpecial> selectPriceCustomerSpecialList(PriceCustomerSpecial priceCustomerSpecial)
    {
        return priceCustomerSpecialMapper.selectPriceCustomerSpecialList(priceCustomerSpecial);
    }

    @Override
    public int insertPriceCustomerSpecial(PriceCustomerSpecial priceCustomerSpecial)
    {
        validateSpecialPrice(priceCustomerSpecial, true);
        if (StringUtils.isEmpty(priceCustomerSpecial.getStatus()))
        {
            priceCustomerSpecial.setStatus("0");
        }
        return priceCustomerSpecialMapper.insertPriceCustomerSpecial(priceCustomerSpecial);
    }

    @Override
    public int updatePriceCustomerSpecial(PriceCustomerSpecial priceCustomerSpecial)
    {
        validateSpecialPrice(priceCustomerSpecial, false);
        return priceCustomerSpecialMapper.updatePriceCustomerSpecial(priceCustomerSpecial);
    }

    @Override
    public int deletePriceCustomerSpecialById(Long specialPriceId)
    {
        return priceCustomerSpecialMapper.deletePriceCustomerSpecialById(specialPriceId);
    }

    @Override
    public int deletePriceCustomerSpecialByIds(Long[] specialPriceIds)
    {
        return priceCustomerSpecialMapper.deletePriceCustomerSpecialByIds(specialPriceIds);
    }

    private void validateSpecialPrice(PriceCustomerSpecial item, boolean isNew)
    {
        if (item == null || item.getCustomerId() == null || item.getProductId() == null)
        {
            throw new ServiceException("客户与商品不能为空");
        }
        if (item.getSalePrice() == null || item.getSalePrice().compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("专属售价不能为空且不能为负数");
        }
        if (item.getEffectiveDate() != null && item.getExpireDate() != null
            && item.getEffectiveDate().after(item.getExpireDate()))
        {
            throw new ServiceException("生效日期不能晚于失效日期");
        }
        PriceCustomerSpecial query = new PriceCustomerSpecial();
        query.setCustomerId(item.getCustomerId());
        query.setProductId(item.getProductId());
        List<PriceCustomerSpecial> exists = priceCustomerSpecialMapper.selectPriceCustomerSpecialList(query);
        if (exists != null)
        {
            for (PriceCustomerSpecial ex : exists)
            {
                if (isNew || !ex.getSpecialPriceId().equals(item.getSpecialPriceId()))
                {
                    throw new ServiceException("该客户与商品的专属价格记录已存在，请直接修改原记录");
                }
            }
        }
    }
}
