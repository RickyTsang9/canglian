package com.canglian.business.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.canglian.business.domain.BusinessTraceNode;

/**
 * 业务链路数据层
 *
 * @author canglian
 */
public interface BusinessTraceMapper
{
    /**
     * 查询业务链路根节点
     *
     * @param billType 单据类型
     * @param billId 单据id
     * @return 业务链路根节点
     */
    public BusinessTraceNode selectBusinessTraceRootNode(@Param("billType") String billType, @Param("billId") Long billId);

    /**
     * 查询业务链路节点列表
     *
     * @param rootBillType 根单据类型
     * @param rootBillId 根单据id
     * @param rootBillNo 根单据号
     * @return 业务链路节点列表
     */
    public List<BusinessTraceNode> selectBusinessTraceNodeList(@Param("rootBillType") String rootBillType,
        @Param("rootBillId") Long rootBillId, @Param("rootBillNo") String rootBillNo);
}
