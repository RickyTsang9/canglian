package com.canglian.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.canglian.business.domain.FinProfitLossReport;
import com.canglian.business.mapper.FinReportMapper;
import com.canglian.business.service.impl.FinReportServiceImpl;

/**
 * 权责发生制利润表口径核算单元测试 (P4-4)
 */
@ExtendWith(MockitoExtension.class)
class FinReportProfitLossCalculationTest
{
    @Mock
    private FinReportMapper finReportMapper;

    @InjectMocks
    private FinReportServiceImpl reportService;

    @Test
    void shouldComputeAccrualProfitLossAndMarginsCorrectly()
    {
        FinProfitLossReport raw = new FinProfitLossReport();
        raw.setSalesIncomeAmount(new BigDecimal("100.00"));
        raw.setSaleReturnAmount(new BigDecimal("25.00"));
        raw.setSalesCostAmount(new BigDecimal("30.00"));
        raw.setExpenseAmount(new BigDecimal("15.00"));
        raw.setBadDebtAmount(new BigDecimal("0.00"));

        when(finReportMapper.selectProfitLossReport("2026-01-01", "2026-12-31")).thenReturn(raw);

        FinProfitLossReport res = reportService.selectProfitLossReport("2026-01-01", "2026-12-31");
        assertEquals(0, new BigDecimal("75.00").compareTo(res.getNetIncomeAmount()));
        assertEquals(0, new BigDecimal("45.00").compareTo(res.getGrossProfitAmount()));
        assertEquals(0, new BigDecimal("60.00").compareTo(res.getGrossMarginRate()));
        assertEquals(0, new BigDecimal("30.00").compareTo(res.getNetProfitAmount()));
        assertEquals(0, new BigDecimal("40.00").compareTo(res.getNetMarginRate()));
        // 兼容字段对齐
        assertEquals(0, new BigDecimal("75.00").compareTo(res.getIncomeAmount()));
        assertEquals(0, new BigDecimal("45.00").compareTo(res.getCostAmount()));
        assertEquals(0, new BigDecimal("30.00").compareTo(res.getProfitAmount()));
    }

    @Test
    void shouldHandleZeroNetIncomeWithoutDivisionByZero()
    {
        FinProfitLossReport raw = new FinProfitLossReport();
        raw.setSalesIncomeAmount(BigDecimal.ZERO);
        raw.setSaleReturnAmount(BigDecimal.ZERO);
        raw.setSalesCostAmount(BigDecimal.ZERO);
        raw.setExpenseAmount(new BigDecimal("10.00"));
        raw.setBadDebtAmount(BigDecimal.ZERO);

        when(finReportMapper.selectProfitLossReport("2026-01-01", "2026-12-31")).thenReturn(raw);

        FinProfitLossReport res = reportService.selectProfitLossReport("2026-01-01", "2026-12-31");
        assertEquals(0, BigDecimal.ZERO.compareTo(res.getGrossMarginRate()));
        assertEquals(0, new BigDecimal("-10.00").compareTo(res.getNetProfitAmount()));
    }
}
