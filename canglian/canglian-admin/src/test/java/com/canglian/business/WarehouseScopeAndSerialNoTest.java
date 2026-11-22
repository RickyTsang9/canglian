package com.canglian.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.canglian.business.domain.WmsSerialNo;
import com.canglian.business.domain.WmsSerialNoLog;
import com.canglian.business.mapper.FinGeneralLedgerMapper;
import com.canglian.business.mapper.FinVoucherEntryMapper;
import com.canglian.business.mapper.WmsSerialNoMapper;
import com.canglian.business.service.impl.FinGeneralLedgerServiceImpl;
import com.canglian.business.service.impl.WmsSerialNoServiceImpl;
import com.canglian.common.exception.ServiceException;

/**
 * 序列号(SN)唯一性/流转闭环 & 总账科目引用保护/年度建期单元测试 (P5-5 & P6-3)
 */
@ExtendWith(MockitoExtension.class)
class WarehouseScopeAndSerialNoTest
{
    @Mock
    private WmsSerialNoMapper wmsSerialNoMapper;

    @Mock
    private FinGeneralLedgerMapper finGeneralLedgerMapper;

    @Mock
    private FinVoucherEntryMapper finVoucherEntryMapper;

    @InjectMocks
    private WmsSerialNoServiceImpl serialNoService;

    @InjectMocks
    private FinGeneralLedgerServiceImpl generalLedgerService;

    @Test
    void shouldRejectDuplicateSerialNoRegistration()
    {
        WmsSerialNo existing = new WmsSerialNo();
        existing.setSnCode("SN-DUP-001");
        when(wmsSerialNoMapper.selectWmsSerialNoByCode("SN-DUP-001")).thenReturn(existing);

        assertThrows(ServiceException.class, () -> serialNoService.batchRegister(
            10L, 1L, 0L, "B01", null, "RK001", null, Collections.singletonList("SN-DUP-001"), null, "admin"));
    }

    @Test
    void shouldConsumeInStockSerialNoAndWriteLog()
    {
        WmsSerialNo sn = new WmsSerialNo();
        sn.setSerialId(1L);
        sn.setSnCode("SN-OK-001");
        sn.setWarehouseId(1L);
        sn.setStatus("in_stock");
        when(wmsSerialNoMapper.selectWmsSerialNoByCode("SN-OK-001")).thenReturn(sn);

        int count = serialNoService.consumeSerialNos(Arrays.asList("SN-OK-001"), 99L, "CK001", 5L, "销售出库", "admin");

        assertEquals(1, count);
        assertEquals("out_stock", sn.getStatus());
        verify(wmsSerialNoMapper).updateWmsSerialNo(any(WmsSerialNo.class));
        verify(wmsSerialNoMapper).insertWmsSerialNoLog(any(WmsSerialNoLog.class));
    }

    @Test
    void shouldPreventDeletingAccountSubjectReferencedByVoucherTemplate()
    {
        Map<String, Object> subject = new HashMap<String, Object>();
        subject.put("subjectId", 1L);
        subject.put("subjectCode", "1405");
        when(finGeneralLedgerMapper.selectAccountSubjectById(1L)).thenReturn(subject);
        when(finVoucherEntryMapper.countTemplateBySubjectCode("1405")).thenReturn(2);

        assertThrows(ServiceException.class, () -> generalLedgerService.deleteAccountSubjectById(1L));
    }

    @Test
    void shouldInitTwelveMonthlyFiscalPeriodsIdempotently()
    {
        when(finGeneralLedgerMapper.countFiscalPeriodByCode(any())).thenReturn(0);

        Map<String, Object> result = generalLedgerService.initFiscalPeriodsByYear(2028, "admin");

        assertEquals(12, result.get("createdCount"));
        assertEquals(0, result.get("skippedCount"));
        verify(finGeneralLedgerMapper, times(12)).insertFiscalPeriod(any(), any(), any(), any(), any());
    }
}
