package com.canglian.business.service;

import java.math.BigDecimal;
import java.util.List;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.MdProductUnit;

/**
 * 商品多单位服务接口
 *
 * @author canglian
 */
public interface IMdProductUnitService
{
    public MdProductUnit selectMdProductUnitById(Long unitId);

    public List<MdProductUnit> selectMdProductUnitList(MdProductUnit query);

    public List<MdProductUnit> selectUnitsByProductId(Long productId);

    public MdProductUnit ensureBaseUnitPersisted(Long productId);

    public void syncBaseUnitForProduct(MdProduct product);

    public BigDecimal resolveConvertRatio(Long productId, String unitName);

    public BigDecimal toBaseQuantity(Long productId, String unitName, BigDecimal quantity, BigDecimal explicitRatio);

    public int insertMdProductUnit(MdProductUnit unit);

    public int updateMdProductUnit(MdProductUnit unit);

    public int deleteMdProductUnitByIds(Long[] unitIds);
}
