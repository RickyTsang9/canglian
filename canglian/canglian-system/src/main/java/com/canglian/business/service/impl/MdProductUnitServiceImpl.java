package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.MdProductUnit;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.MdProductUnitMapper;
import com.canglian.business.service.IMdProductUnitService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 商品多单位与换算服务实现
 *
 * @author canglian
 */
@Service
public class MdProductUnitServiceImpl implements IMdProductUnitService
{
    @Autowired
    private MdProductUnitMapper mdProductUnitMapper;

    @Autowired
    private MdProductMapper mdProductMapper;

    @Override
    public MdProductUnit selectMdProductUnitById(Long unitId)
    {
        return mdProductUnitMapper.selectMdProductUnitById(unitId);
    }

    @Override
    public List<MdProductUnit> selectMdProductUnitList(MdProductUnit query)
    {
        return mdProductUnitMapper.selectMdProductUnitList(query);
    }

    @Override
    public MdProductUnit ensureBaseUnitPersisted(Long productId)
    {
        if (productId == null)
        {
            return null;
        }
        MdProductUnit existingBase = mdProductUnitMapper.selectBaseUnitByProductId(productId);
        if (existingBase != null)
        {
            return existingBase;
        }
        MdProduct product = mdProductMapper.selectMdProductById(productId);
        if (product == null)
        {
            return null;
        }
        syncBaseUnitForProduct(product);
        return mdProductUnitMapper.selectBaseUnitByProductId(productId);
    }

    @Override
    public void syncBaseUnitForProduct(MdProduct product)
    {
        if (product == null || product.getProductId() == null)
        {
            return;
        }
        String baseName = StringUtils.isNotEmpty(product.getBaseUnit()) ? product.getBaseUnit().trim()
            : (StringUtils.isNotEmpty(product.getUnitName()) ? product.getUnitName().trim() : "件");
        MdProductUnit existingBase = mdProductUnitMapper.selectBaseUnitByProductId(product.getProductId());
        if (existingBase == null)
        {
            MdProductUnit sameName = mdProductUnitMapper.selectByProductAndUnitAnyStatus(product.getProductId(), baseName);
            if (sameName != null)
            {
                sameName.setIsBaseUnit("1");
                sameName.setConvertRatio(BigDecimal.ONE);
                sameName.setStatus("0");
                if (sameName.getPurchasePrice() == null)
                {
                    sameName.setPurchasePrice(product.getCostPrice());
                }
                if (sameName.getSalePrice() == null)
                {
                    sameName.setSalePrice(product.getSalePrice());
                }
                mdProductUnitMapper.updateMdProductUnit(sameName);
                return;
            }
            MdProductUnit baseUnit = new MdProductUnit();
            baseUnit.setProductId(product.getProductId());
            baseUnit.setUnitName(baseName);
            baseUnit.setIsBaseUnit("1");
            baseUnit.setConvertRatio(BigDecimal.ONE);
            baseUnit.setPurchasePrice(product.getCostPrice());
            baseUnit.setSalePrice(product.getSalePrice());
            baseUnit.setStatus("0");
            baseUnit.setCreateBy(StringUtils.isNotEmpty(product.getUpdateBy()) ? product.getUpdateBy() : product.getCreateBy());
            mdProductUnitMapper.insertMdProductUnit(baseUnit);
        }
        else
        {
            boolean changed = false;
            if (!baseName.equals(existingBase.getUnitName()))
            {
                MdProductUnit clash = mdProductUnitMapper.selectByProductAndUnitAnyStatus(product.getProductId(), baseName);
                if (clash == null || clash.getUnitId().equals(existingBase.getUnitId()))
                {
                    existingBase.setUnitName(baseName);
                    changed = true;
                }
            }
            if (product.getCostPrice() != null)
            {
                existingBase.setPurchasePrice(product.getCostPrice());
                changed = true;
            }
            if (product.getSalePrice() != null)
            {
                existingBase.setSalePrice(product.getSalePrice());
                changed = true;
            }
            if (changed)
            {
                existingBase.setConvertRatio(BigDecimal.ONE);
                existingBase.setUpdateBy(product.getUpdateBy());
                mdProductUnitMapper.updateMdProductUnit(existingBase);
            }
        }
    }

    @Override
    public List<MdProductUnit> selectUnitsByProductId(Long productId)
    {
        if (productId == null)
        {
            return new ArrayList<MdProductUnit>();
        }
        // 确保真实持久化的基准单位一定存在，防止新增辅助单位后基准单位消失
        ensureBaseUnitPersisted(productId);
        MdProductUnit query = new MdProductUnit();
        query.setProductId(productId);
        query.setStatus("0");
        List<MdProductUnit> units = mdProductUnitMapper.selectMdProductUnitList(query);
        return units == null ? new ArrayList<MdProductUnit>() : units;
    }

    @Override
    public BigDecimal resolveConvertRatio(Long productId, String unitName)
    {
        if (productId == null || StringUtils.isEmpty(unitName))
        {
            return BigDecimal.ONE;
        }
        String trimmed = unitName.trim();
        MdProductUnit unit = mdProductUnitMapper.selectByProductAndUnit(productId, trimmed);
        if (unit != null && unit.getConvertRatio() != null && unit.getConvertRatio().compareTo(BigDecimal.ZERO) > 0)
        {
            return unit.getConvertRatio();
        }
        MdProduct product = mdProductMapper.selectMdProductById(productId);
        if (product != null)
        {
            String baseUnit = StringUtils.isNotEmpty(product.getBaseUnit()) ? product.getBaseUnit() : product.getUnitName();
            if (trimmed.equals(baseUnit))
            {
                return BigDecimal.ONE;
            }
            if ((trimmed.equals(product.getPurchaseUnit()) || trimmed.equals(product.getSaleUnit()))
                && product.getUnitConvertRatio() != null && product.getUnitConvertRatio().compareTo(BigDecimal.ZERO) > 0)
            {
                return product.getUnitConvertRatio();
            }
        }
        return BigDecimal.ONE;
    }

    @Override
    public BigDecimal toBaseQuantity(Long productId, String unitName, BigDecimal quantity, BigDecimal explicitRatio)
    {
        BigDecimal qty = quantity == null ? BigDecimal.ZERO : quantity;
        // 若指定了单位名且商品多单位表已配置该单位，强制以服务端配置的换算率为准，防止前端伪造换算率
        BigDecimal ratio = null;
        if (productId != null && StringUtils.isNotEmpty(unitName))
        {
            MdProductUnit configured = mdProductUnitMapper.selectByProductAndUnit(productId, unitName.trim());
            if (configured != null && configured.getConvertRatio() != null && configured.getConvertRatio().compareTo(BigDecimal.ZERO) > 0)
            {
                ratio = configured.getConvertRatio();
            }
        }
        if (ratio == null)
        {
            ratio = (explicitRatio != null && explicitRatio.compareTo(BigDecimal.ZERO) > 0)
                ? explicitRatio
                : resolveConvertRatio(productId, unitName);
        }
        return qty.multiply(ratio).setScale(4, RoundingMode.HALF_UP);
    }

    @Override
    public int insertMdProductUnit(MdProductUnit unit)
    {
        if (unit != null && unit.getProductId() != null)
        {
            ensureBaseUnitPersisted(unit.getProductId());
        }
        validateUnit(unit, true);
        if (StringUtils.isEmpty(unit.getStatus()))
        {
            unit.setStatus("0");
        }
        if ("1".equals(unit.getIsBaseUnit()))
        {
            unit.setConvertRatio(BigDecimal.ONE);
        }
        return mdProductUnitMapper.insertMdProductUnit(unit);
    }

    @Override
    public int updateMdProductUnit(MdProductUnit unit)
    {
        if (unit == null || unit.getUnitId() == null)
        {
            throw new ServiceException("单位记录ID不能为空");
        }
        MdProductUnit existing = mdProductUnitMapper.selectMdProductUnitById(unit.getUnitId());
        if (existing == null)
        {
            throw new ServiceException("计量单位记录不存在");
        }
        if ("1".equals(existing.getIsBaseUnit()) && !"1".equals(unit.getIsBaseUnit()))
        {
            throw new ServiceException("不能取消商品唯一基准单位标识");
        }
        if ("1".equals(existing.getIsBaseUnit()) && "1".equals(unit.getStatus()))
        {
            throw new ServiceException("商品基准单位不允许停用");
        }
        boolean unitNameChanged = StringUtils.isNotEmpty(unit.getUnitName()) && !unit.getUnitName().trim().equals(existing.getUnitName());
        boolean ratioChanged = unit.getConvertRatio() != null && existing.getConvertRatio() != null
            && unit.getConvertRatio().compareTo(existing.getConvertRatio()) != 0;
        if ((unitNameChanged || ratioChanged)
            && mdProductUnitMapper.countUnitReferences(existing.getProductId(), existing.getUnitName()) > 0)
        {
            throw new ServiceException("计量单位[" + existing.getUnitName() + "]已被历史业务单据引用，禁止修改单位名称或换算率，请停用后新建包装单位");
        }
        validateUnit(unit, false);
        if ("1".equals(unit.getIsBaseUnit()))
        {
            unit.setConvertRatio(BigDecimal.ONE);
        }
        return mdProductUnitMapper.updateMdProductUnit(unit);
    }

    @Override
    public int deleteMdProductUnitByIds(Long[] unitIds)
    {
        if (unitIds == null || unitIds.length == 0)
        {
            return 0;
        }
        for (Long unitId : unitIds)
        {
            if (unitId == null)
            {
                throw new ServiceException("无效的单位记录ID");
            }
            MdProductUnit unit = mdProductUnitMapper.selectMdProductUnitById(unitId);
            if (unit == null)
            {
                continue;
            }
            if ("1".equals(unit.getIsBaseUnit()))
            {
                throw new ServiceException("商品基准单位[" + unit.getUnitName() + "]禁止删除");
            }
            if (mdProductUnitMapper.countUnitReferences(unit.getProductId(), unit.getUnitName()) > 0)
            {
                throw new ServiceException("计量单位[" + unit.getUnitName() + "]已被业务单据引用，禁止删除，请改为停用");
            }
        }
        return mdProductUnitMapper.deleteMdProductUnitByIds(unitIds);
    }

    private void validateUnit(MdProductUnit unit, boolean isNew)
    {
        if (unit == null || unit.getProductId() == null)
        {
            throw new ServiceException("商品ID不能为空");
        }
        if (StringUtils.isEmpty(unit.getUnitName()))
        {
            throw new ServiceException("单位名称不能为空");
        }
        unit.setUnitName(unit.getUnitName().trim());
        if (unit.getConvertRatio() == null || unit.getConvertRatio().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("单位换算率必须大于0");
        }
        MdProductUnit existing = mdProductUnitMapper.selectByProductAndUnitAnyStatus(unit.getProductId(), unit.getUnitName());
        if (existing != null && (isNew || !existing.getUnitId().equals(unit.getUnitId())))
        {
            throw new ServiceException("该商品已存在同名计量单位：" + unit.getUnitName());
        }
        if ("1".equals(unit.getIsBaseUnit()))
        {
            MdProductUnit currentBase = mdProductUnitMapper.selectBaseUnitByProductId(unit.getProductId());
            if (currentBase != null && (isNew || !currentBase.getUnitId().equals(unit.getUnitId())))
            {
                throw new ServiceException("该商品已存在基准单位[" + currentBase.getUnitName() + "]，不能设置多个基准单位");
            }
        }
        if (StringUtils.isNotEmpty(unit.getBarCode()))
        {
            String trimmedBarCode = unit.getBarCode().trim();
            unit.setBarCode(trimmedBarCode);
            MdProductUnit clashUnit = mdProductUnitMapper.selectByBarCode(trimmedBarCode);
            if (clashUnit != null && (isNew || !clashUnit.getUnitId().equals(unit.getUnitId())))
            {
                throw new ServiceException("包装条码[" + trimmedBarCode + "]已被其他计量单位使用");
            }
            MdProduct queryProd = new MdProduct();
            queryProd.setBarCode(trimmedBarCode);
            List<MdProduct> clashProducts = mdProductMapper.selectMdProductList(queryProd);
            if (clashProducts != null)
            {
                for (MdProduct p : clashProducts)
                {
                    if (p != null && trimmedBarCode.equals(p.getBarCode()) && !unit.getProductId().equals(p.getProductId()))
                    {
                        throw new ServiceException("包装条码[" + trimmedBarCode + "]与商品[" + p.getProductName() + "]主条码重复");
                    }
                }
            }
        }
    }
}
