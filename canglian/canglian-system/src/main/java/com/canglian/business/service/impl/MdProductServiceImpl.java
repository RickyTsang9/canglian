package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.service.IMdProductService;

/**
 * 商品档案 服务层实现
 * 
 * @author canglian
 */
@Service
public class MdProductServiceImpl implements IMdProductService
{
    @Autowired
    private MdProductMapper mdProductMapper;

    /**
     * 查询商品档案信息
     * 
     * @param productId 商品id
     * @return 商品档案信息
     */
    @Override
    public MdProduct selectMdProductById(Long productId)
    {
        return mdProductMapper.selectMdProductById(productId);
    }

    /**
     * 查询商品档案列表
     * 
     * @param mdProduct 商品档案
     * @return 商品档案集合
     */
    @Override
    public List<MdProduct> selectMdProductList(MdProduct mdProduct)
    {
        return mdProductMapper.selectMdProductList(mdProduct);
    }

    /**
     * 新增商品档案
     * 
     * @param mdProduct 商品档案
     * @return 结果
     */
    @Override
    public int insertMdProduct(MdProduct mdProduct)
    {
        return mdProductMapper.insertMdProduct(mdProduct);
    }

    /**
     * 修改商品档案
     * 
     * @param mdProduct 商品档案
     * @return 结果
     */
    @Override
    public int updateMdProduct(MdProduct mdProduct)
    {
        return mdProductMapper.updateMdProduct(mdProduct);
    }

    /**
     * 删除商品档案
     * 
     * @param productId 商品id
     * @return 结果
     */
    @Override
    public int deleteMdProductById(Long productId)
    {
        return mdProductMapper.deleteMdProductById(productId);
    }

    /**
     * 批量删除商品档案
     * 
     * @param productIds 需要删除的商品id
     * @return 结果
     */
    @Override
    public int deleteMdProductByIds(Long[] productIds)
    {
        return mdProductMapper.deleteMdProductByIds(productIds);
    }
}

