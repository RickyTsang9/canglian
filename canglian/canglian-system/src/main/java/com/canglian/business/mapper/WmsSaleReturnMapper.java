package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.WmsSaleReturn;

/**
 * 销售退货 数据层
 * 
 * @author canglian
 */
public interface WmsSaleReturnMapper
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
     * 按前缀查询最大退货单号
     * 
     * @param returnNoPrefix 退货单号前缀
     * @return 最大退货单号
     */
    public String selectMaxReturnNoByPrefix(String returnNoPrefix);
}

