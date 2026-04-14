package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.common.exception.ServiceException;
import com.canglian.business.domain.WmsSaleReturn;
import com.canglian.business.domain.WmsSaleReturnItem;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsStockLog;
import com.canglian.business.mapper.WmsSaleReturnMapper;
import com.canglian.business.mapper.WmsSaleReturnItemMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.mapper.WmsStockLogMapper;
import com.canglian.business.service.IWmsSaleReturnService;

/**
 * 销售退货 服务层实现
 * 
 * @author canglian
 */
@Service
public class WmsSaleReturnServiceImpl implements IWmsSaleReturnService
{
    @Autowired
    private WmsSaleReturnMapper wmsSaleReturnMapper;

    @Autowired
    private WmsSaleReturnItemMapper wmsSaleReturnItemMapper;

    @Autowired
    private WmsStockMapper wmsStockMapper;

    @Autowired
    private WmsStockLogMapper wmsStockLogMapper;

    /**
     * 查询销售退货信息
     * 
     * @param saleReturnId 销售退货id
     * @return 销售退货信息
     */
    @Override
    public WmsSaleReturn selectWmsSaleReturnById(Long saleReturnId)
    {
        return wmsSaleReturnMapper.selectWmsSaleReturnById(saleReturnId);
    }

    /**
     * 查询销售退货列表
     * 
     * @param wmsSaleReturn 销售退货信息
     * @return 销售退货集合
     */
    @Override
    public List<WmsSaleReturn> selectWmsSaleReturnList(WmsSaleReturn wmsSaleReturn)
    {
        return wmsSaleReturnMapper.selectWmsSaleReturnList(wmsSaleReturn);
    }

    /**
     * 新增销售退货
     * 
     * @param wmsSaleReturn 销售退货信息
     * @return 结果
     */
    @Override
    public int insertWmsSaleReturn(WmsSaleReturn wmsSaleReturn)
    {
        return wmsSaleReturnMapper.insertWmsSaleReturn(wmsSaleReturn);
    }

    /**
     * 修改销售退货
     * 
     * @param wmsSaleReturn 销售退货信息
     * @return 结果
     */
    @Override
    public int updateWmsSaleReturn(WmsSaleReturn wmsSaleReturn)
    {
        return wmsSaleReturnMapper.updateWmsSaleReturn(wmsSaleReturn);
    }

    /**
     * 删除销售退货
     * 
     * @param saleReturnId 销售退货id
     * @return 结果
     */
    @Override
    public int deleteWmsSaleReturnById(Long saleReturnId)
    {
        checkSaleReturnDeletable(saleReturnId);
        return wmsSaleReturnMapper.deleteWmsSaleReturnById(saleReturnId);
    }

    /**
     * 批量删除销售退货
     * 
     * @param saleReturnIds 需要删除的销售退货id
     * @return 结果
     */
    @Override
    public int deleteWmsSaleReturnByIds(Long[] saleReturnIds)
    {
        for (Long saleReturnId : saleReturnIds)
        {
            checkSaleReturnDeletable(saleReturnId);
        }
        return wmsSaleReturnMapper.deleteWmsSaleReturnByIds(saleReturnIds);
    }

    /**
     * 销售退货审核
     * 
     * @param saleReturnId 销售退货id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    @Transactional
    public int auditWmsSaleReturn(Long saleReturnId, String operator)
    {
        WmsSaleReturn saleReturn = wmsSaleReturnMapper.selectWmsSaleReturnById(saleReturnId);
        if (saleReturn == null)
        {
            throw new ServiceException("销售退货单不存在");
        }
        if (!"0".equals(saleReturn.getStatus()))
        {
            throw new ServiceException("销售退货单状态已变更，无法审核");
        }
        if (saleReturn.getWarehouseId() == null)
        {
            throw new ServiceException("退货仓库不能为空");
        }
        WmsSaleReturnItem itemQuery = new WmsSaleReturnItem();
        itemQuery.setSaleReturnId(saleReturnId);
        List<WmsSaleReturnItem> itemList = wmsSaleReturnItemMapper.selectWmsSaleReturnItemList(itemQuery);
        if (itemList == null || itemList.isEmpty())
        {
            throw new ServiceException("销售退货明细不能为空");
        }
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (WmsSaleReturnItem returnItem : itemList)
        {
            BigDecimal itemQuantity = defaultBigDecimal(returnItem.getQuantity());
            if (itemQuantity.compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("退货数量必须大于0");
            }
            if (returnItem.getProductId() == null)
            {
                throw new ServiceException("退货明细商品不能为空");
            }
            Long locationId = defaultLocationId(null);
            String batchNo = defaultBatchNo(null);
            WmsStock stockQuery = new WmsStock();
            stockQuery.setWarehouseId(saleReturn.getWarehouseId());
            stockQuery.setProductId(returnItem.getProductId());
            stockQuery.setLocationId(locationId);
            stockQuery.setBatchNo(batchNo);
            WmsStock stock = wmsStockMapper.selectWmsStockByKey(stockQuery);
            BigDecimal beforeQuantity;
            if (stock == null)
            {
                stock = new WmsStock();
                stock.setWarehouseId(saleReturn.getWarehouseId());
                stock.setProductId(returnItem.getProductId());
                stock.setLocationId(locationId);
                stock.setBatchNo(batchNo);
                stock.setQuantity(itemQuantity);
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
                beforeQuantity = defaultBigDecimal(stock.getQuantity());
                stock.setQuantity(beforeQuantity.add(itemQuantity));
                stock.setUpdateBy(operator);
                wmsStockMapper.updateWmsStock(stock);
            }
            BigDecimal afterQuantity = beforeQuantity.add(itemQuantity);
            WmsStockLog stockLog = new WmsStockLog();
            stockLog.setWarehouseId(saleReturn.getWarehouseId());
            stockLog.setProductId(returnItem.getProductId());
            stockLog.setLocationId(locationId);
            stockLog.setBatchNo(batchNo);
            stockLog.setBillType("sale_return");
            stockLog.setBillId(saleReturnId);
            stockLog.setInOut("1");
            stockLog.setQuantity(itemQuantity);
            BigDecimal itemPrice = defaultBigDecimal(returnItem.getPrice());
            BigDecimal itemAmount = returnItem.getAmount();
            if (itemAmount == null)
            {
                itemAmount = itemPrice.multiply(itemQuantity);
            }
            stockLog.setPrice(itemPrice);
            stockLog.setAmount(itemAmount);
            stockLog.setBeforeQty(beforeQuantity);
            stockLog.setAfterQty(afterQuantity);
            stockLog.setCreateBy(operator);
            wmsStockLogMapper.insertWmsStockLog(stockLog);
            totalQuantity = totalQuantity.add(itemQuantity);
            totalAmount = totalAmount.add(itemAmount);
        }
        saleReturn.setStatus("1");
        saleReturn.setAuditBy(operator);
        saleReturn.setAuditTime(new Date());
        saleReturn.setUpdateBy(operator);
        saleReturn.setTotalQty(totalQuantity);
        saleReturn.setTotalAmount(totalAmount);
        return wmsSaleReturnMapper.updateWmsSaleReturn(saleReturn);
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
     * 校验销售退货是否可删除
     * 
     * @param saleReturnId 销售退货id
     */
    private void checkSaleReturnDeletable(Long saleReturnId)
    {
        WmsSaleReturn saleReturn = wmsSaleReturnMapper.selectWmsSaleReturnById(saleReturnId);
        if (saleReturn == null)
        {
            throw new ServiceException("销售退货单不存在");
        }
        if (!"0".equals(saleReturn.getStatus()))
        {
            throw new ServiceException("销售退货单状态已变更，无法删除");
        }
    }
}

