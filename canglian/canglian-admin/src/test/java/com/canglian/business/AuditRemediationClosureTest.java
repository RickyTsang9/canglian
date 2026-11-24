package com.canglian.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import com.canglian.business.domain.FinCostAccount;
import com.canglian.business.domain.FinCostCalculate;
import com.canglian.business.domain.FinReceivable;
import com.canglian.business.domain.MdLocation;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.MdWarehouse;
import com.canglian.business.domain.SalOrder;
import com.canglian.business.domain.WmsOutbound;
import com.canglian.business.domain.WmsOutboundItem;
import com.canglian.business.domain.WmsSerialNo;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsStockLog;
import com.canglian.business.mapper.FinCostAccountMapper;
import com.canglian.business.mapper.FinCostLayerMapper;
import com.canglian.business.mapper.FinCostLogMapper;
import com.canglian.business.mapper.FinGeneralLedgerMapper;
import com.canglian.business.mapper.FinReceivableMapper;
import com.canglian.business.mapper.MdLocationMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.MdWarehouseMapper;
import com.canglian.business.mapper.SalOrderMapper;
import com.canglian.business.mapper.WmsOutboundItemMapper;
import com.canglian.business.mapper.WmsOutboundMapper;
import com.canglian.business.mapper.WmsSerialNoMapper;
import com.canglian.business.mapper.WmsStockLogMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IFinCostCalculateService;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IWmsOutboundService;
import com.canglian.business.service.impl.BizScopeHelper;
import com.canglian.business.service.impl.BusinessScanServiceImpl;
import com.canglian.business.service.impl.FinCostCalculateServiceImpl;
import com.canglian.business.service.impl.FinGeneralLedgerServiceImpl;
import com.canglian.business.service.impl.FinReceivableServiceImpl;
import com.canglian.business.service.impl.SalOrderServiceImpl;
import com.canglian.business.service.impl.WmsOutboundServiceImpl;
import com.canglian.business.service.impl.WmsSerialNoServiceImpl;
import com.canglian.business.service.impl.WmsStockServiceImpl;
import com.canglian.common.core.domain.entity.SysRole;
import com.canglian.common.core.domain.entity.SysUser;
import com.canglian.common.core.domain.model.LoginUser;
import com.canglian.common.exception.ServiceException;
import com.canglian.system.service.ISysConfigService;

/**
 * 仓链 ERP 全模块深度审计 30 项闭环专项回归测试
 */
@ExtendWith(MockitoExtension.class)
class AuditRemediationClosureTest
{
    @Mock
    private WmsOutboundMapper wmsOutboundMapper;

    @Mock
    private WmsOutboundItemMapper wmsOutboundItemMapper;

    @Mock
    private FinReceivableMapper finReceivableMapper;

    @Mock
    private IFinGeneralLedgerService finGeneralLedgerService;

    @Mock
    private FinCostAccountMapper finCostAccountMapper;

    @Mock
    private FinCostLayerMapper finCostLayerMapper;

    @Mock
    private FinCostLogMapper finCostLogMapper;

    @Mock
    private ISysConfigService sysConfigService;

    @Mock
    private WmsSerialNoMapper wmsSerialNoMapper;

    @Mock
    private SalOrderMapper salOrderMapper;

    @Mock
    private FinGeneralLedgerMapper finGeneralLedgerMapper;

    @Mock
    private WmsStockMapper wmsStockMapper;

    @Mock
    private WmsStockLogMapper wmsStockLogMapper;

    @Mock
    private MdWarehouseMapper mdWarehouseMapper;

    @Mock
    private MdProductMapper mdProductMapper;

    @Mock
    private MdLocationMapper mdLocationMapper;

    @Mock
    private IFinCostCalculateService finCostCalculateService;

    @Mock
    private IWmsOutboundService wmsOutboundServiceMock;

    @InjectMocks
    private WmsOutboundServiceImpl wmsOutboundService;

    @InjectMocks
    private FinReceivableServiceImpl finReceivableService;

    @InjectMocks
    private FinCostCalculateServiceImpl finCostCalculateServiceImpl;

    @InjectMocks
    private BusinessScanServiceImpl businessScanService;

    @InjectMocks
    private WmsSerialNoServiceImpl wmsSerialNoService;

    @InjectMocks
    private SalOrderServiceImpl salOrderService;

    @InjectMocks
    private FinGeneralLedgerServiceImpl finGeneralLedgerServiceImpl;

    @InjectMocks
    private WmsStockServiceImpl wmsStockService;

    @Test
    void testP0_01_OutboundUnauditBlockedWhenShippedOrSigned()
    {
        WmsOutbound shipped = new WmsOutbound();
        shipped.setOutboundId(101L);
        shipped.setOutboundNo("OUT20261124001");
        shipped.setStatus("1");
        shipped.setDeliveryStatus("1");
        shipped.setBusinessDate(new Date());
        when(wmsOutboundMapper.selectWmsOutboundByIdForUpdate(101L)).thenReturn(shipped);

        ServiceException ex = assertThrows(ServiceException.class,
            () -> wmsOutboundService.unauditWmsOutbound(101L, "admin"));
        assertTrue(ex.getMessage().contains("已发货") || ex.getMessage().contains("已签收"));
    }

    @Test
    void testP0_04_OpeningReceivableCreatedConfirmedAndProtected()
    {
        when(finReceivableMapper.selectFinReceivableBySource("customer_opening", 88L)).thenReturn(null);
        when(finReceivableMapper.selectMaxReceivableNoByPrefix(any())).thenReturn(null);

        FinReceivable created = finReceivableService.syncOpeningReceivable(
            88L, "CUST088", new BigDecimal("5000.00"), 30, "admin");
        assertNotNull(created);
        assertEquals("opening", created.getBillType());
        assertEquals("confirmed", created.getBizStatus());
        assertEquals(0, new BigDecimal("5000.00").compareTo(created.getAmount()));
        verify(finReceivableMapper).insertFinReceivable(any(FinReceivable.class));

        FinReceivable existingPaid = new FinReceivable();
        existingPaid.setReceivableId(201L);
        existingPaid.setBillType("opening");
        existingPaid.setBizStatus("confirmed");
        existingPaid.setAmount(new BigDecimal("5000.00"));
        existingPaid.setReceivedAmount(new BigDecimal("1000.00"));
        when(finReceivableMapper.selectFinReceivableBySource("customer_opening", 88L)).thenReturn(existingPaid);

        assertThrows(ServiceException.class,
            () -> finReceivableService.syncOpeningReceivable(88L, "CUST088", new BigDecimal("6000.00"), 30, "admin"));
    }

    @Test
    void testP0_06_SimulateCostIsStrictlyReadOnlySandbox()
    {
        FinCostAccount acc = new FinCostAccount();
        acc.setCostAccountId(1L);
        acc.setProductId(10L);
        acc.setWarehouseId(1L);
        acc.setCostMethod("moving");
        acc.setTotalQty(new BigDecimal("10"));
        acc.setTotalAmount(new BigDecimal("1000.00"));
        acc.setAvgCost(new BigDecimal("100.00"));
        when(sysConfigService.selectConfigByKey("canglian.cost.defaultMethod")).thenReturn("moving");
        when(finCostAccountMapper.selectFinCostAccountByUnique(10L, 1L, "moving")).thenReturn(acc);

        FinCostCalculate req = new FinCostCalculate();
        req.setProductId(10L);
        req.setWarehouseId(1L);
        req.setInOut("1");
        req.setQuantity(new BigDecimal("10"));
        req.setPrice(new BigDecimal("200.00"));

        Map<String, Object> sim = finCostCalculateServiceImpl.simulateCost(req);
        assertEquals(Boolean.TRUE, sim.get("simulated"));
        assertEquals(0, new BigDecimal("150.00").compareTo(new BigDecimal(String.valueOf(sim.get("afterAvgCost")))));
        verify(finCostAccountMapper, never()).insertFinCostAccount(any());
        verify(finCostAccountMapper, never()).updateFinCostAccount(any());
        verify(finCostLayerMapper, never()).insertFinCostLayer(any());
        verify(finCostLogMapper, never()).insertFinCostLog(any());
    }

    @Test
    void testP0_09_ScanVerifyRejectsDraftAndExtraUnlistedSku()
    {
        WmsOutbound draft = new WmsOutbound();
        draft.setOutboundId(501L);
        draft.setStatus("0");
        when(wmsOutboundServiceMock.selectWmsOutboundById(501L)).thenReturn(draft);
        assertThrows(ServiceException.class,
            () -> businessScanService.verifyOutboundByScan(501L, Collections.emptyList(), "admin"));

        WmsOutboundItem expected = new WmsOutboundItem();
        expected.setProductId(10L);
        expected.setQuantity(new BigDecimal("2"));
        expected.setBaseQuantity(new BigDecimal("2"));

        WmsOutbound audited = new WmsOutbound();
        audited.setOutboundId(502L);
        audited.setOutboundNo("OUT502");
        audited.setStatus("1");
        audited.setIsReversal("0");
        audited.setReversedFlag("0");
        audited.setItemList(Collections.singletonList(expected));
        when(wmsOutboundServiceMock.selectWmsOutboundById(502L)).thenReturn(audited);

        Map<String, Object> scan1 = new HashMap<String, Object>();
        scan1.put("productId", 10L);
        scan1.put("quantity", 2);
        Map<String, Object> extraScan = new HashMap<String, Object>();
        extraScan.put("productId", 99L);
        extraScan.put("quantity", 1);

        Map<String, Object> res = businessScanService.verifyOutboundByScan(502L, Arrays.asList(scan1, extraScan), "admin");
        assertEquals(Boolean.FALSE, res.get("passed"));
        verify(wmsOutboundMapper, never()).updateWmsOutbound(any());
    }

    @Test
    void testP0_09_InventoryCheckSerialLossAndRollback()
    {
        MdProduct prod = new MdProduct();
        prod.setProductId(77L);
        prod.setProductName("序列号测试商品");
        prod.setEnableSerial("1");

        WmsSerialNo sn = new WmsSerialNo();
        sn.setSerialId(1L);
        sn.setSnCode("SN-CHK-001");
        sn.setProductId(77L);
        sn.setWarehouseId(1L);
        sn.setLocationId(0L);
        sn.setBatchNo("");
        sn.setStatus("in_stock");
        when(wmsSerialNoMapper.selectWmsSerialNoByCode("SN-CHK-001")).thenReturn(sn);

        wmsSerialNoService.handleInventoryCheckSerials(prod, 1L, 0L, "", 300L, "CHK300", null,
            "SN-CHK-001", new BigDecimal("-1"), "admin");
        assertEquals("loss", sn.getStatus());

        wmsSerialNoService.rollbackInventoryCheckSerials(prod, 1L, 0L, 300L, "CHK300",
            "SN-CHK-001", new BigDecimal("-1"), false, "admin");
        assertEquals("in_stock", sn.getStatus());
    }

    @Test
    void testP0_10_SelfOnlyScopeNeverLeaksNullDeptRecords()
    {
        BizScopeHelper helper = new BizScopeHelper();
        SysUser user = new SysUser();
        user.setUserId(99L);
        user.setDeptId(105L);
        SysRole selfRole = new SysRole();
        selfRole.setRoleId(5L);
        selfRole.setDataScope("5");
        selfRole.setStatus("0");
        user.setRoles(Collections.singletonList(selfRole));
        LoginUser loginUser = new LoginUser(99L, 105L, user, Collections.emptySet());
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(loginUser, null));
        try
        {
            SalOrder q = new SalOrder();
            helper.applyScopeFilter(q, "o", true, false);
            String sql = String.valueOf(q.getParams().get("dataScope"));
            assertTrue(sql.contains("o.owner_user_id = 99"));
            assertFalse(sql.contains("dept_id IS NULL"));
        }
        finally
        {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void testP1_01_CloseSalOrderBlockedWithInFlightOutbound()
    {
        SalOrder order = new SalOrder();
        order.setOrderId(601L);
        order.setOrderNo("SO601");
        order.setOrderType("sale");
        order.setBizStatus("approved");
        order.setBusinessDate(new Date());
        when(salOrderMapper.selectSalOrderById(601L)).thenReturn(order);

        WmsOutbound inFlight = new WmsOutbound();
        inFlight.setOutboundId(701L);
        inFlight.setOutboundNo("OUT701");
        inFlight.setSourceBillType("sale_order");
        inFlight.setSourceBillId(601L);
        inFlight.setStatus("0");
        inFlight.setBizStatus("draft");
        inFlight.setIsReversal("0");
        inFlight.setReversedFlag("0");
        when(wmsOutboundMapper.selectWmsOutboundList(any())).thenReturn(Collections.singletonList(inFlight));

        ServiceException ex = assertThrows(ServiceException.class,
            () -> salOrderService.closeSalOrder(601L, "客户取消", "admin"));
        assertTrue(ex.getMessage().contains("OUT701"));
    }

    @Test
    void testP1_12_GeneralLedgerTrialBalanceCalculation()
    {
        Map<String, Object> cash = new LinkedHashMap<String, Object>();
        cash.put("subjectCode", "1002");
        cash.put("subjectName", "银行存款");
        cash.put("subjectType", "asset");
        cash.put("balanceDirection", "debit");
        cash.put("openingDebit", new BigDecimal("1000.00"));
        cash.put("openingCredit", BigDecimal.ZERO);
        cash.put("periodDebit", new BigDecimal("500.00"));
        cash.put("periodCredit", BigDecimal.ZERO);

        Map<String, Object> capital = new LinkedHashMap<String, Object>();
        capital.put("subjectCode", "4001");
        capital.put("subjectName", "实收资本");
        capital.put("subjectType", "equity");
        capital.put("balanceDirection", "credit");
        capital.put("openingDebit", BigDecimal.ZERO);
        capital.put("openingCredit", new BigDecimal("1000.00"));
        capital.put("periodDebit", BigDecimal.ZERO);
        capital.put("periodCredit", new BigDecimal("500.00"));

        when(finGeneralLedgerMapper.selectTrialBalance("2026-01-01", "2026-12-31"))
            .thenReturn(Arrays.asList(cash, capital));

        Map<String, Object> tb = finGeneralLedgerServiceImpl.selectTrialBalance("2026-01-01", "2026-12-31");
        assertEquals(Boolean.TRUE, tb.get("periodBalanced"));
        assertEquals(Boolean.TRUE, tb.get("endingBalanced"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) tb.get("rows");
        assertEquals(2, rows.size());
    }

    @Test
    void testP1_14_StockOpeningImportResolvesCodesAndSyncsLogAndCost()
    {
        MdWarehouse wh = new MdWarehouse();
        wh.setWarehouseId(1L);
        wh.setWarehouseCode("WH01");
        wh.setWarehouseName("主仓");
        wh.setStatus("0");
        when(mdWarehouseMapper.selectMdWarehouseList(any())).thenReturn(Collections.singletonList(wh));

        MdProduct prod = new MdProduct();
        prod.setProductId(10L);
        prod.setProductCode("SKU01");
        prod.setProductName("测试商品");
        prod.setCostPrice(new BigDecimal("50.00"));
        prod.setEnableBatch("0");
        prod.setStatus("0");
        when(mdProductMapper.selectMdProductList(any())).thenReturn(Collections.singletonList(prod));

        MdLocation loc = new MdLocation();
        loc.setLocationId(5L);
        loc.setWarehouseId(1L);
        loc.setLocationCode("A01");
        loc.setLocationName("A区01位");
        loc.setStatus("0");
        when(mdLocationMapper.selectMdLocationList(any())).thenReturn(Collections.singletonList(loc));
        when(wmsStockMapper.selectWmsStockByKey(any())).thenReturn(null);

        WmsStock row = new WmsStock();
        row.setWarehouseCode("WH01");
        row.setProductCode("SKU01");
        row.setLocationCode("A01");
        row.setQuantity(new BigDecimal("20"));
        row.setCostPrice(new BigDecimal("48.00"));

        String msg = wmsStockService.importWmsStock(Collections.singletonList(row), false, "admin");
        assertTrue(msg.contains("1 条"), msg);
        assertEquals(Long.valueOf(1L), row.getWarehouseId());
        assertEquals(Long.valueOf(10L), row.getProductId());
        assertEquals(Long.valueOf(5L), row.getLocationId());

        ArgumentCaptor<WmsStockLog> logCap = ArgumentCaptor.forClass(WmsStockLog.class);
        verify(wmsStockLogMapper).insertWmsStockLog(logCap.capture());
        assertEquals("stock_opening", logCap.getValue().getBillType());
        assertEquals(0, new BigDecimal("20").compareTo(logCap.getValue().getQuantity()));

        ArgumentCaptor<FinCostCalculate> costCap = ArgumentCaptor.forClass(FinCostCalculate.class);
        verify(finCostCalculateService).calculateCost(costCap.capture());
        assertEquals("stock_opening", costCap.getValue().getBillType());
        assertEquals(0, new BigDecimal("48.00").compareTo(costCap.getValue().getPrice()));
    }
}
