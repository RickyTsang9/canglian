package com.canglian.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.canglian.business.domain.FinCostAccount;
import com.canglian.business.domain.FinCostCalculate;
import com.canglian.business.domain.FinCostLayer;
import com.canglian.business.domain.FinCostLog;
import com.canglian.business.mapper.FinCostAccountMapper;
import com.canglian.business.mapper.FinCostLayerMapper;
import com.canglian.business.mapper.FinCostLogMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.WmsStockLogMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.business.service.impl.FinCostCalculateServiceImpl;
import com.canglian.common.exception.ServiceException;
import com.canglian.system.service.ISysConfigService;

/**
 * 成本核算、FIFO分层、退货成本解析与受控调价单元测试 (P4-5)
 */
@ExtendWith(MockitoExtension.class)
class FinCostCalculateServiceTest
{
    @Mock
    private FinCostAccountMapper finCostAccountMapper;

    @Mock
    private FinCostLayerMapper finCostLayerMapper;

    @Mock
    private FinCostLogMapper finCostLogMapper;

    @Mock
    private WmsStockLogMapper wmsStockLogMapper;

    @Mock
    private WmsStockMapper wmsStockMapper;

    @Mock
    private MdProductMapper mdProductMapper;

    @Mock
    private ISysConfigService sysConfigService;

    @Mock
    private IFinGeneralLedgerService finGeneralLedgerService;

    @Mock
    private IFinVoucherEventService finVoucherEventService;

    @InjectMocks
    private FinCostCalculateServiceImpl service;

    @Test
    void shouldCalculateMovingAverageCostAccurately()
    {
        FinCostAccount account = new FinCostAccount();
        account.setCostAccountId(1L);
        account.setProductId(10L);
        account.setWarehouseId(1L);
        account.setCostMethod("moving");
        account.setTotalQty(new BigDecimal("10"));
        account.setTotalAmount(new BigDecimal("100.00"));
        account.setAvgCost(new BigDecimal("10.00"));

        when(sysConfigService.selectConfigByKey(FinCostCalculateServiceImpl.COST_METHOD_CONFIG_KEY)).thenReturn("moving");
        when(finCostAccountMapper.selectFinCostAccountByUnique(10L, 1L, "moving")).thenReturn(account);

        FinCostCalculate inbound = new FinCostCalculate();
        inbound.setProductId(10L);
        inbound.setWarehouseId(1L);
        inbound.setBillType("inbound");
        inbound.setBillId(100L);
        inbound.setInOut("1");
        inbound.setQuantity(new BigDecimal("10"));
        inbound.setPrice(new BigDecimal("20.00"));

        FinCostLog log = service.calculateCost(inbound);
        assertEquals(0, new BigDecimal("200.00").compareTo(log.getAmount()));
        assertEquals(0, new BigDecimal("20").compareTo(account.getTotalQty()));
        assertEquals(0, new BigDecimal("300.00").compareTo(account.getTotalAmount()));
        assertEquals(0, new BigDecimal("15.00").compareTo(account.getAvgCost()));
    }

    @Test
    void shouldConsumeFifoLayersInStrictChronologicalOrder()
    {
        FinCostAccount account = new FinCostAccount();
        account.setCostAccountId(2L);
        account.setProductId(20L);
        account.setWarehouseId(1L);
        account.setCostMethod("fifo");
        account.setTotalQty(new BigDecimal("15"));
        account.setTotalAmount(new BigDecimal("200.00"));
        account.setAvgCost(new BigDecimal("13.33"));

        FinCostLayer layer1 = new FinCostLayer();
        layer1.setCostLayerId(1L);
        layer1.setRemainingQty(new BigDecimal("10"));
        layer1.setPrice(new BigDecimal("10.00"));

        FinCostLayer layer2 = new FinCostLayer();
        layer2.setCostLayerId(2L);
        layer2.setRemainingQty(new BigDecimal("5"));
        layer2.setPrice(new BigDecimal("20.00"));

        when(finCostAccountMapper.selectFinCostAccountByUnique(20L, 1L, "fifo")).thenReturn(account);
        when(finCostLayerMapper.selectAvailableCostLayers(2L)).thenReturn(Arrays.asList(layer1, layer2));

        FinCostCalculate outbound = new FinCostCalculate();
        outbound.setCostMethod("fifo");
        outbound.setProductId(20L);
        outbound.setWarehouseId(1L);
        outbound.setBillType("outbound");
        outbound.setBillId(200L);
        outbound.setInOut("2");
        outbound.setQuantity(new BigDecimal("12"));

        FinCostLog log = service.calculateCost(outbound);
        // 10 * 10 + 2 * 20 = 140
        assertEquals(0, new BigDecimal("140.00").compareTo(log.getAmount()));
        assertEquals(0, BigDecimal.ZERO.compareTo(layer1.getRemainingQty()));
        assertEquals(0, new BigDecimal("3").compareTo(layer2.getRemainingQty()));
        assertEquals(0, new BigDecimal("60.00").compareTo(account.getTotalAmount()));
    }

    @Test
    void shouldBlockMethodSwitchWhenHistoryLogsExist()
    {
        when(sysConfigService.selectConfigByKey(FinCostCalculateServiceImpl.COST_METHOD_CONFIG_KEY)).thenReturn("moving");
        when(finCostLogMapper.countAllCostLogs()).thenReturn(5L);

        assertThrows(ServiceException.class, () -> service.updateGlobalCostMethod("fifo", "admin"));
    }

    @Test
    void shouldResolveReturnCostFromHistoricalOutboundLogInsteadOfSalePrice()
    {
        FinCostLog outLog = new FinCostLog();
        outLog.setQuantity(new BigDecimal("4"));
        outLog.setAmount(new BigDecimal("40.00"));
        when(finCostLogMapper.selectFinCostLogList(any(FinCostLog.class))).thenReturn(Collections.singletonList(outLog));

        BigDecimal resolved = service.resolveReturnUnitCost(1L, 10L, "outbound", 500L, new BigDecimal("99.00"));
        assertEquals(0, new BigDecimal("10.00").compareTo(resolved));
    }

    @Test
    void shouldRecordCostAdjustLogAndVoucherEvent()
    {
        FinCostAccount account = new FinCostAccount();
        account.setCostAccountId(9L);
        account.setProductId(10L);
        account.setWarehouseId(1L);
        account.setTotalQty(new BigDecimal("5"));
        account.setTotalAmount(new BigDecimal("50.00"));
        account.setAvgCost(new BigDecimal("10.00"));

        when(sysConfigService.selectConfigByKey(FinCostCalculateServiceImpl.COST_METHOD_CONFIG_KEY)).thenReturn("moving");
        when(finCostAccountMapper.selectFinCostAccountByUnique(10L, 1L, "moving")).thenReturn(account);

        FinCostLog log = service.adjustCost(1L, 10L, new BigDecimal("12.00"), "补录运费分摊", "admin");
        assertEquals("cost_adjust", log.getBillType());
        assertEquals(0, new BigDecimal("10.00").compareTo(log.getAmount()));
        assertEquals(0, new BigDecimal("60.00").compareTo(account.getTotalAmount()));
        verify(finVoucherEventService).recordVoucherEvent(any(), any(), any(), any(), any(), any(), any(), any());
    }
}
