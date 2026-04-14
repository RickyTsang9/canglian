package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.MdCustomer;

/**
 * 客户档案 数据层
 * 
 * @author canglian
 */
public interface MdCustomerMapper
{
    /**
     * 查询客户档案信息
     * 
     * @param customerId 客户id
     * @return 客户档案信息
     */
    public MdCustomer selectMdCustomerById(Long customerId);

    /**
     * 查询客户档案列表
     * 
     * @param mdCustomer 客户档案
     * @return 客户档案集合
     */
    public List<MdCustomer> selectMdCustomerList(MdCustomer mdCustomer);

    /**
     * 新增客户档案
     * 
     * @param mdCustomer 客户档案
     * @return 结果
     */
    public int insertMdCustomer(MdCustomer mdCustomer);

    /**
     * 修改客户档案
     * 
     * @param mdCustomer 客户档案
     * @return 结果
     */
    public int updateMdCustomer(MdCustomer mdCustomer);

    /**
     * 删除客户档案
     * 
     * @param customerId 客户id
     * @return 结果
     */
    public int deleteMdCustomerById(Long customerId);

    /**
     * 批量删除客户档案
     * 
     * @param customerIds 需要删除的客户id
     * @return 结果
     */
    public int deleteMdCustomerByIds(Long[] customerIds);
}

