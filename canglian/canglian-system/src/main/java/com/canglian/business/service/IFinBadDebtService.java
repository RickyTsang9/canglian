package com.canglian.business.service;

import java.math.BigDecimal;
import java.util.List;
import com.canglian.business.domain.FinBadDebt;

/**
 * 坏账 服务层
 *
 * @author canglian
 */
public interface IFinBadDebtService
{
    public FinBadDebt selectFinBadDebtById(Long badDebtId);

    public List<FinBadDebt> selectFinBadDebtList(FinBadDebt finBadDebt);

    public int insertFinBadDebt(FinBadDebt finBadDebt);

    public int updateFinBadDebt(FinBadDebt finBadDebt);

    public int confirmFinBadDebt(Long badDebtId, String operator);

    /** 反确认已确认但未收回的坏账单 */
    public int unconfirmFinBadDebt(Long badDebtId, String operator);

    /** 已核销坏账事后收回（增加资金账户余额、记录资金流水与坏账收回凭证事件） */
    public int recoverFinBadDebt(Long badDebtId, BigDecimal recoverAmount, Long fundAccountId, String remark, String operator);

    public int deleteFinBadDebtById(Long badDebtId);

    public int deleteFinBadDebtByIds(Long[] badDebtIds);
}
