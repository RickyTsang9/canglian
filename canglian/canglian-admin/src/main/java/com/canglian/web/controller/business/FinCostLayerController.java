package com.canglian.web.controller.business;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.business.domain.FinCostLayer;
import com.canglian.business.service.IFinCostLayerService;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.exception.ServiceException;

/**
 * 成本分层 信息操作处理（只读 FIFO 成本层账簿）
 *
 * @author canglian
 */
@RestController
@RequestMapping("/business/costLayer")
public class FinCostLayerController extends BaseController
{
    private static final String READONLY_MSG = "成本分层由成本核算引擎自动维护，禁止手工增删改；如需纠偏请使用「成本核算 -> 期末重算」";

    @Autowired
    private IFinCostLayerService finCostLayerService;

    @PreAuthorize("@ss.hasPermi('business:costLayer:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinCostLayer finCostLayer)
    {
        startPage();
        List<FinCostLayer> list = finCostLayerService.selectFinCostLayerList(finCostLayer);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('business:costLayer:query')")
    @GetMapping(value = "/{costLayerId}")
    public AjaxResult getInfo(@PathVariable Long costLayerId)
    {
        return success(finCostLayerService.selectFinCostLayerById(costLayerId));
    }

    @PreAuthorize("@ss.hasPermi('business:costLayer:add')")
    @PostMapping
    public AjaxResult add(@RequestBody(required = false) FinCostLayer finCostLayer)
    {
        throw new ServiceException(READONLY_MSG);
    }

    @PreAuthorize("@ss.hasPermi('business:costLayer:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody(required = false) FinCostLayer finCostLayer)
    {
        throw new ServiceException(READONLY_MSG);
    }

    @PreAuthorize("@ss.hasPermi('business:costLayer:remove')")
    @DeleteMapping("/{costLayerIds}")
    public AjaxResult remove(@PathVariable Long[] costLayerIds)
    {
        throw new ServiceException(READONLY_MSG);
    }
}
