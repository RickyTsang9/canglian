package com.canglian.web.controller.business;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.canglian.business.domain.MdCustomer;
import com.canglian.business.domain.MdLocation;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.MdSupplier;
import com.canglian.business.domain.MdWarehouse;
import com.canglian.business.mapper.MdCustomerMapper;
import com.canglian.business.mapper.MdLocationMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.MdSupplierMapper;
import com.canglian.business.mapper.MdWarehouseMapper;

/**
 * 业务单据打印数据名称补全器：服务端直接解析往来单位/仓库/商品/库位展示名称，避免前端依赖有限下拉缓存
 *
 * @author canglian
 */
@Component
public class PrintPayloadEnricher
{
    @Autowired(required = false)
    private MdCustomerMapper mdCustomerMapper;

    @Autowired(required = false)
    private MdSupplierMapper mdSupplierMapper;

    @Autowired(required = false)
    private MdWarehouseMapper mdWarehouseMapper;

    @Autowired(required = false)
    private MdProductMapper mdProductMapper;

    @Autowired(required = false)
    private MdLocationMapper mdLocationMapper;

    public String resolveCustomerName(Long customerId)
    {
        if (customerId == null || mdCustomerMapper == null)
        {
            return "";
        }
        MdCustomer c = mdCustomerMapper.selectMdCustomerById(customerId);
        return c != null ? c.getCustomerName() : ("客户#" + customerId);
    }

    public String resolveSupplierName(Long supplierId)
    {
        if (supplierId == null || mdSupplierMapper == null)
        {
            return "";
        }
        MdSupplier s = mdSupplierMapper.selectMdSupplierById(supplierId);
        return s != null ? s.getSupplierName() : ("供应商#" + supplierId);
    }

    public String resolveWarehouseName(Long warehouseId)
    {
        if (warehouseId == null || mdWarehouseMapper == null)
        {
            return "";
        }
        MdWarehouse w = mdWarehouseMapper.selectMdWarehouseById(warehouseId);
        return w != null ? w.getWarehouseName() : ("仓库#" + warehouseId);
    }

    public Map<String, String> resolveProductNames(Collection<Long> productIds)
    {
        Map<String, String> names = new LinkedHashMap<String, String>();
        if (productIds == null || mdProductMapper == null)
        {
            return names;
        }
        for (Long pid : productIds)
        {
            if (pid == null || names.containsKey(String.valueOf(pid)))
            {
                continue;
            }
            MdProduct p = mdProductMapper.selectMdProductById(pid);
            names.put(String.valueOf(pid), p != null ? p.getProductName() : ("商品#" + pid));
        }
        return names;
    }

    public Map<String, String> resolveLocationNames(Collection<Long> locationIds)
    {
        Map<String, String> names = new LinkedHashMap<String, String>();
        if (locationIds == null || mdLocationMapper == null)
        {
            return names;
        }
        for (Long lid : locationIds)
        {
            if (lid == null || names.containsKey(String.valueOf(lid)))
            {
                continue;
            }
            if (lid.longValue() == 0L)
            {
                names.put("0", "默认库位");
                continue;
            }
            MdLocation loc = mdLocationMapper.selectMdLocationByLocationId(lid);
            names.put(String.valueOf(lid), loc != null ? loc.getLocationName() : ("库位#" + lid));
        }
        return names;
    }

    public Map<String, Object> buildDisplayNames(String counterpartyName, String warehouseName,
        String outWarehouseName, String inWarehouseName,
        Collection<Long> productIds, Collection<Long> locationIds)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        if (counterpartyName != null)
        {
            map.put("counterpartyName", counterpartyName);
        }
        if (warehouseName != null)
        {
            map.put("warehouseName", warehouseName);
        }
        if (outWarehouseName != null)
        {
            map.put("outWarehouseName", outWarehouseName);
        }
        if (inWarehouseName != null)
        {
            map.put("inWarehouseName", inWarehouseName);
        }
        map.put("productNames", resolveProductNames(productIds));
        map.put("locationNames", resolveLocationNames(locationIds));
        return map;
    }
}
