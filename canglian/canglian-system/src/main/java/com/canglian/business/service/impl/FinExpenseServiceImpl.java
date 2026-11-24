package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.FinExpense;
import com.canglian.business.mapper.FinExpenseMapper;
import com.canglian.business.service.IFinExpenseService;
import com.canglian.business.service.IFinFundLogService;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 费用 服务层实现
 *
 * @author canglian
 */
@Service
public class FinExpenseServiceImpl implements IFinExpenseService
{
    private static final DateTimeFormatter NO_DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private FinExpenseMapper finExpenseMapper;

    @Autowired
    private IFinFundLogService finFundLogService;

    @Autowired
    private IFinGeneralLedgerService finGeneralLedgerService;

    @Autowired
    private IFinVoucherEventService finVoucherEventService;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Override
    public FinExpense selectFinExpenseById(Long expenseId)
    {
        FinExpense exp = finExpenseMapper.selectFinExpenseById(expenseId);
        if (exp != null && bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(exp.getDeptId(), exp.getOwnerUserId());
        }
        return exp;
    }

    @Override
    public List<FinExpense> selectFinExpenseList(FinExpense finExpense)
    {
        if (bizScopeHelper != null)
        {
            bizScopeHelper.applyScopeFilter(finExpense, "", true, false);
        }
        return finExpenseMapper.selectFinExpenseList(finExpense);
    }

    @Override
    public int insertFinExpense(FinExpense finExpense)
    {
        if (bizScopeHelper != null)
        {
            if (finExpense.getDeptId() == null) finExpense.setDeptId(bizScopeHelper.getCurrentDeptId());
            if (finExpense.getOwnerUserId() == null) finExpense.setOwnerUserId(bizScopeHelper.getCurrentUserId());
        }
        if (finExpense.getExpenseDate() == null)
        {
            finExpense.setExpenseDate(new Date());
        }
        finGeneralLedgerService.assertPeriodOpen(finExpense.getExpenseDate());
        if (StringUtils.isEmpty(finExpense.getExpenseNo()))
        {
            finExpense.setExpenseNo(generateExpenseNo());
        }
        if (StringUtils.isEmpty(finExpense.getExpenseType()))
        {
            throw new ServiceException("费用类别不能为空");
        }
        validateExpenseAmount(finExpense.getAmount());
        finExpense.setStatus("0");
        return finExpenseMapper.insertFinExpense(finExpense);
    }

    @Override
    public int updateFinExpense(FinExpense finExpense)
    {
        FinExpense existingExpense = checkExpenseEditable(finExpense.getExpenseId());
        finGeneralLedgerService.assertPeriodOpen(existingExpense.getExpenseDate() == null ? new Date() : existingExpense.getExpenseDate());
        if (finExpense.getExpenseDate() != null) finGeneralLedgerService.assertPeriodOpen(finExpense.getExpenseDate());
        if (StringUtils.isEmpty(finExpense.getExpenseType()))
        {
            throw new ServiceException("费用类别不能为空");
        }
        validateExpenseAmount(finExpense.getAmount());
        finExpense.setStatus(existingExpense.getStatus());
        return finExpenseMapper.updateFinExpense(finExpense);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirmFinExpense(Long expenseId, String operator)
    {
        FinExpense finExpense = getExistingExpense(expenseId);
        if ("1".equals(finExpense.getStatus()))
        {
            throw new ServiceException("费用单已确认，请勿重复操作");
        }
        if (finExpense.getFundAccountId() == null)
        {
            throw new ServiceException("确认费用支出必须选择资金账户");
        }
        validateExpenseAmount(finExpense.getAmount());
        Date bizDate = finExpense.getExpenseDate() == null ? new Date() : finExpense.getExpenseDate();
        finGeneralLedgerService.assertPeriodOpen(bizDate);
        finFundLogService.recordBalanceChange(finExpense.getFundAccountId(), "expense",
            finExpense.getExpenseId(), finExpense.getExpenseNo(), finExpense.getAmount().negate(),
            bizDate, "费用支出：" + (StringUtils.isEmpty(finExpense.getExpenseType()) ? "期间费用" : finExpense.getExpenseType()),
            finExpense.getRemark(), operator);
        finExpense.setStatus("1");
        finExpense.setUpdateBy(operator);
        int rows = finExpenseMapper.updateFinExpense(finExpense);
        if (rows > 0)
        {
            finVoucherEventService.recordVoucherEvent("expense", finExpense.getExpenseId(), finExpense.getExpenseNo(),
                "expense_confirm", bizDate, finExpense.getAmount(), operator, "费用单确认待生成凭证");
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unconfirmFinExpense(Long expenseId, String operator)
    {
        FinExpense finExpense = getExistingExpense(expenseId);
        if (!"1".equals(finExpense.getStatus()))
        {
            throw new ServiceException("仅已确认状态的费用单允许反确认");
        }
        Date bizDate = finExpense.getExpenseDate() == null ? new Date() : finExpense.getExpenseDate();
        finGeneralLedgerService.assertPeriodOpen(bizDate);
        finVoucherEventService.cancelPendingVoucherEvents("expense", expenseId);
        if (finExpense.getFundAccountId() != null)
        {
            finFundLogService.recordBalanceChange(finExpense.getFundAccountId(), "unconfirm",
                finExpense.getExpenseId(), finExpense.getExpenseNo(), finExpense.getAmount(),
                new Date(), "费用单反确认冲回", "反确认费用单：" + finExpense.getExpenseNo(), operator);
        }
        finExpense.setStatus("0");
        finExpense.setUpdateBy(operator);
        return finExpenseMapper.updateFinExpense(finExpense);
    }

    private void validateExpenseAmount(BigDecimal amount)
    {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("费用金额必须大于0");
        }
    }

    @Override
    public int deleteFinExpenseById(Long expenseId)
    {
        FinExpense exp = checkExpenseEditable(expenseId);
        finGeneralLedgerService.assertPeriodOpen(exp.getExpenseDate() == null ? new Date() : exp.getExpenseDate());
        return finExpenseMapper.deleteFinExpenseById(expenseId);
    }

    @Override
    public int deleteFinExpenseByIds(Long[] expenseIds)
    {
        for (Long expenseId : expenseIds)
        {
            FinExpense exp = checkExpenseEditable(expenseId);
            finGeneralLedgerService.assertPeriodOpen(exp.getExpenseDate() == null ? new Date() : exp.getExpenseDate());
        }
        return finExpenseMapper.deleteFinExpenseByIds(expenseIds);
    }

    private String generateExpenseNo()
    {
        String prefix = "exp" + LocalDate.now().format(NO_DATE_FMT);
        FinExpense q = new FinExpense();
        q.setExpenseNo(prefix);
        List<FinExpense> list = finExpenseMapper.selectFinExpenseList(q);
        int maxSeq = 0;
        if (list != null)
        {
            for (FinExpense row : list)
            {
                String no = row.getExpenseNo();
                if (no != null && no.startsWith(prefix) && no.length() > prefix.length())
                {
                    String tail = no.substring(prefix.length());
                    if (StringUtils.isNumeric(tail)) maxSeq = Math.max(maxSeq, Integer.parseInt(tail));
                }
            }
        }
        return prefix + String.format("%04d", maxSeq + 1);
    }

    private FinExpense getExistingExpense(Long expenseId)
    {
        FinExpense finExpense = finExpenseMapper.selectFinExpenseById(expenseId);
        if (finExpense == null) throw new ServiceException("费用单不存在");
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(finExpense.getDeptId(), finExpense.getOwnerUserId());
        }
        return finExpense;
    }

    private FinExpense checkExpenseEditable(Long expenseId)
    {
        FinExpense finExpense = getExistingExpense(expenseId);
        if ("1".equals(finExpense.getStatus()))
        {
            throw new ServiceException("费用单已确认，不允许修改或删除");
        }
        return finExpense;
    }
}
