package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.MdProduct;

/**
 * 商品档案 服务层
 *
 * @author canglian
 */
public interface IMdProductService
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
     * 导入商品档案
     *
     * @param productList 商品档案集合
     * @param updateSupport 是否更新已存在数据
     * @param operator 操作人
     * @return 导入结果
     */
    public String importMdProduct(List<MdProduct> productList, Boolean updateSupport, String operator);

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

