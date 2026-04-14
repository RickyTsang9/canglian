package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IWmsStockService;

/**
 * 库存 服务层实现
 * 
 * @author canglian
 */
@Service
public class WmsStockServiceImpl implements IWmsStockService
{
    @Autowired
    private WmsStockMapper wmsStockMapper;

    /**
     * 查询库存信息
     * 
     * @param stockId 库存id
     * @return 库存信息
     */
    @Override
    public WmsStock selectWmsStockById(Long stockId)
    {
        WmsStock stock = wmsStockMapper.selectWmsStockById(stockId);
        fillAvailableQuantity(stock);
        return stock;
    }

    /**
     * 查询库存列表
     * 
     * @param wmsStock 库存信息
     * @return 库存集合
     */
    @Override
    public List<WmsStock> selectWmsStockList(WmsStock wmsStock)
    {
        List<WmsStock> stockList = wmsStockMapper.selectWmsStockList(wmsStock);
        for (WmsStock stock : stockList)
        {
            fillAvailableQuantity(stock);
        }
        return stockList;
    }

    /**
     * 查询库存预警列表
     * 
     * @param wmsStock 库存信息
     * @return 预警库存集合
     */
    @Override
    public List<WmsStock> selectWmsStockWarningList(WmsStock wmsStock)
    {
        List<WmsStock> stockList = wmsStockMapper.selectWmsStockWarningList(wmsStock);
        for (WmsStock stock : stockList)
        {
            fillAvailableQuantity(stock);
        }
        return stockList;
    }

    /**
     * 新增库存
     * 
     * @param wmsStock 库存信息
     * @return 结果
     */
    @Override
    public int insertWmsStock(WmsStock wmsStock)
    {
        return wmsStockMapper.insertWmsStock(wmsStock);
    }

    /**
     * 修改库存
     * 
     * @param wmsStock 库存信息
     * @return 结果
     */
    @Override
    public int updateWmsStock(WmsStock wmsStock)
    {
        return wmsStockMapper.updateWmsStock(wmsStock);
    }

    /**
     * 删除库存
     * 
     * @param stockId 库存id
     * @return 结果
     */
    @Override
    public int deleteWmsStockById(Long stockId)
    {
        return wmsStockMapper.deleteWmsStockById(stockId);
    }

    /**
     * 批量删除库存
     * 
     * @param stockIds 需要删除的库存id
     * @return 结果
     */
    @Override
    public int deleteWmsStockByIds(Long[] stockIds)
    {
        return wmsStockMapper.deleteWmsStockByIds(stockIds);
    }

    /**
     * 填充可用库存数量
     * 
     * @param stock 库存信息
     */
    private void fillAvailableQuantity(WmsStock stock)
    {
        if (stock == null)
        {
            return;
        }
        stock.setAvailableQuantity(calculateAvailableQuantity(stock));
    }

    /**
     * 计算可用库存数量
     * 
     * @param stock 库存信息
     * @return 可用库存数量
     */
    private BigDecimal calculateAvailableQuantity(WmsStock stock)
    {
        BigDecimal quantity = defaultBigDecimal(stock.getQuantity());
        BigDecimal lockedQuantity = defaultBigDecimal(stock.getLockedQuantity());
        BigDecimal frozenQuantity = defaultBigDecimal(stock.getFrozenQuantity());
        return quantity.subtract(lockedQuantity).subtract(frozenQuantity);
    }

    /**
     * 空值转换
     * 
     * @param value 数值
     * @return 转换后的数值
     */
    private BigDecimal defaultBigDecimal(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO : value;
    }
}

