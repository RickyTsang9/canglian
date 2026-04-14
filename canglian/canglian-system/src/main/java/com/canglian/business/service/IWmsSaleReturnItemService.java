package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.WmsSaleReturnItem;

/**
 * 销售退货明细 服务层
 * 
 * @author canglian
 */
public interface IWmsSaleReturnItemService
{
    /**
     * 查询销售退货明细信息
     * 
     * @param saleReturnItemId 销售退货明细id
     * @return 销售退货明细信息
     */
    public WmsSaleReturnItem selectWmsSaleReturnItemById(Long saleReturnItemId);

    /**
     * 查询销售退货明细列表
     * 
     * @param wmsSaleReturnItem 销售退货明细信息
     * @return 销售退货明细集合
     */
    public List<WmsSaleReturnItem> selectWmsSaleReturnItemList(WmsSaleReturnItem wmsSaleReturnItem);

    /**
     * 新增销售退货明细
     * 
     * @param wmsSaleReturnItem 销售退货明细信息
     * @return 结果
     */
    public int insertWmsSaleReturnItem(WmsSaleReturnItem wmsSaleReturnItem);

    /**
     * 修改销售退货明细
     * 
     * @param wmsSaleReturnItem 销售退货明细信息
     * @return 结果
     */
    public int updateWmsSaleReturnItem(WmsSaleReturnItem wmsSaleReturnItem);

    /**
     * 删除销售退货明细
     * 
     * @param saleReturnItemId 销售退货明细id
     * @return 结果
     */
    public int deleteWmsSaleReturnItemById(Long saleReturnItemId);

    /**
     * 批量删除销售退货明细
     * 
     * @param saleReturnItemIds 需要删除的销售退货明细id
     * @return 结果
     */
    public int deleteWmsSaleReturnItemByIds(Long[] saleReturnItemIds);
}

