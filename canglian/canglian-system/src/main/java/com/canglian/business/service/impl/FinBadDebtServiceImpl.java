package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinBadDebt;
import com.canglian.business.mapper.FinBadDebtMapper;
import com.canglian.business.service.IFinBadDebtService;

/**
 * 坏账 服务层实现
 * 
 * @author canglian
 */
@Service
public class FinBadDebtServiceImpl implements IFinBadDebtService
{
    @Autowired
    private FinBadDebtMapper finBadDebtMapper;

    /**
     * 查询坏账信息
     * 
     * @param badDebtId 坏账id
     * @return 坏账信息
     */
    @Override
    public FinBadDebt selectFinBadDebtById(Long badDebtId)
    {
        return finBadDebtMapper.selectFinBadDebtById(badDebtId);
    }

    /**
     * 查询坏账列表
     * 
     * @param finBadDebt 坏账信息
     * @return 坏账集合
     */
    @Override
    public List<FinBadDebt> selectFinBadDebtList(FinBadDebt finBadDebt)
    {
        return finBadDebtMapper.selectFinBadDebtList(finBadDebt);
    }

    /**
     * 新增坏账
     * 
     * @param finBadDebt 坏账信息
     * @return 结果
     */
    @Override
    public int insertFinBadDebt(FinBadDebt finBadDebt)
    {
        return finBadDebtMapper.insertFinBadDebt(finBadDebt);
    }

    /**
     * 修改坏账
     * 
     * @param finBadDebt 坏账信息
     * @return 结果
     */
    @Override
    public int updateFinBadDebt(FinBadDebt finBadDebt)
    {
        return finBadDebtMapper.updateFinBadDebt(finBadDebt);
    }

    /**
     * 删除坏账
     * 
     * @param badDebtId 坏账id
     * @return 结果
     */
    @Override
    public int deleteFinBadDebtById(Long badDebtId)
    {
        return finBadDebtMapper.deleteFinBadDebtById(badDebtId);
    }

    /**
     * 批量删除坏账
     * 
     * @param badDebtIds 需要删除的坏账id
     * @return 结果
     */
    @Override
    public int deleteFinBadDebtByIds(Long[] badDebtIds)
    {
        return finBadDebtMapper.deleteFinBadDebtByIds(badDebtIds);
    }
}

