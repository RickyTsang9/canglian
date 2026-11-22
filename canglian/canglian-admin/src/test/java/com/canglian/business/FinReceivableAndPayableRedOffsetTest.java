package com.canglian.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.canglian.business.domain.FinPayable;
import com.canglian.business.domain.FinReceivable;
import com.canglian.business.mapper.FinPayableMapper;
import com.canglian.business.mapper.FinReceivableMapper;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.impl.FinPayableServiceImpl;
import com.canglian.business.service.impl.FinReceivableServiceImpl;
import com.canglian.common.exception.ServiceException;

/**
 * 退货红字应收/应付自动生成与红蓝对冲单元测试 (P4-3)
 */
@ExtendWith(MockitoExtension.class)
class FinReceivableAndPayableRedOffsetTest
{
    @Mock
    private FinReceivableMapper finReceivableMapper;

    @Mock
    private FinPayableMapper finPayableMapper;

    @Mock
    private IFinGeneralLedgerService finGeneralLedgerService;

    @InjectMocks
    private FinReceivableServiceImpl receivableService;

    @InjectMocks
    private FinPayableServiceImpl payableService;

    @Test
    void shouldCreateNegativeConfirmedRedReceivableIdempotently()
    {
        when(finReceivableMapper.selectFinReceivableBySource("sale_return", 88L)).thenReturn(null);
        when(finReceivableMapper.selectMaxReceivableNoByPrefix(any())).thenReturn(null);

        FinReceivable red = receivableService.createRedReceivableForReturn(
            1L, "sale_return", 88L, "srt20261122001", new Date(), new BigDecimal("300.00"), "admin", "退货冲减");

        assertEquals("2", red.getBillDirection());
        assertEquals("confirmed", red.getBizStatus());
        assertEquals(0, new BigDecimal("-300.00").compareTo(red.getAmount()));
        verify(finReceivableMapper).insertFinReceivable(any(FinReceivable.class));
    }

    @Test
    void shouldConserveCustomerNetBalanceDuringRedBlueReceivableOffset()
    {
        FinReceivable blue = new FinReceivable();
        blue.setReceivableId(10L);
        blue.setCustomerId(1L);
        blue.setBillDirection("1");
        blue.setBizStatus("confirmed");
        blue.setAmount(new BigDecimal("1000.00"));
        blue.setReceivedAmount(BigDecimal.ZERO);

        FinReceivable red = new FinReceivable();
        red.setReceivableId(11L);
        red.setCustomerId(1L);
        red.setBillDirection("2");
        red.setBizStatus("confirmed");
        red.setAmount(new BigDecimal("-300.00"));
        red.setReceivedAmount(BigDecimal.ZERO);

        when(finReceivableMapper.selectFinReceivableById(11L)).thenReturn(red);
        when(finReceivableMapper.selectFinReceivableById(10L)).thenReturn(blue);
        when(finReceivableMapper.updateFinReceivable(any(FinReceivable.class))).thenReturn(1);

        receivableService.offsetRedReceivable(11L, 10L, new BigDecimal("300.00"), "admin");

        assertEquals(0, new BigDecimal("300.00").compareTo(blue.getReceivedAmount()));
        assertEquals("1", blue.getStatus());
        assertEquals(0, new BigDecimal("-300.00").compareTo(red.getReceivedAmount()));
        assertEquals("2", red.getStatus());

        // 客户净欠款守恒：(1000 - 300) + (-300 - (-300)) = 700
        BigDecimal netBalance = blue.getAmount().subtract(blue.getReceivedAmount())
            .add(red.getAmount().subtract(red.getReceivedAmount()));
        assertEquals(0, new BigDecimal("700.00").compareTo(netBalance));
    }

    @Test
    void shouldRejectCrossSupplierPayableOffset()
    {
        FinPayable red = new FinPayable();
        red.setPayableId(21L);
        red.setSupplierId(1L);
        red.setBillDirection("2");
        red.setBizStatus("confirmed");
        red.setAmount(new BigDecimal("-200.00"));
        red.setPaidAmount(BigDecimal.ZERO);

        FinPayable blue = new FinPayable();
        blue.setPayableId(20L);
        blue.setSupplierId(2L);
        blue.setBillDirection("1");
        blue.setBizStatus("confirmed");
        blue.setAmount(new BigDecimal("500.00"));
        blue.setPaidAmount(BigDecimal.ZERO);

        when(finPayableMapper.selectFinPayableById(21L)).thenReturn(red);
        when(finPayableMapper.selectFinPayableById(20L)).thenReturn(blue);

        assertThrows(ServiceException.class,
            () -> payableService.offsetRedPayable(21L, 20L, new BigDecimal("100.00"), "admin"));
    }
}
