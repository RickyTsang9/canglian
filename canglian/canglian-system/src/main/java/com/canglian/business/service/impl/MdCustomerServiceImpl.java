package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.MdCustomer;
import com.canglian.business.mapper.MdCustomerMapper;
import com.canglian.business.service.IMdCustomerService;

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
}

