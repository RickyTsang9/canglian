package com.canglian.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.canglian.business.domain.PurOrder;
import com.canglian.business.domain.PurOrderItem;
import com.canglian.business.domain.SalOrder;
import com.canglian.business.domain.SalOrderItem;
import com.canglian.business.mapper.PurOrderItemMapper;
import com.canglian.business.mapper.PurOrderMapper;
import com.canglian.business.mapper.SalOrderItemMapper;
import com.canglian.business.mapper.SalOrderMapper;
import com.canglian.business.mapper.WmsInboundMapper;
import com.canglian.business.mapper.WmsOutboundMapper;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IWmsInboundService;
import com.canglian.business.service.IWmsOutboundService;
import com.canglian.business.service.IWmsStockService;
import com.canglian.business.service.impl.PurOrderServiceImpl;
import com.canglian.business.service.impl.SalOrderServiceImpl;
import com.canglian.common.exception.ServiceException;
import com.canglian.system.service.ISysConfigService;

/**
 * 采购/销售订单分批下推、折扣税率、反审批与强制关闭单元测试 (P5-1 & P6-4)
 */
@ExtendWith(MockitoExtension.class)
class PurAndSalOrderWorkflowTest
{
    @Mock
    private PurOrderMapper purOrderMapper;

    @Mock
    private PurOrderItemMapper purOrderItemMapper;

    @Mock
    private WmsInboundMapper wmsInboundMapper;

    @Mock
    private IWmsInboundService wmsInboundService;

    @Mock
    private SalOrderMapper salOrderMapper;

    @Mock
    private SalOrderItemMapper salOrderItemMapper;

    @Mock
    private WmsOutboundMapper wmsOutboundMapper;

    @Mock
    private IWmsOutboundService wmsOutboundService;

    @Mock
    private IWmsStockService wmsStockService;

    @Mock
    private IFinGeneralLedgerService finGeneralLedgerService;

    @Mock
    private ISysConfigService sysConfigService;

    @InjectMocks
    private PurOrderServiceImpl purOrderService;

    @InjectMocks
    private SalOrderServiceImpl salOrderService;

    @Test
    void shouldRejectOverPushingPurchaseOrderBeyondRemainingQuantity()
    {
        PurOrder order = new PurOrder();
        order.setPurchaseOrderId(100L);
        order.setPurchaseOrderNo("PO20261122001");
        order.setSupplierId(1L);
        order.setWarehouseId(1L);
        order.setBizStatus("approved");

        PurOrderItem item = new PurOrderItem();
        item.setPurchaseOrderItemId(1001L);
        item.setProductId(10L);
        item.setQuantity(new BigDecimal("10"));
        item.setReceivedQty(new BigDecimal("6"));
        item.setPrice(new BigDecimal("50"));
        item.setAmount(new BigDecimal("500"));

        when(purOrderMapper.selectPurOrderById(100L)).thenReturn(order);
        when(purOrderItemMapper.selectPurOrderItemListByOrderId(100L)).thenReturn(Collections.singletonList(item));

        Map<String, Object> overReq = new HashMap<String, Object>();
        overReq.put("purchaseOrderItemId", 1001L);
        overReq.put("pushQty", new BigDecimal("5"));

        assertThrows(ServiceException.class,
            () -> purOrderService.pushPurOrderToInbound(100L, Collections.singletonList(overReq), "admin"));
    }

    @Test
    void shouldRejectUnapprovePurOrderWhenAlreadyExecuting()
    {
        PurOrder order = new PurOrder();
        order.setPurchaseOrderId(101L);
        order.setBizStatus("executing");
        when(purOrderMapper.selectPurOrderById(101L)).thenReturn(order);

        assertThrows(ServiceException.class, () -> purOrderService.unapprovePurOrder(101L, "admin"));
    }

    @Test
    void shouldReleaseRemainingStockLockOnForceCloseSaleOrder()
    {
        SalOrder order = new SalOrder();
        order.setOrderId(200L);
        order.setOrderNo("SO20261122001");
        order.setOrderType("sale");
        order.setWarehouseId(1L);
        order.setBizStatus("executing");

        SalOrderItem item = new SalOrderItem();
        item.setOrderItemId(2001L);
        item.setProductId(10L);
        item.setQuantity(new BigDecimal("10"));
        item.setShippedQty(new BigDecimal("4"));
        item.setConvertRatio(BigDecimal.ONE);

        when(sysConfigService.selectConfigByKey("canglian.stock.lockOnSaleApprove")).thenReturn("true");
        when(salOrderMapper.selectSalOrderById(200L)).thenReturn(order);
        when(salOrderItemMapper.selectSalOrderItemListByOrderId(200L)).thenReturn(Collections.singletonList(item));
        when(salOrderMapper.updateSalOrder(any(SalOrder.class))).thenReturn(1);

        int rows = salOrderService.closeSalOrder(200L, "客户取消余单", "admin");

        assertEquals(1, rows);
        assertEquals("closed", order.getBizStatus());
        assertEquals("客户取消余单", order.getCloseReason());
        verify(wmsStockService).releaseStockLockForSaleOrder(1L, 10L, new BigDecimal("6.0000"), 200L, "admin");
    }
}
