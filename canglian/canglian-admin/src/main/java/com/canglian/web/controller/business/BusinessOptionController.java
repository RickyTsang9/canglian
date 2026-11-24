package com.canglian.web.controller.business;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.business.domain.FinExpenseCategory;
import com.canglian.business.domain.FinFundAccount;
import com.canglian.business.domain.MdCustomer;
import com.canglian.business.domain.MdLocation;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.MdSupplier;
import com.canglian.business.domain.MdWarehouse;
import com.canglian.business.domain.WmsInbound;
import com.canglian.business.domain.WmsInventoryCheck;
import com.canglian.business.domain.WmsOutbound;
import com.canglian.business.domain.WmsPurchaseReturn;
import com.canglian.business.domain.WmsSaleReturn;
import com.canglian.business.domain.WmsTransfer;
import com.canglian.business.mapper.FinExpenseCategoryMapper;
import com.canglian.business.mapper.FinFundAccountMapper;
import com.canglian.business.mapper.MdCustomerMapper;
import com.canglian.business.mapper.MdLocationMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.MdSupplierMapper;
import com.canglian.business.mapper.MdWarehouseMapper;
import com.canglian.business.mapper.WmsInboundMapper;
import com.canglian.business.mapper.WmsInventoryCheckMapper;
import com.canglian.business.mapper.WmsOutboundMapper;
import com.canglian.business.mapper.WmsPurchaseReturnMapper;
import com.canglian.business.mapper.WmsSaleReturnMapper;
import com.canglian.business.mapper.WmsTransferMapper;
import com.canglian.business.service.impl.BizScopeHelper;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;

/**
 * 轻量只读基础下拉选项控制器
 * 供各业务岗位下拉反显与开单选品使用，返回最小安全字段并严格遵循仓库数据隔离，消除跨模块 403 弹窗
 *
 * @author canglian
 */
@RestController
@RequestMapping("/business/options")
public class BusinessOptionController extends BaseController
{
    @Autowired
    private MdWarehouseMapper mdWarehouseMapper;

    @Autowired
    private MdLocationMapper mdLocationMapper;

    @Autowired
    private MdProductMapper mdProductMapper;

    @Autowired
    private MdCustomerMapper mdCustomerMapper;

    @Autowired
    private MdSupplierMapper mdSupplierMapper;

    @Autowired
    private FinFundAccountMapper finFundAccountMapper;

    @Autowired
    private FinExpenseCategoryMapper finExpenseCategoryMapper;

    @Autowired(required = false)
    private WmsInboundMapper wmsInboundMapper;

    @Autowired(required = false)
    private WmsOutboundMapper wmsOutboundMapper;

    @Autowired(required = false)
    private WmsTransferMapper wmsTransferMapper;

    @Autowired(required = false)
    private WmsInventoryCheckMapper wmsInventoryCheckMapper;

    @Autowired(required = false)
    private WmsPurchaseReturnMapper wmsPurchaseReturnMapper;

    @Autowired(required = false)
    private WmsSaleReturnMapper wmsSaleReturnMapper;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/warehouses")
    public AjaxResult warehouses()
    {
        List<MdWarehouse> list = mdWarehouseMapper.selectMdWarehouseList(new MdWarehouse());
        Set<Long> allowed = bizScopeHelper == null ? null : bizScopeHelper.getRestrictedWarehouseIds();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        if (list != null)
        {
            for (MdWarehouse w : list)
            {
                if (allowed != null && !allowed.contains(w.getWarehouseId()))
                {
                    continue;
                }
                Map<String, Object> m = new LinkedHashMap<String, Object>();
                m.put("warehouseId", w.getWarehouseId());
                m.put("warehouseCode", w.getWarehouseCode());
                m.put("warehouseName", w.getWarehouseName());
                m.put("status", w.getStatus());
                rows.add(m);
            }
        }
        return success(rows);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/locations")
    public AjaxResult locations(@RequestParam(required = false) Long warehouseId)
    {
        MdLocation q = new MdLocation();
        if (warehouseId != null)
        {
            q.setWarehouseId(warehouseId);
        }
        List<MdLocation> list = mdLocationMapper.selectMdLocationList(q);
        Set<Long> allowed = bizScopeHelper == null ? null : bizScopeHelper.getRestrictedWarehouseIds();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        if (list != null)
        {
            for (MdLocation l : list)
            {
                if (allowed != null && !allowed.contains(l.getWarehouseId()))
                {
                    continue;
                }
                Map<String, Object> m = new LinkedHashMap<String, Object>();
                m.put("locationId", l.getLocationId());
                m.put("warehouseId", l.getWarehouseId());
                m.put("locationCode", l.getLocationCode());
                m.put("locationName", l.getLocationName());
                m.put("status", l.getStatus());
                rows.add(m);
            }
        }
        return success(rows);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/products")
    public AjaxResult products()
    {
        List<MdProduct> list = mdProductMapper.selectMdProductList(new MdProduct());
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        if (list != null)
        {
            for (MdProduct p : list)
            {
                Map<String, Object> m = new LinkedHashMap<String, Object>();
                m.put("productId", p.getProductId());
                m.put("productCode", p.getProductCode());
                m.put("productName", p.getProductName());
                m.put("productSpec", p.getProductSpec());
                m.put("barCode", p.getBarCode());
                m.put("unitName", p.getUnitName());
                m.put("baseUnit", p.getBaseUnit());
                m.put("purchaseUnit", p.getPurchaseUnit());
                m.put("saleUnit", p.getSaleUnit());
                m.put("costPrice", p.getCostPrice());
                m.put("salePrice", p.getSalePrice());
                m.put("enableBatch", p.getEnableBatch());
                m.put("enableSerial", p.getEnableSerial());
                m.put("shelfLifeDays", p.getShelfLifeDays());
                m.put("status", p.getStatus());
                rows.add(m);
            }
        }
        return success(rows);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/customers")
    public AjaxResult customers()
    {
        List<MdCustomer> list = mdCustomerMapper.selectMdCustomerList(new MdCustomer());
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        if (list != null)
        {
            for (MdCustomer c : list)
            {
                Map<String, Object> m = new LinkedHashMap<String, Object>();
                m.put("customerId", c.getCustomerId());
                m.put("customerCode", c.getCustomerCode());
                m.put("customerName", c.getCustomerName());
                m.put("customerLevel", c.getCustomerLevel());
                m.put("pricePolicyType", c.getPricePolicyType());
                m.put("receivableDays", c.getReceivableDays());
                m.put("creditLimit", c.getCreditLimit());
                m.put("status", c.getStatus());
                rows.add(m);
            }
        }
        return success(rows);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/suppliers")
    public AjaxResult suppliers()
    {
        List<MdSupplier> list = mdSupplierMapper.selectMdSupplierList(new MdSupplier());
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        if (list != null)
        {
            for (MdSupplier s : list)
            {
                Map<String, Object> m = new LinkedHashMap<String, Object>();
                m.put("supplierId", s.getSupplierId());
                m.put("supplierCode", s.getSupplierCode());
                m.put("supplierName", s.getSupplierName());
                m.put("payableDays", s.getPayableDays());
                m.put("status", s.getStatus());
                rows.add(m);
            }
        }
        return success(rows);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/fundAccounts")
    public AjaxResult fundAccounts()
    {
        List<FinFundAccount> list = finFundAccountMapper.selectFinFundAccountList(new FinFundAccount());
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        if (list != null)
        {
            for (FinFundAccount a : list)
            {
                Map<String, Object> m = new LinkedHashMap<String, Object>();
                m.put("accountId", a.getAccountId());
                m.put("accountNo", a.getAccountNo());
                m.put("accountName", a.getAccountName());
                m.put("accountType", a.getAccountType());
                m.put("currency", a.getCurrency());
                m.put("balance", a.getBalance());
                m.put("status", a.getStatus());
                rows.add(m);
            }
        }
        return success(rows);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/expenseCategories")
    public AjaxResult expenseCategories()
    {
        List<FinExpenseCategory> list = finExpenseCategoryMapper.selectFinExpenseCategoryList(new FinExpenseCategory());
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        if (list != null)
        {
            for (FinExpenseCategory c : list)
            {
                Map<String, Object> m = new LinkedHashMap<String, Object>();
                m.put("categoryId", c.getCategoryId());
                m.put("categoryCode", c.getCategoryCode());
                m.put("categoryName", c.getCategoryName());
                m.put("status", c.getStatus());
                rows.add(m);
            }
        }
        return success(rows);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/billNos")
    public AjaxResult billNos()
    {
        Set<Long> allowed = bizScopeHelper == null ? null : bizScopeHelper.getRestrictedWarehouseIds();
        Map<String, Map<Long, String>> result = new LinkedHashMap<String, Map<Long, String>>();

        Map<Long, String> inbMap = new LinkedHashMap<Long, String>();
        if (wmsInboundMapper != null)
        {
            List<WmsInbound> list = wmsInboundMapper.selectWmsInboundList(new WmsInbound());
            if (list != null)
            {
                for (WmsInbound x : list)
                {
                    if (allowed == null || allowed.contains(x.getWarehouseId())) inbMap.put(x.getInboundId(), x.getInboundNo());
                }
            }
        }
        result.put("inbound", inbMap);

        Map<Long, String> outMap = new LinkedHashMap<Long, String>();
        if (wmsOutboundMapper != null)
        {
            List<WmsOutbound> list = wmsOutboundMapper.selectWmsOutboundList(new WmsOutbound());
            if (list != null)
            {
                for (WmsOutbound x : list)
                {
                    if (allowed == null || allowed.contains(x.getWarehouseId())) outMap.put(x.getOutboundId(), x.getOutboundNo());
                }
            }
        }
        result.put("outbound", outMap);

        Map<Long, String> trfMap = new LinkedHashMap<Long, String>();
        if (wmsTransferMapper != null)
        {
            List<WmsTransfer> list = wmsTransferMapper.selectWmsTransferList(new WmsTransfer());
            if (list != null)
            {
                for (WmsTransfer x : list)
                {
                    if (allowed == null || allowed.contains(x.getOutWarehouseId()) || allowed.contains(x.getInWarehouseId()))
                    {
                        trfMap.put(x.getTransferId(), x.getTransferNo());
                    }
                }
            }
        }
        result.put("transfer", trfMap);

        Map<Long, String> chkMap = new LinkedHashMap<Long, String>();
        if (wmsInventoryCheckMapper != null)
        {
            List<WmsInventoryCheck> list = wmsInventoryCheckMapper.selectWmsInventoryCheckList(new WmsInventoryCheck());
            if (list != null)
            {
                for (WmsInventoryCheck x : list)
                {
                    if (allowed == null || allowed.contains(x.getWarehouseId())) chkMap.put(x.getCheckId(), x.getCheckNo());
                }
            }
        }
        result.put("inventoryCheck", chkMap);

        Map<Long, String> prtMap = new LinkedHashMap<Long, String>();
        if (wmsPurchaseReturnMapper != null)
        {
            List<WmsPurchaseReturn> list = wmsPurchaseReturnMapper.selectWmsPurchaseReturnList(new WmsPurchaseReturn());
            if (list != null)
            {
                for (WmsPurchaseReturn x : list)
                {
                    if (allowed == null || allowed.contains(x.getWarehouseId())) prtMap.put(x.getPurchaseReturnId(), x.getReturnNo());
                }
            }
        }
        result.put("purchaseReturn", prtMap);

        Map<Long, String> srtMap = new LinkedHashMap<Long, String>();
        if (wmsSaleReturnMapper != null)
        {
            List<WmsSaleReturn> list = wmsSaleReturnMapper.selectWmsSaleReturnList(new WmsSaleReturn());
            if (list != null)
            {
                for (WmsSaleReturn x : list)
                {
                    if (allowed == null || allowed.contains(x.getWarehouseId())) srtMap.put(x.getSaleReturnId(), x.getReturnNo());
                }
            }
        }
        result.put("saleReturn", srtMap);

        return success(result);
    }
}
