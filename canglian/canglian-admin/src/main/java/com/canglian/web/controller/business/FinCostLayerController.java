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
import com.canglian.business.domain.FinCostLayer;
import com.canglian.business.service.IFinCostLayerService;

/**
 * 成本分层 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/costLayer")
public class FinCostLayerController extends BaseController
{
    @Autowired
    private IFinCostLayerService finCostLayerService;

    /**
     * 获取成本分层列表
     */
    @PreAuthorize("@ss.hasPermi('business:costLayer:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinCostLayer finCostLayer)
    {
        startPage();
        List<FinCostLayer> list = finCostLayerService.selectFinCostLayerList(finCostLayer);
        return getDataTable(list);
    }

    /**
     * 根据成本分层编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:costLayer:query')")
    @GetMapping(value = "/{costLayerId}")
    public AjaxResult getInfo(@PathVariable Long costLayerId)
    {
        return success(finCostLayerService.selectFinCostLayerById(costLayerId));
    }

    /**
     * 新增成本分层
     */
    @PreAuthorize("@ss.hasPermi('business:costLayer:add')")
    @Log(title = "成本分层", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinCostLayer finCostLayer)
    {
        finCostLayer.setCreateBy(getUsername());
        return toAjax(finCostLayerService.insertFinCostLayer(finCostLayer));
    }

    /**
     * 修改成本分层
     */
    @PreAuthorize("@ss.hasPermi('business:costLayer:edit')")
    @Log(title = "成本分层", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinCostLayer finCostLayer)
    {
        return toAjax(finCostLayerService.updateFinCostLayer(finCostLayer));
    }

    /**
     * 删除成本分层
     */
    @PreAuthorize("@ss.hasPermi('business:costLayer:remove')")
    @Log(title = "成本分层", businessType = BusinessType.DELETE)
    @DeleteMapping("/{costLayerIds}")
    public AjaxResult remove(@PathVariable Long[] costLayerIds)
    {
        return toAjax(finCostLayerService.deleteFinCostLayerByIds(costLayerIds));
    }
}

