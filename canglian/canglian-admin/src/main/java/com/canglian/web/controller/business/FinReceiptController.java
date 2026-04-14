package com.canglian.web.controller.business;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;
import com.canglian.business.domain.FinReceipt;
import com.canglian.business.service.IFinReceiptService;

/**
 * 收款单 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/receipt")
public class FinReceiptController extends BaseController
{
    @Autowired
    private IFinReceiptService finReceiptService;

    /**
     * 获取收款单列表
     */
    @PreAuthorize("@ss.hasPermi('business:receipt:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinReceipt finReceipt)
    {
        startPage();
        List<FinReceipt> list = finReceiptService.selectFinReceiptList(finReceipt);
        return getDataTable(list);
    }

    /**
     * 根据收款单编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:receipt:query')")
    @GetMapping(value = "/{receiptId}")
    public AjaxResult getInfo(@PathVariable Long receiptId)
    {
        return success(finReceiptService.selectFinReceiptById(receiptId));
    }

    /**
     * 新增收款单
     */
    @PreAuthorize("@ss.hasPermi('business:receipt:add')")
    @Log(title = "收款单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinReceipt finReceipt)
    {
        finReceipt.setCreateBy(getUsername());
        return toAjax(finReceiptService.insertFinReceipt(finReceipt));
    }

    /**
     * 修改收款单
     */
    @PreAuthorize("@ss.hasPermi('business:receipt:edit')")
    @Log(title = "收款单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinReceipt finReceipt)
    {
        finReceipt.setUpdateBy(getUsername());
        return toAjax(finReceiptService.updateFinReceipt(finReceipt));
    }

    /**
     * 删除收款单
     */
    @PreAuthorize("@ss.hasPermi('business:receipt:remove')")
    @Log(title = "收款单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{receiptIds}")
    public AjaxResult remove(@PathVariable Long[] receiptIds)
    {
        return toAjax(finReceiptService.deleteFinReceiptByIds(receiptIds));
    }
}

