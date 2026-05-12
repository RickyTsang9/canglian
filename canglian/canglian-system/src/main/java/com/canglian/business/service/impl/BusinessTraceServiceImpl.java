package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.BusinessTraceNode;
import com.canglian.business.mapper.BusinessTraceMapper;
import com.canglian.business.service.IBusinessTraceService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 业务链路服务实现
 *
 * @author canglian
 */
@Service
public class BusinessTraceServiceImpl implements IBusinessTraceService
{
    @Autowired
    private BusinessTraceMapper businessTraceMapper;

    /**
     * 查询业务链路节点列表
     *
     * @param billType 单据类型
     * @param billId 单据id
     * @return 业务链路节点列表
     */
    @Override
    public List<BusinessTraceNode> selectBusinessTraceNodeList(String billType, Long billId)
    {
        if (StringUtils.isEmpty(billType) || billId == null)
        {
            throw new ServiceException("链路查询单据参数不能为空");
        }
        BusinessTraceNode rootNode = businessTraceMapper.selectBusinessTraceRootNode(billType, billId);
        if (rootNode == null)
        {
            throw new ServiceException("未找到链路查询根单据");
        }
        return businessTraceMapper.selectBusinessTraceNodeList(rootNode.getBillType(), rootNode.getBillId(), rootNode.getBillNo());
    }
}
