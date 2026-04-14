package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.MdSupplier;

/**
 * 供应商档案 数据层
 * 
 * @author canglian
 */
public interface MdSupplierMapper
{
    /**
     * 查询供应商档案信息
     * 
     * @param supplierId 供应商id
     * @return 供应商档案信息
     */
    public MdSupplier selectMdSupplierById(Long supplierId);

    /**
     * 查询供应商档案列表
     * 
     * @param mdSupplier 供应商档案
     * @return 供应商档案集合
     */
    public List<MdSupplier> selectMdSupplierList(MdSupplier mdSupplier);

    /**
     * 新增供应商档案
     * 
     * @param mdSupplier 供应商档案
     * @return 结果
     */
    public int insertMdSupplier(MdSupplier mdSupplier);

    /**
     * 修改供应商档案
     * 
     * @param mdSupplier 供应商档案
     * @return 结果
     */
    public int updateMdSupplier(MdSupplier mdSupplier);

    /**
     * 删除供应商档案
     * 
     * @param supplierId 供应商id
     * @return 结果
     */
    public int deleteMdSupplierById(Long supplierId);

    /**
     * 批量删除供应商档案
     * 
     * @param supplierIds 需要删除的供应商id
     * @return 结果
     */
    public int deleteMdSupplierByIds(Long[] supplierIds);
}

