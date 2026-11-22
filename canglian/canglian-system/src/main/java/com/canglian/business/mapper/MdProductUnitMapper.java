package com.canglian.business.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.canglian.business.domain.MdProductUnit;

/**
 * 商品多单位 Mapper
 *
 * @author canglian
 */
public interface MdProductUnitMapper
{
    public MdProductUnit selectMdProductUnitById(Long unitId);

    public List<MdProductUnit> selectMdProductUnitList(MdProductUnit mdProductUnit);

    public MdProductUnit selectByProductAndUnit(@Param("productId") Long productId, @Param("unitName") String unitName);

    public MdProductUnit selectByProductAndUnitAnyStatus(@Param("productId") Long productId, @Param("unitName") String unitName);

    public MdProductUnit selectBaseUnitByProductId(@Param("productId") Long productId);

    public MdProductUnit selectByBarCode(@Param("barCode") String barCode);

    public int countUnitReferences(@Param("productId") Long productId, @Param("unitName") String unitName);

    public int insertMdProductUnit(MdProductUnit mdProductUnit);

    public int updateMdProductUnit(MdProductUnit mdProductUnit);

    public int deleteMdProductUnitById(Long unitId);

    public int deleteMdProductUnitByIds(Long[] unitIds);
}
