package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.MdSupplier;
import com.canglian.business.mapper.MdSupplierMapper;
import com.canglian.business.service.IMdSupplierService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

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
     * 导入供应商档案
     *
     * @param supplierList 供应商档案集合
     * @param updateSupport 是否更新已存在数据
     * @param operator 操作人
     * @return 导入结果
     */
    @Override
    public String importMdSupplier(List<MdSupplier> supplierList, Boolean updateSupport, String operator)
    {
        if (StringUtils.isNull(supplierList) || supplierList.isEmpty())
        {
            throw new ServiceException("导入供应商数据不能为空");
        }
        int successNumber = 0;
        StringBuilder failureMessage = new StringBuilder();
        for (int supplierIndex = 0; supplierIndex < supplierList.size(); supplierIndex++)
        {
            MdSupplier mdSupplier = supplierList.get(supplierIndex);
            try
            {
                validateImportSupplier(mdSupplier);
                MdSupplier existingMdSupplier = mdSupplierMapper.selectMdSupplierByCode(mdSupplier.getSupplierCode());
                if (existingMdSupplier == null)
                {
                    mdSupplier.setCreateBy(operator);
                    insertMdSupplier(mdSupplier);
                    successNumber++;
                }
                else if (Boolean.TRUE.equals(updateSupport))
                {
                    mdSupplier.setSupplierId(existingMdSupplier.getSupplierId());
                    mdSupplier.setUpdateBy(operator);
                    updateMdSupplier(mdSupplier);
                    successNumber++;
                }
                else
                {
                    failureMessage.append("<br/>第").append(supplierIndex + 1).append("行供应商编码已存在");
                }
            }
            catch (Exception exception)
            {
                failureMessage.append("<br/>第").append(supplierIndex + 1).append("行导入失败：").append(exception.getMessage());
            }
        }
        return buildImportMessage("供应商", successNumber, failureMessage);
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

    /**
     * 校验导入供应商
     *
     * @param mdSupplier 供应商档案
     */
    private void validateImportSupplier(MdSupplier mdSupplier)
    {
        if (StringUtils.isEmpty(mdSupplier.getSupplierCode()))
        {
            throw new ServiceException("供应商编码不能为空");
        }
        if (StringUtils.isEmpty(mdSupplier.getSupplierName()))
        {
            throw new ServiceException("供应商名称不能为空");
        }
        if (StringUtils.isEmpty(mdSupplier.getStatus()))
        {
            mdSupplier.setStatus("0");
        }
        if (StringUtils.isEmpty(mdSupplier.getDelFlag()))
        {
            mdSupplier.setDelFlag("0");
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

