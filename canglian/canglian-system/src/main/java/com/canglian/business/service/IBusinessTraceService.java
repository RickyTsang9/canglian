package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.BusinessTraceNode;

/**
 * 业务链路服务层
 *
 * @author canglian
 */
public interface IBusinessTraceService
{
    /**
     * 查询业务链路节点列表
     *
     * @param billType 单据类型
     * @param billId 单据id
     * @return 业务链路节点列表
     */
    public List<BusinessTraceNode> selectBusinessTraceNodeList(String billType, Long billId);
}
