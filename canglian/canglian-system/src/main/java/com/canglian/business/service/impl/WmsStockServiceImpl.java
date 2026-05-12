package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IWmsStockService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

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
     * 导入期初库存
     *
     * @param stockList 库存集合
     * @param updateSupport 是否更新已存在数据
     * @param operator 操作人
     * @return 导入结果
     */
    @Override
    public String importWmsStock(List<WmsStock> stockList, Boolean updateSupport, String operator)
    {
        if (StringUtils.isNull(stockList) || stockList.isEmpty())
        {
            throw new ServiceException("导入库存数据不能为空");
        }
        int successNumber = 0;
        StringBuilder failureMessage = new StringBuilder();
        for (int stockIndex = 0; stockIndex < stockList.size(); stockIndex++)
        {
            WmsStock stock = stockList.get(stockIndex);
            try
            {
                validateImportStock(stock);
                fillDefaultStockValue(stock);
                WmsStock existingStock = wmsStockMapper.selectWmsStockByKey(stock);
                if (existingStock == null)
                {
                    stock.setCreateBy(operator);
                    insertWmsStock(stock);
                    successNumber++;
                }
                else if (Boolean.TRUE.equals(updateSupport))
                {
                    stock.setStockId(existingStock.getStockId());
                    stock.setUpdateBy(operator);
                    updateWmsStock(stock);
                    successNumber++;
                }
                else
                {
                    failureMessage.append("<br/>第").append(stockIndex + 1).append("行库存维度已存在");
                }
            }
            catch (Exception exception)
            {
                failureMessage.append("<br/>第").append(stockIndex + 1).append("行导入失败：").append(exception.getMessage());
            }
        }
        if (failureMessage.length() > 0)
        {
            return "库存导入完成，成功 " + successNumber + " 条，失败信息：" + failureMessage;
        }
        return "库存导入成功，共 " + successNumber + " 条";
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
     * 查询库存风险列表
     *
     * @param wmsStock 库存信息
     * @return 库存风险集合
     */
    @Override
    public List<WmsStock> selectWmsStockRiskList(WmsStock wmsStock)
    {
        if (wmsStock.getWarningDays() == null)
        {
            wmsStock.setWarningDays(90);
        }
        List<WmsStock> stockList = wmsStockMapper.selectWmsStockRiskList(wmsStock);
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

    /**
     * 校验导入库存
     *
     * @param stock 库存信息
     */
    private void validateImportStock(WmsStock stock)
    {
        if (stock.getWarehouseId() == null)
        {
            throw new ServiceException("仓库编号不能为空");
        }
        if (stock.getProductId() == null)
        {
            throw new ServiceException("商品编号不能为空");
        }
        if (stock.getQuantity() == null)
        {
            throw new ServiceException("库存数量不能为空");
        }
    }

    /**
     * 填充导入库存默认值
     *
     * @param stock 库存信息
     */
    private void fillDefaultStockValue(WmsStock stock)
    {
        if (stock.getLocationId() == null)
        {
            stock.setLocationId(0L);
        }
        if (StringUtils.isEmpty(stock.getBatchNo()))
        {
            stock.setBatchNo("");
        }
        if (stock.getLockedQuantity() == null)
        {
            stock.setLockedQuantity(BigDecimal.ZERO);
        }
        if (stock.getFrozenQuantity() == null)
        {
            stock.setFrozenQuantity(BigDecimal.ZERO);
        }
        if (stock.getWarningMinQty() == null)
        {
            stock.setWarningMinQty(BigDecimal.ZERO);
        }
        if (stock.getWarningMaxQty() == null)
        {
            stock.setWarningMaxQty(BigDecimal.ZERO);
        }
        if (stock.getVersion() == null)
        {
            stock.setVersion(0L);
        }
    }
}

