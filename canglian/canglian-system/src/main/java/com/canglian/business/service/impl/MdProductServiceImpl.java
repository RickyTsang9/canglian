package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IMdProductService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

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
     * 导入商品档案
     *
     * @param productList 商品档案集合
     * @param updateSupport 是否更新已存在数据
     * @param operator 操作人
     * @return 导入结果
     */
    @Override
    public String importMdProduct(List<MdProduct> productList, Boolean updateSupport, String operator)
    {
        if (StringUtils.isNull(productList) || productList.isEmpty())
        {
            throw new ServiceException("导入商品数据不能为空");
        }
        int successNumber = 0;
        StringBuilder failureMessage = new StringBuilder();
        for (int productIndex = 0; productIndex < productList.size(); productIndex++)
        {
            MdProduct mdProduct = productList.get(productIndex);
            try
            {
                validateImportProduct(mdProduct, productIndex + 1);
                MdProduct existingMdProduct = mdProductMapper.selectMdProductByCode(mdProduct.getProductCode());
                if (existingMdProduct == null)
                {
                    mdProduct.setCreateBy(operator);
                    insertMdProduct(mdProduct);
                    successNumber++;
                }
                else if (Boolean.TRUE.equals(updateSupport))
                {
                    mdProduct.setProductId(existingMdProduct.getProductId());
                    mdProduct.setUpdateBy(operator);
                    updateMdProduct(mdProduct);
                    successNumber++;
                }
                else
                {
                    failureMessage.append("<br/>第").append(productIndex + 1).append("行商品编码已存在");
                }
            }
            catch (Exception exception)
            {
                failureMessage.append("<br/>第").append(productIndex + 1).append("行导入失败：").append(exception.getMessage());
            }
        }
        return buildImportMessage("商品", successNumber, failureMessage);
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

    /**
     * 校验导入商品
     *
     * @param mdProduct 商品档案
     * @param rowNumber 行号
     */
    private void validateImportProduct(MdProduct mdProduct, int rowNumber)
    {
        if (StringUtils.isEmpty(mdProduct.getProductCode()))
        {
            throw new ServiceException("商品编码不能为空");
        }
        if (StringUtils.isEmpty(mdProduct.getProductName()))
        {
            throw new ServiceException("商品名称不能为空");
        }
        if (StringUtils.isEmpty(mdProduct.getStatus()))
        {
            mdProduct.setStatus("0");
        }
        if (StringUtils.isEmpty(mdProduct.getDelFlag()))
        {
            mdProduct.setDelFlag("0");
        }
    }

    /**
     * 构建导入结果
     *
     * @param moduleName 模块名称
     * @param successNumber 成功数量
     * @param failureMessage 失败信息
     * @return 导入结果
     */
    private String buildImportMessage(String moduleName, int successNumber, StringBuilder failureMessage)
    {
        if (failureMessage.length() > 0)
        {
            return moduleName + "导入完成，成功 " + successNumber + " 条，失败信息：" + failureMessage;
        }
        return moduleName + "导入成功，共 " + successNumber + " 条";
    }
}

