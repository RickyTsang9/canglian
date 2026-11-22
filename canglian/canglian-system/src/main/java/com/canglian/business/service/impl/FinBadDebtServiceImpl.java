package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.FinBadDebt;
import com.canglian.business.domain.FinReceivable;
import com.canglian.business.mapper.FinBadDebtMapper;
import com.canglian.business.mapper.FinReceivableMapper;
import com.canglian.business.service.IFinBadDebtService;
import com.canglian.business.service.IFinFundLogService;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 坏账 服务层实现（含已核销坏账收回闭环）
 *
 * @author canglian
 */
@Service
public class FinBadDebtServiceImpl implements IFinBadDebtService
{
    private static final DateTimeFormatter NO_DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private FinBadDebtMapper finBadDebtMapper;

    @Autowired
    private FinReceivableMapper finReceivableMapper;

    @Autowired
    private IFinGeneralLedgerService finGeneralLedgerService;

    @Autowired
    private IFinVoucherEventService finVoucherEventService;

    @Autowired(required = false)
    private IFinFundLogService finFundLogService;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Override
    public FinBadDebt selectFinBadDebtById(Long badDebtId)
    {
        FinBadDebt doc = finBadDebtMapper.selectFinBadDebtById(badDebtId);
        if (doc != null && bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(doc.getDeptId(), doc.getOwnerUserId(), null);
        }
        return doc;
    }

    @Override
    public List<FinBadDebt> selectFinBadDebtList(FinBadDebt finBadDebt)
    {
        if (bizScopeHelper != null) bizScopeHelper.applyScopeFilter(finBadDebt, "", true, false);
        return finBadDebtMapper.selectFinBadDebtList(finBadDebt);
    }

    @Override
    public int insertFinBadDebt(FinBadDebt finBadDebt)
    {
        if (bizScopeHelper != null)
        {
            if (finBadDebt.getDeptId() == null) finBadDebt.setDeptId(bizScopeHelper.currentDeptId());
            if (finBadDebt.getOwnerUserId() == null) finBadDebt.setOwnerUserId(bizScopeHelper.currentUserId());
        }
        if (finBadDebt.getBadDebtDate() == null)
        {
            finBadDebt.setBadDebtDate(new Date());
        }
        finGeneralLedgerService.assertPeriodOpen(finBadDebt.getBadDebtDate());
        if (StringUtils.isEmpty(finBadDebt.getBadDebtNo()))
        {
            finBadDebt.setBadDebtNo(generateBadDebtNo());
        }
        validateBadDebt(finBadDebt);
        finBadDebt.setStatus("0");
        finBadDebt.setRecoveredAmount(BigDecimal.ZERO);
        return finBadDebtMapper.insertFinBadDebt(finBadDebt);
    }

    @Override
    public int updateFinBadDebt(FinBadDebt finBadDebt)
    {
        FinBadDebt existing = checkBadDebtEditable(finBadDebt.getBadDebtId());
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(existing.getDeptId(), existing.getOwnerUserId(), null);
        finGeneralLedgerService.assertPeriodOpen(existing.getBadDebtDate() == null ? new Date() : existing.getBadDebtDate());
        if (finBadDebt.getBadDebtDate() != null) finGeneralLedgerService.assertPeriodOpen(finBadDebt.getBadDebtDate());
        validateBadDebt(finBadDebt);
        finBadDebt.setStatus(existing.getStatus());
        return finBadDebtMapper.updateFinBadDebt(finBadDebt);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirmFinBadDebt(Long badDebtId, String operator)
    {
        FinBadDebt badDebt = finBadDebtMapper.selectFinBadDebtById(badDebtId);
        if (badDebt == null) throw new ServiceException("坏账单不存在");
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(badDebt.getDeptId(), badDebt.getOwnerUserId(), null);
        if (!"0".equals(badDebt.getStatus())) throw new ServiceException("坏账单已确认，请勿重复操作");
        Date bizDate = badDebt.getBadDebtDate() == null ? new Date() : badDebt.getBadDebtDate();
        finGeneralLedgerService.assertPeriodOpen(bizDate);
        FinReceivable ar = validateBadDebt(badDebt);
        BigDecimal currentReceived = ar.getReceivedAmount() == null ? BigDecimal.ZERO : ar.getReceivedAmount();
        BigDecimal currentBadDebt = ar.getBadDebtAmount() == null ? BigDecimal.ZERO : ar.getBadDebtAmount();
        BigDecimal totalAr = ar.getAmount() == null ? BigDecimal.ZERO : ar.getAmount();
        // 坏账金额单独记入 fin_receivable.bad_debt_amount，绝不污染现金已收金额 received_amount
        BigDecimal nextBadDebt = currentBadDebt.add(badDebt.getAmount());
        ar.setBadDebtAmount(nextBadDebt);
        ar.setStatus(currentReceived.add(nextBadDebt).compareTo(totalAr) >= 0 ? "2" : "1");
        String existingRemark = StringUtils.isEmpty(ar.getRemark()) ? "" : ar.getRemark() + "；";
        ar.setRemark(existingRemark + "已确认坏账核销：" + badDebt.getBadDebtNo() + "(" + badDebt.getAmount() + ")");
        ar.setUpdateBy(operator);
        finReceivableMapper.updateFinReceivable(ar);

        badDebt.setStatus("1");
        badDebt.setUpdateBy(operator);
        int rows = finBadDebtMapper.updateFinBadDebt(badDebt);
        if (rows > 0)
        {
            finVoucherEventService.recordVoucherEvent("bad_debt", badDebt.getBadDebtId(), badDebt.getBadDebtNo(),
                "bad_debt_confirm", bizDate, badDebt.getAmount(), operator, "坏账确认待生成凭证");
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unconfirmFinBadDebt(Long badDebtId, String operator)
    {
        FinBadDebt badDebt = finBadDebtMapper.selectFinBadDebtById(badDebtId);
        if (badDebt == null) throw new ServiceException("坏账单不存在");
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(badDebt.getDeptId(), badDebt.getOwnerUserId(), null);
        if (!"1".equals(badDebt.getStatus())) throw new ServiceException("仅已确认且未收回状态的坏账单允许反确认");
        BigDecimal recovered = badDebt.getRecoveredAmount() == null ? BigDecimal.ZERO : badDebt.getRecoveredAmount();
        if (recovered.compareTo(BigDecimal.ZERO) > 0)
        {
            throw new ServiceException("该坏账单已发生坏账收回(" + recovered + ")，禁止反确认");
        }
        Date bizDate = badDebt.getBadDebtDate() == null ? new Date() : badDebt.getBadDebtDate();
        finGeneralLedgerService.assertPeriodOpen(bizDate);
        finVoucherEventService.cancelPendingVoucherEvents("bad_debt", badDebtId);

        if (badDebt.getReceivableId() != null)
        {
            FinReceivable ar = finReceivableMapper.selectFinReceivableById(badDebt.getReceivableId());
            if (ar != null)
            {
                BigDecimal currentReceived = ar.getReceivedAmount() == null ? BigDecimal.ZERO : ar.getReceivedAmount();
                BigDecimal currentBadDebt = ar.getBadDebtAmount() == null ? BigDecimal.ZERO : ar.getBadDebtAmount();
                BigDecimal nextBadDebt = currentBadDebt.subtract(badDebt.getAmount());
                if (nextBadDebt.compareTo(BigDecimal.ZERO) < 0) nextBadDebt = BigDecimal.ZERO;
                ar.setBadDebtAmount(nextBadDebt);
                BigDecimal settled = currentReceived.add(nextBadDebt);
                BigDecimal totalAr = ar.getAmount() == null ? BigDecimal.ZERO : ar.getAmount();
                ar.setStatus(settled.compareTo(BigDecimal.ZERO) <= 0 ? "0" : (settled.compareTo(totalAr) >= 0 ? "2" : "1"));
                ar.setUpdateBy(operator);
                finReceivableMapper.updateFinReceivable(ar);
            }
        }
        badDebt.setStatus("0");
        badDebt.setUpdateBy(operator);
        return finBadDebtMapper.updateFinBadDebt(badDebt);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int recoverFinBadDebt(Long badDebtId, BigDecimal recoverAmount, Long fundAccountId, String remark, String operator)
    {
        FinBadDebt badDebt = finBadDebtMapper.selectFinBadDebtById(badDebtId);
        if (badDebt == null) throw new ServiceException("坏账单不存在");
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(badDebt.getDeptId(), badDebt.getOwnerUserId(), null);
        if (!"1".equals(badDebt.getStatus())) throw new ServiceException("仅已确认坏账单允许办理坏账收回");
        if (recoverAmount == null || recoverAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("收回金额必须大于0");
        }
        if (fundAccountId == null) throw new ServiceException("收回入账资金账户不能为空");
        BigDecimal alreadyRecovered = badDebt.getRecoveredAmount() == null ? BigDecimal.ZERO : badDebt.getRecoveredAmount();
        BigDecimal remaining = badDebt.getAmount().subtract(alreadyRecovered);
        if (recoverAmount.compareTo(remaining) > 0)
        {
            throw new ServiceException("收回金额(" + recoverAmount + ")不能超过未收回坏账余额(" + remaining + ")");
        }
        Date now = new Date();
        finGeneralLedgerService.assertPeriodOpen(now);

        if (finFundLogService != null)
        {
            finFundLogService.recordBalanceChange(fundAccountId, "bad_debt_recover",
                badDebt.getBadDebtId(), badDebt.getBadDebtNo(), recoverAmount, now,
                "客户#" + badDebt.getCustomerId() + " 坏账收回",
                StringUtils.isNotEmpty(remark) ? remark : ("坏账单 " + badDebt.getBadDebtNo() + " 收回入账"), operator);
        }

        BigDecimal nextRecovered = alreadyRecovered.add(recoverAmount);
        badDebt.setRecoveredAmount(nextRecovered);
        badDebt.setRecoverTime(now);
        badDebt.setRecoverAccountId(fundAccountId);
        if (nextRecovered.compareTo(badDebt.getAmount()) >= 0)
        {
            badDebt.setStatus("2");
        }
        if (StringUtils.isNotEmpty(remark))
        {
            String oldRemark = StringUtils.isEmpty(badDebt.getRemark()) ? "" : badDebt.getRemark() + "；";
            badDebt.setRemark(oldRemark + "坏账收回(" + recoverAmount + ")：" + remark);
        }
        badDebt.setUpdateBy(operator);
        int rows = finBadDebtMapper.updateFinBadDebt(badDebt);
        if (rows > 0)
        {
            finVoucherEventService.recordVoucherEvent("bad_debt", badDebt.getBadDebtId(), badDebt.getBadDebtNo(),
                "bad_debt_recover", now, recoverAmount, operator, "已核销坏账收回待生成冲减凭证");
        }
        return rows;
    }

    @Override
    public int deleteFinBadDebtById(Long badDebtId)
    {
        FinBadDebt bd = checkBadDebtEditable(badDebtId);
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(bd.getDeptId(), bd.getOwnerUserId(), null);
        finGeneralLedgerService.assertPeriodOpen(bd.getBadDebtDate() == null ? new Date() : bd.getBadDebtDate());
        return finBadDebtMapper.deleteFinBadDebtById(badDebtId);
    }

    @Override
    public int deleteFinBadDebtByIds(Long[] badDebtIds)
    {
        for (Long badDebtId : badDebtIds)
        {
            FinBadDebt bd = checkBadDebtEditable(badDebtId);
            if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(bd.getDeptId(), bd.getOwnerUserId(), null);
            finGeneralLedgerService.assertPeriodOpen(bd.getBadDebtDate() == null ? new Date() : bd.getBadDebtDate());
        }
        return finBadDebtMapper.deleteFinBadDebtByIds(badDebtIds);
    }

    private FinBadDebt checkBadDebtEditable(Long badDebtId)
    {
        FinBadDebt existing = finBadDebtMapper.selectFinBadDebtById(badDebtId);
        if (existing == null) throw new ServiceException("坏账单不存在");
        if (!"0".equals(existing.getStatus())) throw new ServiceException("坏账单已确认，不允许修改或删除");
        return existing;
    }

    private FinReceivable validateBadDebt(FinBadDebt badDebt)
    {
        if (badDebt.getReceivableId() == null) throw new ServiceException("关联应收单不能为空");
        if (badDebt.getAmount() == null || badDebt.getAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("坏账金额必须大于0");
        }
        FinReceivable ar = finReceivableMapper.selectFinReceivableById(badDebt.getReceivableId());
        if (ar == null) throw new ServiceException("关联应收单不存在");
        if ("2".equals(ar.getBillDirection())) throw new ServiceException("红字应收冲减单不允许计提坏账");
        if (!"confirmed".equals(ar.getBizStatus())) throw new ServiceException("仅已审核确认的应收单允许计提坏账");
        if (badDebt.getCustomerId() == null)
        {
            badDebt.setCustomerId(ar.getCustomerId());
        }
        else if (!Objects.equals(badDebt.getCustomerId(), ar.getCustomerId()))
        {
            throw new ServiceException("坏账单客户与关联应收单客户不一致");
        }
        BigDecimal totalAr = ar.getAmount() == null ? BigDecimal.ZERO : ar.getAmount();
        BigDecimal received = ar.getReceivedAmount() == null ? BigDecimal.ZERO : ar.getReceivedAmount();
        BigDecimal existingBadDebt = ar.getBadDebtAmount() == null ? BigDecimal.ZERO : ar.getBadDebtAmount();
        BigDecimal unpaid = totalAr.subtract(received).subtract(existingBadDebt);
        if (badDebt.getAmount().compareTo(unpaid) > 0)
        {
            throw new ServiceException("坏账金额(" + badDebt.getAmount() + ")不能大于应收单剩余未结余额(" + unpaid + ")");
        }
        return ar;
    }

    private String generateBadDebtNo()
    {
        String prefix = "bd" + LocalDate.now().format(NO_DATE_FMT);
        FinBadDebt q = new FinBadDebt();
        q.setBadDebtNo(prefix);
        List<FinBadDebt> list = finBadDebtMapper.selectFinBadDebtList(q);
        int maxSeq = 0;
        if (list != null)
        {
            for (FinBadDebt row : list)
            {
                String no = row.getBadDebtNo();
                if (no != null && no.startsWith(prefix) && no.length() > prefix.length())
                {
                    String tail = no.substring(prefix.length());
                    if (StringUtils.isNumeric(tail)) maxSeq = Math.max(maxSeq, Integer.parseInt(tail));
                }
            }
        }
        return prefix + String.format("%04d", maxSeq + 1);
    }
}
