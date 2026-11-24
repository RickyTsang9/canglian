package com.canglian.web.controller.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.canglian.common.utils.poi.ExcelUtil;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.business.domain.WmsSerialNo;
import com.canglian.business.service.IWmsSerialNoService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;

/**
 * 商品序列号(SN)全生命周期追踪控制器
 *
 * @author canglian
 */
@RestController
@RequestMapping("/business/serialNo")
public class WmsSerialNoController extends BaseController
{
    @Autowired
    private IWmsSerialNoService wmsSerialNoService;

    @PreAuthorize("@ss.hasPermi('business:serialNo:list')")
    @GetMapping("/list")
    public TableDataInfo list(WmsSerialNo query)
    {
        startPage();
        List<WmsSerialNo> list = wmsSerialNoService.selectWmsSerialNoList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('business:serialNo:list')")
    @GetMapping("/trace/{snCode}")
    public AjaxResult trace(@PathVariable String snCode)
    {
        return success(wmsSerialNoService.traceBySnCode(snCode));
    }

    @PreAuthorize("@ss.hasPermi('business:serialNo:edit')")
    @Log(title = "序列号批量登记", businessType = BusinessType.INSERT)
    @PostMapping("/batchRegister")
    public AjaxResult batchRegister(@RequestBody Map<String, Object> body)
    {
        Long productId = toLong(body.get("productId"));
        Long warehouseId = toLong(body.get("warehouseId"));
        Long locationId = toLong(body.get("locationId"));
        String batchNo = toStr(body.get("batchNo"));
        Long inboundId = toLong(body.get("inboundId"));
        String billNo = toStr(body.get("billNo"));
        Date warrantyEndDate = toDate(body.get("warrantyEndDate"));
        List<String> snCodes = toStringList(body.get("snCodes"));
        String remark = toStr(body.get("remark"));
        return success(wmsSerialNoService.batchRegister(productId, warehouseId, locationId, batchNo, inboundId, billNo, warrantyEndDate, snCodes, remark, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:serialNo:edit')")
    @Log(title = "序列号出库核销", businessType = BusinessType.UPDATE)
    @PostMapping("/consume")
    public AjaxResult consume(@RequestBody Map<String, Object> body)
    {
        List<String> snCodes = toStringList(body.get("snCodes"));
        Long outboundId = toLong(body.get("outboundId"));
        String billNo = toStr(body.get("billNo"));
        Long customerId = toLong(body.get("customerId"));
        String remark = toStr(body.get("remark"));
        return toAjax(wmsSerialNoService.consumeSerialNos(snCodes, outboundId, billNo, customerId, remark, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:serialNo:edit')")
    @Log(title = "序列号退货回库", businessType = BusinessType.UPDATE)
    @PostMapping("/returnBack")
    public AjaxResult returnBack(@RequestBody Map<String, Object> body)
    {
        List<String> snCodes = toStringList(body.get("snCodes"));
        Long warehouseId = toLong(body.get("warehouseId"));
        Long locationId = toLong(body.get("locationId"));
        Long saleReturnId = toLong(body.get("saleReturnId"));
        String billNo = toStr(body.get("billNo"));
        String remark = toStr(body.get("remark"));
        return toAjax(wmsSerialNoService.returnSerialNos(snCodes, warehouseId, locationId, saleReturnId, billNo, remark, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:serialNo:edit')")
    @Log(title = "序列号删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/{serialIds}")
    public AjaxResult remove(@PathVariable Long[] serialIds)
    {
        return toAjax(wmsSerialNoService.deleteWmsSerialNoByIds(serialIds));
    }

    private Long toLong(Object v)
    {
        if (v == null || "".equals(String.valueOf(v).trim())) return null;
        return Long.valueOf(String.valueOf(v).trim());
    }

    private String toStr(Object v)
    {
        return v == null ? null : String.valueOf(v);
    }

    private Date toDate(Object v)
    {
        if (v == null || "".equals(String.valueOf(v).trim())) return null;
        try
        {
            return new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(v).trim());
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> toStringList(Object v)
    {
        List<String> list = new ArrayList<String>();
        if (v instanceof List)
        {
            for (Object o : (List<Object>) v)
            {
                if (o != null) list.add(String.valueOf(o));
            }
        }
        else if (v != null)
        {
            list.add(String.valueOf(v));
        }
        return list;
    }

    /**
     * 导出序列号台账
     */
    @Log(title = "序列号台账", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('business:serialNo:export') or @ss.hasPermi('business:serialNo:list')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, WmsSerialNo query)
    {
        List<WmsSerialNo> list = wmsSerialNoService.selectWmsSerialNoList(query);
        ExcelUtil<WmsSerialNo> util = new ExcelUtil<WmsSerialNo>(WmsSerialNo.class);
        util.exportExcel(response, list, "序列号台账");
    }
}
