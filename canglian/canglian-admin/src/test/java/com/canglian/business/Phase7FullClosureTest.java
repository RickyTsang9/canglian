package com.canglian.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.canglian.business.domain.FinCostAccount;
import com.canglian.business.domain.FinInvoice;
import com.canglian.business.domain.FinVoucherEvent;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.MdProductUnit;
import com.canglian.business.domain.PriceRecentTransaction;
import com.canglian.business.domain.WmsSerialNo;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsStockLock;
import com.canglian.business.domain.WmsStockLog;
import com.canglian.business.mapper.FinCostAccountMapper;
import com.canglian.business.mapper.FinCostLayerMapper;
import com.canglian.business.mapper.FinCostLogMapper;
import com.canglian.business.mapper.FinGeneralLedgerMapper;
import com.canglian.business.mapper.FinInvoiceMapper;
import com.canglian.business.mapper.FinReceivableMapper;
import com.canglian.business.mapper.FinVoucherEntryMapper;
import com.canglian.business.mapper.FinVoucherEventMapper;
import com.canglian.business.mapper.MdCustomerMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.MdProductUnitMapper;
import com.canglian.business.mapper.PriceCustomerLevelMapper;
import com.canglian.business.mapper.PriceCustomerSpecialMapper;
import com.canglian.business.mapper.PriceRecentTransactionMapper;
import com.canglian.business.mapper.WmsSerialNoMapper;
import com.canglian.business.mapper.WmsStockLockMapper;
import com.canglian.business.mapper.WmsStockLogMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IFinPayableService;
import com.canglian.business.service.IFinReceivableService;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.business.service.impl.BizScopeHelper;
import com.canglian.business.service.impl.FinCostCalculateServiceImpl;
import com.canglian.business.service.impl.FinGeneralLedgerServiceImpl;
import com.canglian.business.service.impl.FinInvoiceServiceImpl;
import com.canglian.business.service.impl.FinVoucherEventServiceImpl;
import com.canglian.business.service.impl.PriceResolverServiceImpl;
import com.canglian.business.service.impl.WmsSerialNoServiceImpl;
import com.canglian.business.service.impl.WmsStockServiceImpl;
import com.canglian.common.core.domain.entity.SysRole;
import com.canglian.common.core.domain.entity.SysUser;
import com.canglian.common.core.domain.model.LoginUser;
import com.canglian.common.exception.ServiceException;
import com.canglian.system.mapper.SysRoleDeptMapper;
import com.canglian.system.mapper.SysRoleWarehouseMapper;
import com.canglian.system.service.ISysConfigService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Phase 7 全模块 58 项闭环核心回归单元测试
 */
@ExtendWith(MockitoExtension.class)
class Phase7FullClosureTest
{
    @Mock
    private FinInvoiceMapper finInvoiceMapper;

    @Mock
    private FinReceivableMapper finReceivableMapper;

    @Mock
    private IFinVoucherEventService finVoucherEventService;

    @Mock
    private IFinGeneralLedgerService finGeneralLedgerService;

    @Mock
    private IFinReceivableService finReceivableService;

    @Mock
    private IFinPayableService finPayableService;

    @InjectMocks
    private FinInvoiceServiceImpl finInvoiceService;

    @Mock
    private FinVoucherEventMapper finVoucherEventMapper;

    @Mock
    private FinVoucherEntryMapper finVoucherEntryMapper;

    @InjectMocks
    private FinVoucherEventServiceImpl voucherEventService;

    @Mock
    private MdProductMapper mdProductMapper;

    @Mock
    private MdCustomerMapper mdCustomerMapper;

    @Mock
    private MdProductUnitMapper mdProductUnitMapper;

    @Mock
    private PriceCustomerLevelMapper priceCustomerLevelMapper;

    @Mock
    private PriceCustomerSpecialMapper priceCustomerSpecialMapper;

    @Mock
    private PriceRecentTransactionMapper priceRecentTransactionMapper;

    @InjectMocks
    private PriceResolverServiceImpl priceResolverService;

    @Mock
    private FinCostAccountMapper finCostAccountMapper;

    @Mock
    private FinCostLayerMapper finCostLayerMapper;

    @Mock
    private FinCostLogMapper finCostLogMapper;

    @Mock
    private WmsStockLogMapper wmsStockLogMapper;

    @Mock
    private ISysConfigService sysConfigService;

    @InjectMocks
    private FinCostCalculateServiceImpl costCalculateService;

    @Mock
    private WmsSerialNoMapper wmsSerialNoMapper;

    @InjectMocks
    private WmsSerialNoServiceImpl serialNoService;

    @Mock
    private FinGeneralLedgerMapper finGeneralLedgerMapper;

    @InjectMocks
    private FinGeneralLedgerServiceImpl generalLedgerService;

    @Mock
    private WmsStockMapper wmsStockMapper;

    @Mock
    private WmsStockLockMapper wmsStockLockMapper;

    @InjectMocks
    private WmsStockServiceImpl stockService;

    @Mock
    private SysRoleWarehouseMapper sysRoleWarehouseMapper;

    @Mock
    private SysRoleDeptMapper sysRoleDeptMapper;

    @InjectMocks
    private BizScopeHelper bizScopeHelper;

    @Test
    void shouldCalculate13PercentVatAccuratelyAndEmitTaxVoucherOnInvoiceConfirm()
    {
        FinInvoice inv = new FinInvoice();
        inv.setInvoiceNo("FP20261122001");
        inv.setInvoiceType("sale");
        inv.setCustomerId(1L);
        inv.setInvoiceAmount(new BigDecimal("113.00"));
        inv.setTaxRate(new BigDecimal("13")); // 百分数输入 13 -> 应折算为 0.13
        inv.setInvoiceDate(new Date());

        when(finInvoiceMapper.insertFinInvoice(any(FinInvoice.class))).thenAnswer(a -> {
            FinInvoice arg = a.getArgument(0);
            arg.setInvoiceId(88L);
            return 1;
        });

        finInvoiceService.insertFinInvoice(inv);
        assertEquals(0, new BigDecimal("100.00").compareTo(inv.getUntaxedAmount()), "113元含税13%不含税额应为100.00");
        assertEquals(0, new BigDecimal("13.00").compareTo(inv.getTaxAmount()), "113元含税13%税额应为13.00");

        inv.setStatus("0");
        inv.setBizStatus("draft");
        when(finInvoiceMapper.selectFinInvoiceById(88L)).thenReturn(inv);
        when(finInvoiceMapper.updateFinInvoice(any(FinInvoice.class))).thenReturn(1);

        finInvoiceService.confirmFinInvoice(88L, "admin");
        verify(finVoucherEventService).recordVoucherEvent(
            eq("invoice"), eq(88L), eq("FP20261122001"), eq("sale_invoice_confirm"),
            any(Date.class), eq(new BigDecimal("13.00")), eq("admin"), any(String.class));
    }

    @Test
    void shouldCancelPendingVoucherEventsStrictlyScopedByBillId()
    {
        FinVoucherEvent target = new FinVoucherEvent();
        target.setVoucherEventId(10L);
        target.setBillType("outbound");
        target.setBillId(101L);
        target.setStatus("0");

        FinVoucherEvent sibling = new FinVoucherEvent();
        sibling.setVoucherEventId(11L);
        sibling.setBillType("outbound");
        sibling.setBillId(102L);
        sibling.setStatus("0");

        when(finVoucherEventMapper.selectFinVoucherEventList(any(FinVoucherEvent.class)))
            .thenReturn(Arrays.asList(target, sibling));

        voucherEventService.cancelPendingVoucherEvents("outbound", 101L);

        verify(finVoucherEntryMapper).deleteEntriesByEventId(10L);
        verify(finVoucherEventMapper).deleteFinVoucherEventById(10L);
        verify(finVoucherEventMapper, never()).deleteFinVoucherEventById(11L);
    }

    @Test
    void shouldNormalizeRecentSalePriceToBaseUnitAndScaleByTargetUnit()
    {
        MdProduct prod = new MdProduct();
        prod.setProductId(1L);
        prod.setUnitName("瓶");
        prod.setSalePrice(new BigDecimal("10.00"));
        when(mdProductMapper.selectMdProductById(1L)).thenReturn(prod);

        MdProductUnit boxUnit = new MdProductUnit();
        boxUnit.setProductId(1L);
        boxUnit.setUnitName("箱");
        boxUnit.setConvertRatio(new BigDecimal("12"));
        when(mdProductUnitMapper.selectByProductAndUnit(1L, "箱")).thenReturn(boxUnit);

        // 保存按箱(12瓶)卖120元的最近成交价，应折算为基本单位 10.00 元/瓶入库
        priceResolverService.saveRecentSalePrice(5L, 1L, new BigDecimal("120.00"), new BigDecimal("12"),
            "outbound", 999L, new Date(), "admin");

        ArgumentCaptor<PriceRecentTransaction> captor = ArgumentCaptor.forClass(PriceRecentTransaction.class);
        verify(priceRecentTransactionMapper).insertPriceRecentTransaction(captor.capture());
        assertEquals(0, new BigDecimal("10.00").compareTo(captor.getValue().getSalePrice()),
            "箱价120(换算率12)应按基础单位10元存储");

        // 反查该客户按箱报价时，应在 10.00 基础价上乘以 12 返回 120.00
        when(priceCustomerSpecialMapper.selectPriceCustomerSpecialList(any())).thenReturn(Collections.emptyList());
        when(priceRecentTransactionMapper.selectPriceRecentTransactionList(any()))
            .thenReturn(Collections.singletonList(captor.getValue()));
        Map<String, Object> detail = priceResolverService.resolveSalePriceDetail(5L, 1L, "箱");
        assertEquals(0, new BigDecimal("120.00").compareTo((BigDecimal) detail.get("price")));
    }

    @Test
    void shouldBlockRecalculateCostWhenClosedPeriodExistsAndSkipFreezeLogs()
    {
        Map<String, Object> closedPeriod = new HashMap<String, Object>();
        closedPeriod.put("periodCode", "2026-10");
        closedPeriod.put("closeStatus", "closed");
        when(finGeneralLedgerService.selectFiscalPeriodList("closed"))
            .thenReturn(Collections.singletonList(closedPeriod));

        assertThrows(ServiceException.class, () -> costCalculateService.recalculateCost(1L, 1L, "admin"));

        // 放开已结账期间后，重算应跳过 stock_freeze / stock_unfreeze 流水
        when(finGeneralLedgerService.selectFiscalPeriodList("closed")).thenReturn(Collections.emptyList());
        FinCostAccount acc = new FinCostAccount();
        acc.setCostAccountId(50L);
        acc.setWarehouseId(1L);
        acc.setProductId(1L);
        acc.setCostMethod("moving");
        acc.setTotalQty(BigDecimal.ZERO);
        acc.setTotalAmount(BigDecimal.ZERO);
        when(finCostAccountMapper.selectFinCostAccountByUnique(1L, 1L, "moving")).thenReturn(acc);
        when(finCostLogMapper.selectFinCostLogList(any())).thenReturn(Collections.emptyList());

        WmsStockLog inLog = new WmsStockLog();
        inLog.setWarehouseId(1L);
        inLog.setProductId(1L);
        inLog.setBillType("inbound");
        inLog.setBillId(1L);
        inLog.setInOut("1");
        inLog.setQuantity(new BigDecimal("10"));
        inLog.setPrice(new BigDecimal("50"));

        WmsStockLog freezeLog = new WmsStockLog();
        freezeLog.setWarehouseId(1L);
        freezeLog.setProductId(1L);
        freezeLog.setBillType("stock_freeze");
        freezeLog.setBillId(2L);
        freezeLog.setInOut("2");
        freezeLog.setQuantity(new BigDecimal("5"));
        freezeLog.setPrice(BigDecimal.ZERO);

        when(wmsStockLogMapper.selectWmsStockLogChronological(1L, 1L))
            .thenReturn(Arrays.asList(inLog, freezeLog));

        Map<String, Object> res = costCalculateService.recalculateCost(1L, 1L, "admin");
        assertEquals(1, res.get("replayedLogs"), "重算必须跳过 stock_freeze，仅重放 1 条业务入库流水");
        assertEquals(0, new BigDecimal("10").compareTo(acc.getTotalQty()));
    }

    @Test
    void shouldEnforceExactSerialCountAndUniquenessWhenEnableSerialIsOne()
    {
        MdProduct serialProd = new MdProduct();
        serialProd.setProductId(20L);
        serialProd.setProductName("序列号管控主机");
        serialProd.setEnableSerial("1");

        // 缺少 SN 必须拦截
        assertThrows(ServiceException.class, () -> serialNoService.handleInboundSerials(
            serialProd, 1L, 0L, "B01", 10L, "RK01", null, "", new BigDecimal("2"), "admin"));

        // 数量不匹配（需要2个，只录1个）必须拦截
        assertThrows(ServiceException.class, () -> serialNoService.handleInboundSerials(
            serialProd, 1L, 0L, "B01", 10L, "RK01", null, "SN-001", new BigDecimal("2"), "admin"));

        // 重复 SN 必须拦截
        assertThrows(ServiceException.class, () -> serialNoService.handleInboundSerials(
            serialProd, 1L, 0L, "B01", 10L, "RK01", null, "SN-001,SN-001", new BigDecimal("2"), "admin"));

        // 恰好 2 个唯一 SN 应放行建档
        when(wmsSerialNoMapper.selectWmsSerialNoByCode("SN-001")).thenReturn(null);
        when(wmsSerialNoMapper.selectWmsSerialNoByCode("SN-002")).thenReturn(null);
        serialNoService.handleInboundSerials(
            serialProd, 1L, 0L, "B01", 10L, "RK01", null, "SN-001, SN-002", new BigDecimal("2"), "admin");
        verify(wmsSerialNoMapper, times(2)).insertWmsSerialNo(any(WmsSerialNo.class));
    }

    @Test
    void shouldBlockFiscalPeriodCloseWhenDraftDocsOrUnwrittenVouchersExist()
    {
        Map<String, Object> period = new HashMap<String, Object>();
        period.put("periodId", 9L);
        period.put("periodCode", "2026-11");
        period.put("startDate", new Date());
        period.put("endDate", new Date());
        period.put("closeStatus", "open");
        when(finGeneralLedgerMapper.selectFiscalPeriodById(9L)).thenReturn(period);
        when(finGeneralLedgerMapper.countEarlierOpenPeriods("2026-11")).thenReturn(0);
        when(finGeneralLedgerMapper.countUnwrittenBackVoucherInPeriod(any(Date.class), any(Date.class))).thenReturn(1);

        ServiceException ex = assertThrows(ServiceException.class,
            () -> generalLedgerService.closeFiscalPeriod(9L, "admin"));
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().contains("阻塞事项") && ex.getMessage().contains("禁止结账"));
        verify(finGeneralLedgerMapper, never()).updateFiscalPeriodCloseStatus(any(), any(), any());
    }

    @Test
    void shouldReleaseOnlyOwnOrderStockLockWithoutStealingSiblingOrderLock()
    {
        WmsStock stock = new WmsStock();
        stock.setStockId(5L);
        stock.setWarehouseId(1L);
        stock.setProductId(10L);
        stock.setLocationId(0L);
        stock.setBatchNo("B01");
        stock.setQuantity(new BigDecimal("100"));
        stock.setLockedQuantity(new BigDecimal("30")); // 订单100锁10 + 订单200锁20

        WmsStockLock order100Lock = new WmsStockLock();
        order100Lock.setLockId(1L);
        order100Lock.setStockId(5L);
        order100Lock.setOrderId(100L);
        order100Lock.setWarehouseId(1L);
        order100Lock.setProductId(10L);
        order100Lock.setLocationId(0L);
        order100Lock.setBatchNo("B01");
        order100Lock.setLockedQty(new BigDecimal("10"));
        order100Lock.setReleasedQty(BigDecimal.ZERO);

        when(wmsStockLockMapper.selectActiveLocksByOrderAndProductForUpdate(100L, 10L))
            .thenReturn(Collections.singletonList(order100Lock));
        when(wmsStockMapper.selectWmsStockByIdForUpdate(5L)).thenReturn(stock);

        // 订单100试图释放25件（超过自身锁定的10件），最多只能释放自身10件，绝不能误释放订单200的20件锁
        stockService.releaseStockLockForSaleOrder(1L, 10L, 0L, "B01", new BigDecimal("25"), 100L, "admin");

        assertEquals(0, new BigDecimal("20").compareTo(stock.getLockedQuantity()),
            "只应释放订单100的10件锁，保留订单200的20件锁");
        verify(wmsStockLockMapper).updateWmsStockLock(any(WmsStockLock.class));
        assertEquals(0, BigDecimal.ZERO.compareTo(order100Lock.getRemainingLockedQty()));
    }

    @Test
    void shouldEnforceWarehouseAndCustomDeptDataScopeIsolation()
    {
        SysRole whRole = new SysRole();
        whRole.setRoleId(20L);
        whRole.setStatus("0");
        whRole.setDataScope("2");
        whRole.setWarehouseScope("2");

        SysUser user = new SysUser();
        user.setUserId(99L);
        user.setDeptId(101L);
        user.setRoles(Collections.singletonList(whRole));

        LoginUser loginUser = new LoginUser(99L, 101L, user, Collections.emptySet());
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList()));
        try
        {
            when(sysRoleWarehouseMapper.selectAuthorizedWarehouseIdsByUserId(99L))
                .thenReturn(Collections.singletonList(1L));
            when(sysRoleDeptMapper.selectDeptIdsByRoleId(20L))
                .thenReturn(Collections.singletonList(101L));

            // 允许仓库1且部门101
            bizScopeHelper.assertWarehouseAccess(1L);
            bizScopeHelper.assertRecordAccess(101L, 88L, 1L);

            // 禁止越权访问未授权仓库2或未授权部门102
            assertThrows(ServiceException.class, () -> bizScopeHelper.assertWarehouseAccess(2L));
            assertThrows(ServiceException.class, () -> bizScopeHelper.assertRecordAccess(102L, 88L, 1L));

            WmsStock query = new WmsStock();
            bizScopeHelper.applyScopeFilter(query, "s", true, true);
            String whSql = String.valueOf(query.getParams().get("warehouseScope"));
            String dsSql = String.valueOf(query.getParams().get("dataScope"));
            assertTrue(whSql.contains("s.warehouse_id IN (1)"), "应注入仓库隔离 SQL: " + whSql);
            assertTrue(dsSql.contains("s.dept_id"), "应注入部门数据权限 SQL: " + dsSql);
        }
        finally
        {
            SecurityContextHolder.clearContext();
        }
    }
}
