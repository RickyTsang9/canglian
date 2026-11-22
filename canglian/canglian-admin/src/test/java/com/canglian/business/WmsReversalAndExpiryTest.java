package com.canglian.business;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.canglian.business.domain.WmsInbound;
import com.canglian.business.domain.WmsOutbound;
import com.canglian.business.domain.WmsOutboundItem;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.mapper.WmsInboundMapper;
import com.canglian.business.mapper.WmsOutboundItemMapper;
import com.canglian.business.mapper.WmsOutboundMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.impl.WmsInboundServiceImpl;
import com.canglian.business.service.impl.WmsOutboundServiceImpl;
import com.canglian.common.exception.ServiceException;

/**
 * 仓储红冲反审核与过期批次出库硬拦截单元测试 (P5-2 & P6-2)
 */
@ExtendWith(MockitoExtension.class)
class WmsReversalAndExpiryTest
{
    @Mock
    private WmsOutboundMapper wmsOutboundMapper;

    @Mock
    private WmsOutboundItemMapper wmsOutboundItemMapper;

    @Mock
    private WmsStockMapper wmsStockMapper;

    @Mock
    private WmsInboundMapper wmsInboundMapper;

    @Mock
    private IFinGeneralLedgerService finGeneralLedgerService;

    @InjectMocks
    private WmsOutboundServiceImpl outboundService;

    @InjectMocks
    private WmsInboundServiceImpl inboundService;

    @Test
    void shouldBlockAuditOutboundWhenBatchIsExpired()
    {
        WmsOutbound outbound = new WmsOutbound();
        outbound.setOutboundId(500L);
        outbound.setOutboundNo("CK20261122001");
        outbound.setWarehouseId(1L);
        outbound.setStatus("0");
        outbound.setBusinessDate(new Date());

        WmsOutboundItem item = new WmsOutboundItem();
        item.setProductId(10L);
        item.setLocationId(0L);
        item.setBatchNo("EXP-BATCH-01");
        item.setQuantity(new BigDecimal("2"));
        item.setConvertRatio(BigDecimal.ONE);
        item.setPrice(new BigDecimal("100"));

        WmsStock expiredStock = new WmsStock();
        expiredStock.setStockId(99L);
        expiredStock.setWarehouseId(1L);
        expiredStock.setProductId(10L);
        expiredStock.setBatchNo("EXP-BATCH-01");
        expiredStock.setQuantity(new BigDecimal("20"));
        expiredStock.setAvailableQuantity(new BigDecimal("20"));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -5);
        expiredStock.setExpiryDate(cal.getTime());

        when(wmsOutboundMapper.selectWmsOutboundByIdForUpdate(500L)).thenReturn(outbound);
        when(wmsOutboundItemMapper.selectWmsOutboundItemList(any(WmsOutboundItem.class)))
            .thenReturn(Collections.singletonList(item));
        when(wmsStockMapper.selectWmsStockByKeyForUpdate(any(WmsStock.class)))
            .thenReturn(expiredStock);

        assertThrows(ServiceException.class, () -> outboundService.auditWmsOutbound(500L, "admin"));
    }

    @Test
    void shouldRejectDoubleReversalOfAlreadyReversedInbound()
    {
        WmsInbound inbound = new WmsInbound();
        inbound.setInboundId(600L);
        inbound.setInboundNo("RK20261122001");
        inbound.setStatus("1");
        inbound.setIsReversal("0");
        inbound.setReversedFlag("1");

        when(wmsInboundMapper.selectWmsInboundByIdForUpdate(600L)).thenReturn(inbound);

        assertThrows(ServiceException.class, () -> inboundService.reverseWmsInbound(600L, "admin"));
    }
}
