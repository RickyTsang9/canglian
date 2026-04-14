package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.MdSupplier;
import com.canglian.business.mapper.MdSupplierMapper;
import com.canglian.business.service.IMdSupplierService;

/**
 * 供应商档案 服务层实现
 * 
 * @author canglian
 */
@Service
public class MdSupplierServiceImpl implements IMdSupplierService
{
    @Autowired
    private MdSupplierMapper mdSupplierMapper;

    /**
     * 查询供应商档案信息
     * 
     * @param supplierId 供应商id
     * @return 供应商档案信息
     */
    @Override
    public MdSupplier selectMdSupplierById(Long supplierId)
    {
        return mdSupplierMapper.selectMdSupplierById(supplierId);
    }

    /**
     * 查询供应商档案列表
     * 
     * @param mdSupplier 供应商档案
     * @return 供应商档案集合
     */
    @Override
    public List<MdSupplier> selectMdSupplierList(MdSupplier mdSupplier)
    {
        return mdSupplierMapper.selectMdSupplierList(mdSupplier);
    }

    /**
     * 新增供应商档案
     * 
     * @param mdSupplier 供应商档案
     * @return 结果
     */
    @Override
    public int insertMdSupplier(MdSupplier mdSupplier)
    {
        return mdSupplierMapper.insertMdSupplier(mdSupplier);
    }

    /**
     * 修改供应商档案
     * 
     * @param mdSupplier 供应商档案
     * @return 结果
     */
    @Override
    public int updateMdSupplier(MdSupplier mdSupplier)
    {
        return mdSupplierMapper.updateMdSupplier(mdSupplier);
    }

    /**
     * 删除供应商档案
     * 
     * @param supplierId 供应商id
     * @return 结果
     */
    @Override
    public int deleteMdSupplierById(Long supplierId)
    {
        return mdSupplierMapper.deleteMdSupplierById(supplierId);
    }

    /**
     * 批量删除供应商档案
     * 
     * @param supplierIds 需要删除的供应商id
     * @return 结果
     */
    @Override
    public int deleteMdSupplierByIds(Long[] supplierIds)
    {
        return mdSupplierMapper.deleteMdSupplierByIds(supplierIds);
    }
}

