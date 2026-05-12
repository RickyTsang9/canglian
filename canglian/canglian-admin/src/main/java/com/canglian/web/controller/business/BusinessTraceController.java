package com.canglian.web.controller.business;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.business.domain.BusinessTraceNode;
import com.canglian.business.service.IBusinessTraceService;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;

/**
 * 业务链路控制器
 *
 * @author canglian
 */
@RestController
@RequestMapping("/business/trace")
public class BusinessTraceController extends BaseController
{
    @Autowired
    private IBusinessTraceService businessTraceService;

    /**
     * 查询业务链路节点列表
     *
     * @param billType 单据类型
     * @param billId 单据id
     * @return 业务链路节点列表
     */
    @PreAuthorize("@ss.hasPermi('business:trace:query')")
    @GetMapping("/{billType}/{billId}")
    public AjaxResult list(@PathVariable String billType, @PathVariable Long billId)
    {
        List<BusinessTraceNode> traceNodeList = businessTraceService.selectBusinessTraceNodeList(billType, billId);
        return success(traceNodeList);
    }
}
