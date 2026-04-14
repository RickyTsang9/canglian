package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.WmsSaleReturnItem;
import com.canglian.business.mapper.WmsSaleReturnItemMapper;
import com.canglian.business.service.IWmsSaleReturnItemService;

/**
 * 销售退货明细 服务层实现
 * 
 * @author canglian
 */
@Service
public class WmsSaleReturnItemServiceImpl implements IWmsSaleReturnItemService
{
    @Autowired
    private WmsSaleReturnItemMapper wmsSaleReturnItemMapper;

    /**
     * 查询销售退货明细信息
     * 
     * @param saleReturnItemId 销售退货明细id
     * @return 销售退货明细信息
     */
    @Override
    public WmsSaleReturnItem selectWmsSaleReturnItemById(Long saleReturnItemId)
    {
        return wmsSaleReturnItemMapper.selectWmsSaleReturnItemById(saleReturnItemId);
    }

    /**
     * 查询销售退货明细列表
     * 
     * @param wmsSaleReturnItem 销售退货明细信息
     * @return 销售退货明细集合
     */
    @Override
    public List<WmsSaleReturnItem> selectWmsSaleReturnItemList(WmsSaleReturnItem wmsSaleReturnItem)
    {
        return wmsSaleReturnItemMapper.selectWmsSaleReturnItemList(wmsSaleReturnItem);
    }

    /**
     * 新增销售退货明细
     * 
     * @param wmsSaleReturnItem 销售退货明细信息
     * @return 结果
     */
    @Override
    public int insertWmsSaleReturnItem(WmsSaleReturnItem wmsSaleReturnItem)
    {
        return wmsSaleReturnItemMapper.insertWmsSaleReturnItem(wmsSaleReturnItem);
    }

    /**
     * 修改销售退货明细
     * 
     * @param wmsSaleReturnItem 销售退货明细信息
     * @return 结果
     */
    @Override
    public int updateWmsSaleReturnItem(WmsSaleReturnItem wmsSaleReturnItem)
    {
        return wmsSaleReturnItemMapper.updateWmsSaleReturnItem(wmsSaleReturnItem);
    }

    /**
     * 删除销售退货明细
     * 
     * @param saleReturnItemId 销售退货明细id
     * @return 结果
     */
    @Override
    public int deleteWmsSaleReturnItemById(Long saleReturnItemId)
    {
        return wmsSaleReturnItemMapper.deleteWmsSaleReturnItemById(saleReturnItemId);
    }

    /**
     * 批量删除销售退货明细
     * 
     * @param saleReturnItemIds 需要删除的销售退货明细id
     * @return 结果
     */
    @Override
    public int deleteWmsSaleReturnItemByIds(Long[] saleReturnItemIds)
    {
        return wmsSaleReturnItemMapper.deleteWmsSaleReturnItemByIds(saleReturnItemIds);
    }
}

