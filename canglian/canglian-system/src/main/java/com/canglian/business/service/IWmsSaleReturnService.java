package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.WmsSaleReturn;

/**
 * 销售退货 服务层
 * 
 * @author canglian
 */
public interface IWmsSaleReturnService
{
    /**
     * 查询销售退货信息
     * 
     * @param saleReturnId 销售退货id
     * @return 销售退货信息
     */
    public WmsSaleReturn selectWmsSaleReturnById(Long saleReturnId);

    /**
     * 查询销售退货列表
     * 
     * @param wmsSaleReturn 销售退货信息
     * @return 销售退货集合
     */
    public List<WmsSaleReturn> selectWmsSaleReturnList(WmsSaleReturn wmsSaleReturn);

    /**
     * 新增销售退货
     * 
     * @param wmsSaleReturn 销售退货信息
     * @return 结果
     */
    public int insertWmsSaleReturn(WmsSaleReturn wmsSaleReturn);

    /**
     * 修改销售退货
     * 
     * @param wmsSaleReturn 销售退货信息
     * @return 结果
     */
    public int updateWmsSaleReturn(WmsSaleReturn wmsSaleReturn);

    /**
     * 删除销售退货
     * 
     * @param saleReturnId 销售退货id
     * @return 结果
     */
    public int deleteWmsSaleReturnById(Long saleReturnId);

    /**
     * 批量删除销售退货
     * 
     * @param saleReturnIds 需要删除的销售退货id
     * @return 结果
     */
    public int deleteWmsSaleReturnByIds(Long[] saleReturnIds);

    /**
     * 销售退货审核
     * 
     * @param saleReturnId 销售退货id
     * @param operator 操作人
     * @return 结果
     */
    public int auditWmsSaleReturn(Long saleReturnId, String operator);
}

