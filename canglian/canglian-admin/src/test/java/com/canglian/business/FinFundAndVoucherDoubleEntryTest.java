package com.canglian.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.canglian.business.domain.FinBadDebt;
import com.canglian.business.domain.FinFundAccount;
import com.canglian.business.domain.FinVoucherEntry;
import com.canglian.business.domain.FinVoucherEvent;
import com.canglian.business.domain.FinVoucherTemplate;
import com.canglian.business.mapper.FinBadDebtMapper;
import com.canglian.business.mapper.FinFundAccountMapper;
import com.canglian.business.mapper.FinVoucherEntryMapper;
import com.canglian.business.mapper.FinVoucherEventMapper;
import com.canglian.business.service.IFinFundLogService;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.business.service.impl.FinBadDebtServiceImpl;
import com.canglian.business.service.impl.FinFundAccountServiceImpl;
import com.canglian.business.service.impl.FinVoucherEventServiceImpl;
import com.canglian.common.exception.ServiceException;

/**
 * 资金账户余额锁、坏账收回、复式记账借贷平衡与红字冲销单元测试 (P5-3, P5-4, P5-5)
 */
@ExtendWith(MockitoExtension.class)
class FinFundAndVoucherDoubleEntryTest
{
    @Mock
    private FinFundAccountMapper finFundAccountMapper;

    @Mock
    private IFinFundLogService finFundLogService;

    @Mock
    private FinBadDebtMapper finBadDebtMapper;

    @Mock
    private IFinGeneralLedgerService finGeneralLedgerService;

    @Mock
    private IFinVoucherEventService mockVoucherEventService;

    @Mock
    private FinVoucherEventMapper finVoucherEventMapper;

    @Mock
    private FinVoucherEntryMapper finVoucherEntryMapper;

    @InjectMocks
    private FinFundAccountServiceImpl fundAccountService;

    @InjectMocks
    private FinBadDebtServiceImpl badDebtService;

    @InjectMocks
    private FinVoucherEventServiceImpl voucherEventService;

    @Test
    void shouldBlockDirectBalanceTamperingWhenAccountHasFundLogs()
    {
        FinFundAccount existing = new FinFundAccount();
        existing.setAccountId(1L);
        existing.setAccountName("基本户");
        existing.setBalance(new BigDecimal("10000.00"));

        FinFundAccount update = new FinFundAccount();
        update.setAccountId(1L);
        update.setBalance(new BigDecimal("99999.00"));

        when(finFundLogService.hasLogs(1L)).thenReturn(true);
        when(finFundAccountMapper.selectFinFundAccountById(1L)).thenReturn(existing);

        assertThrows(ServiceException.class, () -> fundAccountService.updateFinFundAccount(update));
    }

    @Test
    void shouldRecoverConfirmedBadDebtAndMarkStatus2WhenFullyRecovered()
    {
        FinBadDebt badDebt = new FinBadDebt();
        badDebt.setBadDebtId(77L);
        badDebt.setBadDebtNo("bd202611220001");
        badDebt.setAmount(new BigDecimal("500.00"));
        badDebt.setRecoveredAmount(BigDecimal.ZERO);
        badDebt.setStatus("1");

        when(finBadDebtMapper.selectFinBadDebtById(77L)).thenReturn(badDebt);
        when(finBadDebtMapper.updateFinBadDebt(any(FinBadDebt.class))).thenReturn(1);

        int rows = badDebtService.recoverFinBadDebt(77L, new BigDecimal("500.00"), 1L, "法院执行追回", "admin");

        assertEquals(1, rows);
        assertEquals("2", badDebt.getStatus());
        assertEquals(0, new BigDecimal("500.00").compareTo(badDebt.getRecoveredAmount()));
        verify(finFundLogService).recordBalanceChange(
            org.mockito.ArgumentMatchers.eq(1L),
            org.mockito.ArgumentMatchers.eq("bad_debt_recover"),
            org.mockito.ArgumentMatchers.eq(77L),
            org.mockito.ArgumentMatchers.eq("bd202611220001"),
            org.mockito.ArgumentMatchers.eq(new BigDecimal("500.00")),
            any(Date.class), any(), any(), org.mockito.ArgumentMatchers.eq("admin"));
    }

    @Test
    void shouldGenerateBalancedDoubleEntryVoucherEntries()
    {
        FinVoucherEvent ev = new FinVoucherEvent();
        ev.setVoucherEventId(88L);
        ev.setBillType("inbound");
        ev.setBillNo("RK20261122001");
        ev.setEventType("inbound_complete");
        ev.setEventDate(new Date());
        ev.setEventAmount(new BigDecimal("1250.50"));
        ev.setStatus("0");

        FinVoucherTemplate dTpl = new FinVoucherTemplate();
        dTpl.setEntrySeq(1);
        dTpl.setDirection("debit");
        dTpl.setSubjectCode("1405");
        dTpl.setSubjectName("库存商品");
        dTpl.setAuxiliaryType("warehouse");
        dTpl.setSummaryTemplate("采购入库 {billNo}");

        FinVoucherTemplate cTpl = new FinVoucherTemplate();
        cTpl.setEntrySeq(2);
        cTpl.setDirection("credit");
        cTpl.setSubjectCode("2202");
        cTpl.setSubjectName("应付账款");
        cTpl.setAuxiliaryType("supplier");
        cTpl.setSummaryTemplate("采购入库应付 {billNo}");

        when(finVoucherEventMapper.selectFinVoucherEventById(88L)).thenReturn(ev);
        when(finVoucherEntryMapper.selectEnabledTemplatesByEventType("inbound_complete")).thenReturn(Arrays.asList(dTpl, cTpl));
        when(finVoucherEventMapper.selectMaxVoucherNoByPrefix(any())).thenReturn(null);
        when(finVoucherEventMapper.updateFinVoucherEvent(any(FinVoucherEvent.class))).thenReturn(1);

        voucherEventService.generateVoucher(88L, "admin");

        ArgumentCaptor<FinVoucherEntry> captor = ArgumentCaptor.forClass(FinVoucherEntry.class);
        verify(finVoucherEntryMapper, atLeast(2)).insertVoucherEntry(captor.capture());
        List<FinVoucherEntry> inserted = captor.getAllValues();
        BigDecimal sumDebit = inserted.stream().map(FinVoucherEntry::getDebitAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumCredit = inserted.stream().map(FinVoucherEntry::getCreditAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, sumDebit.compareTo(sumCredit));
        assertEquals(0, new BigDecimal("1250.50").compareTo(sumDebit));
    }
}
