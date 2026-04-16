package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.common.exception.ServiceException;
import com.canglian.business.domain.WmsInventoryCheckItem;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsStockLog;
import com.canglian.business.mapper.WmsInventoryCheckMapper;
import com.canglian.business.mapper.WmsInventoryCheckItemMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.mapper.WmsStockLogMapper;
import com.canglian.business.domain.WmsInventoryCheck;
import com.canglian.business.service.IWmsInventoryCheckService;

/**
 * 盘点单 服务层实现
 * 
 * @author canglian
 */
@Service
public class WmsInventoryCheckServiceImpl implements IWmsInventoryCheckService
{
    @Autowired
    private WmsInventoryCheckMapper wmsInventoryCheckMapper;

    @Autowired
    private WmsInventoryCheckItemMapper wmsInventoryCheckItemMapper;

    @Autowired
    private WmsStockMapper wmsStockMapper;

    @Autowired
    private WmsStockLogMapper wmsStockLogMapper;

    /**
     * 查询盘点单信息
     * 
     * @param checkId 盘点单id
     * @return 盘点单信息
     */
    @Override
    public WmsInventoryCheck selectWmsInventoryCheckById(Long checkId)
    {
        return wmsInventoryCheckMapper.selectWmsInventoryCheckById(checkId);
    }

    /**
     * 查询盘点单列表
     * 
     * @param wmsInventoryCheck 盘点单信息
     * @return 盘点单集合
     */
    @Override
    public List<WmsInventoryCheck> selectWmsInventoryCheckList(WmsInventoryCheck wmsInventoryCheck)
    {
        return wmsInventoryCheckMapper.selectWmsInventoryCheckList(wmsInventoryCheck);
    }

    /**
     * 新增盘点单
     * 
     * @param wmsInventoryCheck 盘点单信息
     * @return 结果
     */
    @Override
    public int insertWmsInventoryCheck(WmsInventoryCheck wmsInventoryCheck)
    {
        wmsInventoryCheck.setStatus("0");
        return wmsInventoryCheckMapper.insertWmsInventoryCheck(wmsInventoryCheck);
    }

    /**
     * 修改盘点单
     * 
     * @param wmsInventoryCheck 盘点单信息
     * @return 结果
     */
    @Override
    public int updateWmsInventoryCheck(WmsInventoryCheck wmsInventoryCheck)
    {
        WmsInventoryCheck inventoryCheck = getExistingInventoryCheck(wmsInventoryCheck.getCheckId());
        validateInventoryCheckEditable(inventoryCheck);
        wmsInventoryCheck.setStatus(inventoryCheck.getStatus());
        return wmsInventoryCheckMapper.updateWmsInventoryCheck(wmsInventoryCheck);
    }

    /**
     * 删除盘点单信息
     * 
     * @param checkId 盘点单id
     * @return 结果
     */
    @Override
    public int deleteWmsInventoryCheckById(Long checkId)
    {
        checkInventoryCheckDeletable(checkId);
        return wmsInventoryCheckMapper.deleteWmsInventoryCheckById(checkId);
    }

    /**
     * 批量删除盘点单
     * 
     * @param checkIds 需要删除的盘点单id
     * @return 结果
     */
    @Override
    public int deleteWmsInventoryCheckByIds(Long[] checkIds)
    {
        for (Long checkId : checkIds)
        {
            checkInventoryCheckDeletable(checkId);
        }
        return wmsInventoryCheckMapper.deleteWmsInventoryCheckByIds(checkIds);
    }

    /**
     * 盘点单审核
     *
     * @param checkId 盘点单id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    @Transactional
    public int auditWmsInventoryCheck(Long checkId, String operator)
    {
        WmsInventoryCheck inventoryCheck = getExistingInventoryCheck(checkId);
        if (!"0".equals(inventoryCheck.getStatus()))
        {
            throw new ServiceException("盘点单状态已变更，无法审核");
        }
        if (inventoryCheck.getWarehouseId() == null)
        {
            throw new ServiceException("盘点仓库不能为空");
        }
        WmsInventoryCheckItem inventoryCheckItemQuery = new WmsInventoryCheckItem();
        inventoryCheckItemQuery.setCheckId(checkId);
        List<WmsInventoryCheckItem> inventoryCheckItemList = wmsInventoryCheckItemMapper.selectWmsInventoryCheckItemList(inventoryCheckItemQuery);
        if (inventoryCheckItemList == null || inventoryCheckItemList.isEmpty())
        {
            throw new ServiceException("盘点单明细不能为空");
        }
        BigDecimal totalDifferenceQuantity = BigDecimal.ZERO;
        BigDecimal totalDifferenceAmount = BigDecimal.ZERO;
        for (WmsInventoryCheckItem inventoryCheckItem : inventoryCheckItemList)
        {
            if (inventoryCheckItem.getProductId() == null)
            {
                throw new ServiceException("盘点明细商品不能为空");
            }
            if (inventoryCheckItem.getActualQty() == null)
            {
                throw new ServiceException("实盘数量不能为空");
            }
            BigDecimal actualQuantity = defaultBigDecimal(inventoryCheckItem.getActualQty());
            if (actualQuantity.compareTo(BigDecimal.ZERO) < 0)
            {
                throw new ServiceException("实盘数量不能小于0");
            }
            Long locationId = defaultLocationId(inventoryCheckItem.getLocationId());
            String batchNo = defaultBatchNo(inventoryCheckItem.getBatchNo());
            WmsStock stockQuery = new WmsStock();
            stockQuery.setWarehouseId(inventoryCheck.getWarehouseId());
            stockQuery.setProductId(inventoryCheckItem.getProductId());
            stockQuery.setLocationId(locationId);
            stockQuery.setBatchNo(batchNo);
            WmsStock stock = wmsStockMapper.selectWmsStockByKey(stockQuery);
            BigDecimal beforeQuantity;
            if (stock == null)
            {
                stock = new WmsStock();
                stock.setWarehouseId(inventoryCheck.getWarehouseId());
                stock.setProductId(inventoryCheckItem.getProductId());
                stock.setLocationId(locationId);
                stock.setBatchNo(batchNo);
                stock.setQuantity(actualQuantity);
                stock.setLockedQuantity(BigDecimal.ZERO);
                stock.setFrozenQuantity(BigDecimal.ZERO);
                stock.setWarningMinQty(BigDecimal.ZERO);
                stock.setWarningMaxQty(BigDecimal.ZERO);
                stock.setVersion(0L);
                stock.setCreateBy(operator);
                wmsStockMapper.insertWmsStock(stock);
                beforeQuantity = BigDecimal.ZERO;
            }
            else
            {
                if (defaultBigDecimal(stock.getLockedQuantity()).compareTo(BigDecimal.ZERO) > 0
                    || defaultBigDecimal(stock.getFrozenQuantity()).compareTo(BigDecimal.ZERO) > 0)
                {
                    throw new ServiceException("盘点范围存在占用或冻结库存，无法审核");
                }
                beforeQuantity = defaultBigDecimal(stock.getQuantity());
                stock.setQuantity(actualQuantity);
                stock.setUpdateBy(operator);
                wmsStockMapper.updateWmsStock(stock);
            }
            BigDecimal differenceQuantity = actualQuantity.subtract(beforeQuantity);
            BigDecimal itemPrice = defaultBigDecimal(inventoryCheckItem.getPrice());
            BigDecimal differenceAmount = itemPrice.multiply(differenceQuantity);
            boolean needUpdateItem = false;
            if (inventoryCheckItem.getStockQty() == null)
            {
                inventoryCheckItem.setStockQty(beforeQuantity);
                needUpdateItem = true;
            }
            if (inventoryCheckItem.getDiffQty() == null || inventoryCheckItem.getDiffQty().compareTo(differenceQuantity) != 0)
            {
                inventoryCheckItem.setDiffQty(differenceQuantity);
                needUpdateItem = true;
            }
            if (inventoryCheckItem.getDiffAmount() == null || inventoryCheckItem.getDiffAmount().compareTo(differenceAmount) != 0)
            {
                inventoryCheckItem.setDiffAmount(differenceAmount);
                needUpdateItem = true;
            }
            if (needUpdateItem)
            {
                wmsInventoryCheckItemMapper.updateWmsInventoryCheckItem(inventoryCheckItem);
            }
            if (differenceQuantity.compareTo(BigDecimal.ZERO) != 0)
            {
                WmsStockLog stockLog = new WmsStockLog();
                stockLog.setWarehouseId(inventoryCheck.getWarehouseId());
                stockLog.setProductId(inventoryCheckItem.getProductId());
                stockLog.setLocationId(locationId);
                stockLog.setBatchNo(batchNo);
                stockLog.setBillType("inventory_check");
                stockLog.setBillId(checkId);
                stockLog.setInOut(differenceQuantity.compareTo(BigDecimal.ZERO) > 0 ? "1" : "2");
                stockLog.setQuantity(differenceQuantity.abs());
                stockLog.setPrice(itemPrice);
                stockLog.setAmount(itemPrice.multiply(differenceQuantity.abs()));
                stockLog.setBeforeQty(beforeQuantity);
                stockLog.setAfterQty(actualQuantity);
                stockLog.setCreateBy(operator);
                wmsStockLogMapper.insertWmsStockLog(stockLog);
            }
            totalDifferenceQuantity = totalDifferenceQuantity.add(differenceQuantity);
            totalDifferenceAmount = totalDifferenceAmount.add(differenceAmount);
        }
        inventoryCheck.setStatus("1");
        inventoryCheck.setTotalDiffQty(totalDifferenceQuantity);
        inventoryCheck.setTotalDiffAmount(totalDifferenceAmount);
        inventoryCheck.setUpdateBy(operator);
        inventoryCheck.setUpdateTime(new Date());
        return wmsInventoryCheckMapper.updateWmsInventoryCheck(inventoryCheck);
    }

    /**
     * 空值转默认值
     * 
     * @param value 数值
     * @return 默认数值
     */
    private BigDecimal defaultBigDecimal(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO : value;
    }

    /**
     * 空库位转默认值
     * 
     * @param locationId 库位id
     * @return 默认库位id
     */
    private Long defaultLocationId(Long locationId)
    {
        return locationId == null ? 0L : locationId;
    }

    /**
     * 空批次转默认值
     * 
     * @param batchNo 批次号
     * @return 默认批次号
     */
    private String defaultBatchNo(String batchNo)
    {
        return batchNo == null ? "" : batchNo;
    }

    /**
     * 获取存在的盘点单
     * 
     * @param checkId 盘点单id
     * @return 盘点单信息
     */
    private WmsInventoryCheck getExistingInventoryCheck(Long checkId)
    {
        WmsInventoryCheck inventoryCheck = wmsInventoryCheckMapper.selectWmsInventoryCheckById(checkId);
        if (inventoryCheck == null)
        {
            throw new ServiceException("盘点单不存在");
        }
        return inventoryCheck;
    }

    /**
     * 校验盘点单是否允许修改
     * 
     * @param inventoryCheck 盘点单信息
     */
    private void validateInventoryCheckEditable(WmsInventoryCheck inventoryCheck)
    {
        if (!"0".equals(inventoryCheck.getStatus()))
        {
            throw new ServiceException("盘点单已审核，无法修改");
        }
    }

    private void checkInventoryCheckDeletable(Long checkId)
    {
        WmsInventoryCheck inventoryCheck = wmsInventoryCheckMapper.selectWmsInventoryCheckById(checkId);
        if (inventoryCheck == null)
        {
            throw new ServiceException("盘点单不存在");
        }
        if (!"0".equals(inventoryCheck.getStatus()))
        {
            throw new ServiceException("盘点单状态已变更，无法删除");
        }
    }
}

