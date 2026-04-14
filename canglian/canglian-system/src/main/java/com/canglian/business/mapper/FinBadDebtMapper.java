package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.FinBadDebt;

/**
 * 坏账 数据层
 * 
 * @author canglian
 */
public interface FinBadDebtMapper
{
    /**
     * 查询坏账信息
     * 
     * @param badDebtId 坏账id
     * @return 坏账信息
     */
    public FinBadDebt selectFinBadDebtById(Long badDebtId);

    /**
     * 查询坏账列表
     * 
     * @param finBadDebt 坏账信息
     * @return 坏账集合
     */
    public List<FinBadDebt> selectFinBadDebtList(FinBadDebt finBadDebt);

    /**
     * 新增坏账
     * 
     * @param finBadDebt 坏账信息
     * @return 结果
     */
    public int insertFinBadDebt(FinBadDebt finBadDebt);

    /**
     * 修改坏账
     * 
     * @param finBadDebt 坏账信息
     * @return 结果
     */
    public int updateFinBadDebt(FinBadDebt finBadDebt);

    /**
     * 删除坏账
     * 
     * @param badDebtId 坏账id
     * @return 结果
     */
    public int deleteFinBadDebtById(Long badDebtId);

    /**
     * 批量删除坏账
     * 
     * @param badDebtIds 需要删除的坏账id
     * @return 结果
     */
    public int deleteFinBadDebtByIds(Long[] badDebtIds);
}

