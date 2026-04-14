package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.MdProduct;

/**
 * 商品档案 数据层
 * 
 * @author canglian
 */
public interface MdProductMapper
{
    /**
     * 查询商品档案信息
     * 
     * @param productId 商品id
     * @return 商品档案信息
     */
    public MdProduct selectMdProductById(Long productId);

    /**
     * 查询商品档案列表
     * 
     * @param mdProduct 商品档案
     * @return 商品档案集合
     */
    public List<MdProduct> selectMdProductList(MdProduct mdProduct);

    /**
     * 新增商品档案
     * 
     * @param mdProduct 商品档案
     * @return 结果
     */
    public int insertMdProduct(MdProduct mdProduct);

    /**
     * 修改商品档案
     * 
     * @param mdProduct 商品档案
     * @return 结果
     */
    public int updateMdProduct(MdProduct mdProduct);

    /**
     * 删除商品档案
     * 
     * @param productId 商品id
     * @return 结果
     */
    public int deleteMdProductById(Long productId);

    /**
     * 批量删除商品档案
     * 
     * @param productIds 需要删除的商品id
     * @return 结果
     */
    public int deleteMdProductByIds(Long[] productIds);
}

