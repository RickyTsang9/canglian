package com.canglian.business.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.canglian.business.domain.FinCostAccount;

/**
 * 成本账户 数据层
 * 
 * @author canglian
 */
public interface FinCostAccountMapper
{
    /**
     * 查询成本账户信息
     * 
     * @param costAccountId 成本账户id
     * @return 成本账户信息
     */
    public FinCostAccount selectFinCostAccountById(Long costAccountId);

    /**
     * 查询成本账户列表
     * 
     * @param finCostAccount 成本账户信息
     * @return 成本账户集合
     */
    public List<FinCostAccount> selectFinCostAccountList(FinCostAccount finCostAccount);

    /**
     * 查询成本账户信息
     * 
     * @param productId 商品id
     * @param warehouseId 仓库id
     * @param costMethod 成本算法
     * @return 成本账户信息
     */
    public FinCostAccount selectFinCostAccountByUnique(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId, @Param("costMethod") String costMethod);

    /**
     * 新增成本账户
     * 
     * @param finCostAccount 成本账户信息
     * @return 结果
     */
    public int insertFinCostAccount(FinCostAccount finCostAccount);

    /**
     * 修改成本账户
     * 
     * @param finCostAccount 成本账户信息
     * @return 结果
     */
    public int updateFinCostAccount(FinCostAccount finCostAccount);

    /**
     * 删除成本账户
     * 
     * @param costAccountId 成本账户id
     * @return 结果
     */
    public int deleteFinCostAccountById(Long costAccountId);

    /**
     * 批量删除成本账户
     * 
     * @param costAccountIds 需要删除的成本账户id
     * @return 结果
     */
    public int deleteFinCostAccountByIds(Long[] costAccountIds);
}

