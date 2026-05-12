package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.MdCustomer;
import com.canglian.business.mapper.MdCustomerMapper;
import com.canglian.business.service.IMdCustomerService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 客户档案 服务层实现
 *
 * @author canglian
 */
@Service
public class MdCustomerServiceImpl implements IMdCustomerService
{
    @Autowired
    private MdCustomerMapper mdCustomerMapper;

    /**
     * 查询客户档案信息
     *
     * @param customerId 客户id
     * @return 客户档案信息
     */
    @Override
    public MdCustomer selectMdCustomerById(Long customerId)
    {
        return mdCustomerMapper.selectMdCustomerById(customerId);
    }

    /**
     * 查询客户档案列表
     *
     * @param mdCustomer 客户档案
     * @return 客户档案集合
     */
    @Override
    public List<MdCustomer> selectMdCustomerList(MdCustomer mdCustomer)
    {
        return mdCustomerMapper.selectMdCustomerList(mdCustomer);
    }

    /**
     * 导入客户档案
     *
     * @param customerList 客户档案集合
     * @param updateSupport 是否更新已存在数据
     * @param operator 操作人
     * @return 导入结果
     */
    @Override
    public String importMdCustomer(List<MdCustomer> customerList, Boolean updateSupport, String operator)
    {
        if (StringUtils.isNull(customerList) || customerList.isEmpty())
        {
            throw new ServiceException("导入客户数据不能为空");
        }
        int successNumber = 0;
        StringBuilder failureMessage = new StringBuilder();
        for (int customerIndex = 0; customerIndex < customerList.size(); customerIndex++)
        {
            MdCustomer mdCustomer = customerList.get(customerIndex);
            try
            {
                validateImportCustomer(mdCustomer);
                MdCustomer existingMdCustomer = mdCustomerMapper.selectMdCustomerByCode(mdCustomer.getCustomerCode());
                if (existingMdCustomer == null)
                {
                    mdCustomer.setCreateBy(operator);
                    insertMdCustomer(mdCustomer);
                    successNumber++;
                }
                else if (Boolean.TRUE.equals(updateSupport))
                {
                    mdCustomer.setCustomerId(existingMdCustomer.getCustomerId());
                    mdCustomer.setUpdateBy(operator);
                    updateMdCustomer(mdCustomer);
                    successNumber++;
                }
                else
                {
                    failureMessage.append("<br/>第").append(customerIndex + 1).append("行客户编码已存在");
                }
            }
            catch (Exception exception)
            {
                failureMessage.append("<br/>第").append(customerIndex + 1).append("行导入失败：").append(exception.getMessage());
            }
        }
        return buildImportMessage("客户", successNumber, failureMessage);
    }

    /**
     * 新增客户档案
     *
     * @param mdCustomer 客户档案
     * @return 结果
     */
    @Override
    public int insertMdCustomer(MdCustomer mdCustomer)
    {
        return mdCustomerMapper.insertMdCustomer(mdCustomer);
    }

    /**
     * 修改客户档案
     *
     * @param mdCustomer 客户档案
     * @return 结果
     */
    @Override
    public int updateMdCustomer(MdCustomer mdCustomer)
    {
        return mdCustomerMapper.updateMdCustomer(mdCustomer);
    }

    /**
     * 删除客户档案
     *
     * @param customerId 客户id
     * @return 结果
     */
    @Override
    public int deleteMdCustomerById(Long customerId)
    {
        return mdCustomerMapper.deleteMdCustomerById(customerId);
    }

    /**
     * 批量删除客户档案
     *
     * @param customerIds 需要删除的客户id
     * @return 结果
     */
    @Override
    public int deleteMdCustomerByIds(Long[] customerIds)
    {
        return mdCustomerMapper.deleteMdCustomerByIds(customerIds);
    }

    /**
     * 校验导入客户
     *
     * @param mdCustomer 客户档案
     */
    private void validateImportCustomer(MdCustomer mdCustomer)
    {
        if (StringUtils.isEmpty(mdCustomer.getCustomerCode()))
        {
            throw new ServiceException("客户编码不能为空");
        }
        if (StringUtils.isEmpty(mdCustomer.getCustomerName()))
        {
            throw new ServiceException("客户名称不能为空");
        }
        if (StringUtils.isEmpty(mdCustomer.getStatus()))
        {
            mdCustomer.setStatus("0");
        }
        if (StringUtils.isEmpty(mdCustomer.getDelFlag()))
        {
            mdCustomer.setDelFlag("0");
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

