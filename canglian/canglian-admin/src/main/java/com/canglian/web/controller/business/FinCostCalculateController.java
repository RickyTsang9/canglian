package com.canglian.web.controller.business;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.business.domain.FinCostCalculate;
import com.canglian.business.domain.FinCostLog;
import com.canglian.business.service.IFinCostCalculateService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.enums.BusinessType;

/**
 * 成本核算与重算控制层
 *
 * @author canglian
 */
@RestController
@RequestMapping("/business/costCalculate")
public class FinCostCalculateController extends BaseController
{
    @Autowired
    private IFinCostCalculateService finCostCalculateService;

    @PreAuthorize("@ss.hasPermi('business:costCalculate:query')")
    @GetMapping("/config")
    public AjaxResult getConfig()
    {
        return success(finCostCalculateService.getGlobalCostConfig());
    }

    @PreAuthorize("@ss.hasPermi('business:costCalculate:calculate')")
    @Log(title = "全局成本算法配置", businessType = BusinessType.UPDATE)
    @PutMapping("/config")
    public AjaxResult updateConfig(@RequestBody Map<String, String> payload)
    {
        String costMethod = payload == null ? null : payload.get("costMethod");
        return success(finCostCalculateService.updateGlobalCostMethod(costMethod, getUsername()));
    }

    /**
     * 纯内存沙箱模拟测算（只读，不落库不锁成本算法开关）
     */
    @PreAuthorize("@ss.hasPermi('business:costCalculate:query')")
    @PostMapping("/simulate")
    public AjaxResult simulate(@RequestBody FinCostCalculate finCostCalculate)
    {
        return success(finCostCalculateService.simulateCost(finCostCalculate));
    }

    @PreAuthorize("@ss.hasPermi('business:costCalculate:calculate')")
    @Log(title = "成本核算", businessType = BusinessType.INSERT)
    @PostMapping("/calculate")
    public AjaxResult calculate(@RequestBody FinCostCalculate finCostCalculate)
    {
        if (finCostCalculate != null && ("manual".equalsIgnoreCase(finCostCalculate.getBillType())
            || finCostCalculate.getBillId() == null || finCostCalculate.getBillId() <= 0L))
        {
            return success(finCostCalculateService.simulateCost(finCostCalculate));
        }
        FinCostLog finCostLog = finCostCalculateService.calculateCost(finCostCalculate);
        return success(finCostLog);
    }

    /**
     * 未结账期库存成本重算（按库存流水时间序重演成本账与成本层）
     */
    @PreAuthorize("@ss.hasPermi('business:costCalculate:recalculate')")
    @Log(title = "成本期末重算", businessType = BusinessType.UPDATE)
    @PostMapping("/recalculate")
    public AjaxResult recalculate(@RequestBody(required = false) Map<String, Object> payload)
    {
        Long warehouseId = parseLong(payload, "warehouseId");
        Long productId = parseLong(payload, "productId");
        return success(finCostCalculateService.recalculateCost(warehouseId, productId, getUsername()));
    }

    /**
     * 受控成本调价（记录调整流水与凭证事件）
     */
    @PreAuthorize("@ss.hasPermi('business:costCalculate:adjust')")
    @Log(title = "受控成本调价", businessType = BusinessType.UPDATE)
    @PostMapping("/adjust")
    public AjaxResult adjust(@RequestBody Map<String, Object> payload)
    {
        Long warehouseId = parseLong(payload, "warehouseId");
        Long productId = parseLong(payload, "productId");
        BigDecimal newAvgCost = (payload == null || payload.get("newAvgCost") == null || String.valueOf(payload.get("newAvgCost")).trim().isEmpty())
            ? null : new BigDecimal(String.valueOf(payload.get("newAvgCost")));
        String reason = payload == null || payload.get("reason") == null ? null : String.valueOf(payload.get("reason"));
        return success(finCostCalculateService.adjustCost(warehouseId, productId, newAvgCost, reason, getUsername()));
    }

    private Long parseLong(Map<String, Object> payload, String key)
    {
        if (payload == null || payload.get(key) == null)
        {
            return null;
        }
        String text = String.valueOf(payload.get(key)).trim();
        return text.isEmpty() ? null : Long.valueOf(text);
    }
}
