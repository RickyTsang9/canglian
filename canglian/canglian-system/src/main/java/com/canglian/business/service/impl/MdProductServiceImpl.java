package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.WmsStockMapper;
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

    @Autowired
    private WmsStockMapper wmsStockMapper;

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
        fillDefaultWarningQty(mdProduct);
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
        MdProduct originalMdProduct = mdProductMapper.selectMdProductById(mdProduct.getProductId());
        fillDefaultWarningQty(mdProduct);
        int updateRows = mdProductMapper.updateMdProduct(mdProduct);
        if (updateRows > 0 && originalMdProduct != null)
        {
            syncStockWarningQty(mdProduct, originalMdProduct);
        }
        return updateRows;
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

    /**
     * 填充默认预警阈值
     * 
     * @param mdProduct 商品档案
     */
    private void fillDefaultWarningQty(MdProduct mdProduct)
    {
        if (mdProduct.getWarningMinQty() == null)
        {
            mdProduct.setWarningMinQty(BigDecimal.ZERO);
        }
        if (mdProduct.getWarningMaxQty() == null)
        {
            mdProduct.setWarningMaxQty(BigDecimal.ZERO);
        }
    }

    /**
     * 同步商品预警阈值到库存
     * 
     * @param mdProduct 最新商品档案
     * @param originalMdProduct 原商品档案
     */
    private void syncStockWarningQty(MdProduct mdProduct, MdProduct originalMdProduct)
    {
        wmsStockMapper.updateWarningQtyByProductId(mdProduct.getProductId(),
            defaultWarningQty(originalMdProduct.getWarningMinQty()),
            defaultWarningQty(originalMdProduct.getWarningMaxQty()),
            defaultWarningQty(mdProduct.getWarningMinQty()),
            defaultWarningQty(mdProduct.getWarningMaxQty()));
    }

    /**
     * 空预警阈值转默认值
     * 
     * @param warningQty 预警阈值
     * @return 默认值
     */
    private BigDecimal defaultWarningQty(BigDecimal warningQty)
    {
        return warningQty == null ? BigDecimal.ZERO : warningQty;
    }
}

