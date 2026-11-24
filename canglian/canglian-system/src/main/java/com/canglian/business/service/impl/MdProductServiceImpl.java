package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.MdProductUnit;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.MdProductUnitMapper;
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

    @Autowired(required = false)
    private com.canglian.business.service.IMdProductUnitService mdProductUnitService;

    @Autowired(required = false)
    private MdProductUnitMapper mdProductUnitMapper;

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
                    if (mdProduct.getWarningMinQty() == null)
                    {
                        mdProduct.setWarningMinQty(existingMdProduct.getWarningMinQty());
                    }
                    if (mdProduct.getWarningMaxQty() == null)
                    {
                        mdProduct.setWarningMaxQty(existingMdProduct.getWarningMaxQty());
                    }
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
        validateProductCodeUnique(mdProduct);
        fillDefaultWarningQty(mdProduct);
        if (StringUtils.isEmpty(mdProduct.getBaseUnit()) && StringUtils.isNotEmpty(mdProduct.getUnitName()))
        {
            mdProduct.setBaseUnit(mdProduct.getUnitName().trim());
        }
        else if (StringUtils.isEmpty(mdProduct.getUnitName()) && StringUtils.isNotEmpty(mdProduct.getBaseUnit()))
        {
            mdProduct.setUnitName(mdProduct.getBaseUnit().trim());
        }
        int rows = mdProductMapper.insertMdProduct(mdProduct);
        if (rows > 0 && mdProductUnitService != null)
        {
            mdProductUnitService.syncBaseUnitForProduct(mdProduct);
        }
        return rows;
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
        validateProductCodeUnique(mdProduct);
        MdProduct originalMdProduct = mdProductMapper.selectMdProductById(mdProduct.getProductId());
        fillDefaultWarningQty(mdProduct);
        if (StringUtils.isEmpty(mdProduct.getBaseUnit()) && StringUtils.isNotEmpty(mdProduct.getUnitName()))
        {
            mdProduct.setBaseUnit(mdProduct.getUnitName().trim());
        }
        else if (StringUtils.isEmpty(mdProduct.getUnitName()) && StringUtils.isNotEmpty(mdProduct.getBaseUnit()))
        {
            mdProduct.setUnitName(mdProduct.getBaseUnit().trim());
        }
        int updateRows = mdProductMapper.updateMdProduct(mdProduct);
        if (updateRows > 0 && originalMdProduct != null)
        {
            syncStockWarningQty(mdProduct, originalMdProduct);
        }
        if (updateRows > 0 && mdProductUnitService != null)
        {
            mdProductUnitService.syncBaseUnitForProduct(mdProduct);
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
        validateProductDeletable(productId);
        deleteProductUnits(productId);
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
        if (productIds != null)
        {
            for (Long productId : productIds)
            {
                validateProductDeletable(productId);
            }
            for (Long productId : productIds)
            {
                deleteProductUnits(productId);
            }
        }
        return mdProductMapper.deleteMdProductByIds(productIds);
    }

    private void deleteProductUnits(Long productId)
    {
        if (productId == null || mdProductUnitMapper == null) return;
        MdProductUnit q = new MdProductUnit();
        q.setProductId(productId);
        List<MdProductUnit> units = mdProductUnitMapper.selectMdProductUnitList(q);
        if (units != null)
        {
            for (MdProductUnit u : units)
            {
                if (u != null && u.getUnitId() != null)
                {
                    mdProductUnitMapper.deleteMdProductUnitById(u.getUnitId());
                }
            }
        }
    }

    private void validateProductCodeUnique(MdProduct mdProduct)
    {
        if (mdProduct == null || StringUtils.isEmpty(mdProduct.getProductCode()))
        {
            throw new ServiceException("商品编码不能为空");
        }
        mdProduct.setProductCode(mdProduct.getProductCode().trim());
        MdProduct existing = mdProductMapper.selectMdProductByCode(mdProduct.getProductCode());
        if (existing != null && !existing.getProductId().equals(mdProduct.getProductId()))
        {
            throw new ServiceException("商品编码[" + mdProduct.getProductCode() + "]已存在，请勿重复");
        }
        if (StringUtils.isNotEmpty(mdProduct.getBarCode()))
        {
            String trimmedBar = mdProduct.getBarCode().trim();
            mdProduct.setBarCode(trimmedBar);
            MdProduct barQ = new MdProduct();
            barQ.setBarCode(trimmedBar);
            List<MdProduct> clashProds = mdProductMapper.selectMdProductList(barQ);
            if (clashProds != null)
            {
                for (MdProduct p : clashProds)
                {
                    if (p != null && trimmedBar.equals(p.getBarCode()) && !p.getProductId().equals(mdProduct.getProductId()))
                    {
                        throw new ServiceException("商品条码[" + trimmedBar + "]已被商品[" + p.getProductName() + "]占用，请勿重复");
                    }
                }
            }
            if (mdProductUnitMapper != null)
            {
                MdProductUnit clashUnit = mdProductUnitMapper.selectByBarCode(trimmedBar);
                if (clashUnit != null && !clashUnit.getProductId().equals(mdProduct.getProductId()))
                {
                    throw new ServiceException("商品条码[" + trimmedBar + "]已被其他商品的包装单位条码占用，请勿重复");
                }
            }
        }
    }

    private void validateProductDeletable(Long productId)
    {
        if (productId == null)
        {
            return;
        }
        if (mdProductMapper.countProductReferences(productId) > 0)
        {
            throw new ServiceException("商品[" + productId + "]已存在关联库存、价格、序列号或业务单据记录，不允许删除，请改为停用");
        }
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

