package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.common.exception.ServiceException;
import com.canglian.business.domain.WmsPurchaseReturn;
import com.canglian.business.domain.WmsPurchaseReturnItem;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsStockLog;
import com.canglian.business.mapper.WmsPurchaseReturnMapper;
import com.canglian.business.mapper.WmsPurchaseReturnItemMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.mapper.WmsStockLogMapper;
import com.canglian.business.service.IWmsPurchaseReturnService;
import com.canglian.common.utils.StringUtils;

/**
 * 采购退货 服务层实现
 * 
 * @author canglian
 */
@Service
public class WmsPurchaseReturnServiceImpl implements IWmsPurchaseReturnService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private WmsPurchaseReturnMapper wmsPurchaseReturnMapper;

    @Autowired
    private WmsPurchaseReturnItemMapper wmsPurchaseReturnItemMapper;

    @Autowired
    private WmsStockMapper wmsStockMapper;

    @Autowired
    private WmsStockLogMapper wmsStockLogMapper;

    /**
     * 查询采购退货信息
     * 
     * @param purchaseReturnId 采购退货id
     * @return 采购退货信息
     */
    @Override
    public WmsPurchaseReturn selectWmsPurchaseReturnById(Long purchaseReturnId)
    {
        return wmsPurchaseReturnMapper.selectWmsPurchaseReturnById(purchaseReturnId);
    }

    /**
     * 查询采购退货列表
     * 
     * @param wmsPurchaseReturn 采购退货信息
     * @return 采购退货集合
     */
    @Override
    public List<WmsPurchaseReturn> selectWmsPurchaseReturnList(WmsPurchaseReturn wmsPurchaseReturn)
    {
        return wmsPurchaseReturnMapper.selectWmsPurchaseReturnList(wmsPurchaseReturn);
    }

    /**
     * 新增采购退货
     * 
     * @param wmsPurchaseReturn 采购退货信息
     * @return 结果
     */
    @Override
    public int insertWmsPurchaseReturn(WmsPurchaseReturn wmsPurchaseReturn)
    {
        if (StringUtils.isEmpty(wmsPurchaseReturn.getReturnNo()))
        {
            wmsPurchaseReturn.setReturnNo(generateReturnNo());
        }
        if (wmsPurchaseReturn.getBusinessDate() == null)
        {
            wmsPurchaseReturn.setBusinessDate(new Date());
        }
        wmsPurchaseReturn.setTotalQty(null);
        wmsPurchaseReturn.setTotalAmount(null);
        wmsPurchaseReturn.setStatus("0");
        wmsPurchaseReturn.setBizStatus("draft");
        wmsPurchaseReturn.setAuditBy(null);
        wmsPurchaseReturn.setAuditTime(null);
        return wmsPurchaseReturnMapper.insertWmsPurchaseReturn(wmsPurchaseReturn);
    }

    /**
     * 修改采购退货
     * 
     * @param wmsPurchaseReturn 采购退货信息
     * @return 结果
     */
    @Override
    public int updateWmsPurchaseReturn(WmsPurchaseReturn wmsPurchaseReturn)
    {
        WmsPurchaseReturn purchaseReturn = getExistingPurchaseReturn(wmsPurchaseReturn.getPurchaseReturnId());
        validatePurchaseReturnEditable(purchaseReturn);
        wmsPurchaseReturn.setStatus(purchaseReturn.getStatus());
        wmsPurchaseReturn.setAuditBy(purchaseReturn.getAuditBy());
        wmsPurchaseReturn.setAuditTime(purchaseReturn.getAuditTime());
        wmsPurchaseReturn.setTotalQty(purchaseReturn.getTotalQty());
        wmsPurchaseReturn.setTotalAmount(purchaseReturn.getTotalAmount());
        wmsPurchaseReturn.setBizStatus(purchaseReturn.getBizStatus());
        return wmsPurchaseReturnMapper.updateWmsPurchaseReturn(wmsPurchaseReturn);
    }

    /**
     * 删除采购退货
     * 
     * @param purchaseReturnId 采购退货id
     * @return 结果
     */
    @Override
    public int deleteWmsPurchaseReturnById(Long purchaseReturnId)
    {
        checkPurchaseReturnDeletable(purchaseReturnId);
        return wmsPurchaseReturnMapper.deleteWmsPurchaseReturnById(purchaseReturnId);
    }

    /**
     * 批量删除采购退货
     * 
     * @param purchaseReturnIds 需要删除的采购退货id
     * @return 结果
     */
    @Override
    public int deleteWmsPurchaseReturnByIds(Long[] purchaseReturnIds)
    {
        for (Long purchaseReturnId : purchaseReturnIds)
        {
            checkPurchaseReturnDeletable(purchaseReturnId);
        }
        return wmsPurchaseReturnMapper.deleteWmsPurchaseReturnByIds(purchaseReturnIds);
    }

    /**
     * 采购退货审核
     * 
     * @param purchaseReturnId 采购退货id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    @Transactional
    public int auditWmsPurchaseReturn(Long purchaseReturnId, String operator)
    {
        WmsPurchaseReturn purchaseReturn = getExistingPurchaseReturn(purchaseReturnId);
        if (!"0".equals(purchaseReturn.getStatus()))
        {
            throw new ServiceException("采购退货单状态已变更，无法审核");
        }
        if (purchaseReturn.getWarehouseId() == null)
        {
            throw new ServiceException("退货仓库不能为空");
        }
        WmsPurchaseReturnItem itemQuery = new WmsPurchaseReturnItem();
        itemQuery.setPurchaseReturnId(purchaseReturnId);
        List<WmsPurchaseReturnItem> itemList = wmsPurchaseReturnItemMapper.selectWmsPurchaseReturnItemList(itemQuery);
        if (itemList == null || itemList.isEmpty())
        {
            throw new ServiceException("采购退货明细不能为空");
        }
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (WmsPurchaseReturnItem returnItem : itemList)
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
            stockQuery.setWarehouseId(purchaseReturn.getWarehouseId());
            stockQuery.setProductId(returnItem.getProductId());
            stockQuery.setLocationId(locationId);
            stockQuery.setBatchNo(batchNo);
            WmsStock stock = wmsStockMapper.selectWmsStockByKey(stockQuery);
            if (stock == null)
            {
                throw new ServiceException("库存不足，无法退货");
            }
            BigDecimal beforeQuantity = defaultBigDecimal(stock.getQuantity());
            BigDecimal availableQuantity = calculateAvailableQuantity(stock);
            if (availableQuantity.compareTo(itemQuantity) < 0)
            {
                throw new ServiceException("可用库存不足，无法退货");
            }
            BigDecimal afterQuantity = beforeQuantity.subtract(itemQuantity);
            if (afterQuantity.compareTo(BigDecimal.ZERO) < 0)
            {
                throw new ServiceException("库存不足，无法退货");
            }
            stock.setQuantity(afterQuantity);
            stock.setUpdateBy(operator);
            wmsStockMapper.updateWmsStock(stock);
            WmsStockLog stockLog = new WmsStockLog();
            stockLog.setWarehouseId(purchaseReturn.getWarehouseId());
            stockLog.setProductId(returnItem.getProductId());
            stockLog.setLocationId(locationId);
            stockLog.setBatchNo(batchNo);
            stockLog.setBillType("purchase_return");
            stockLog.setBillId(purchaseReturnId);
            stockLog.setInOut("2");
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
        purchaseReturn.setStatus("1");
        purchaseReturn.setAuditBy(operator);
        purchaseReturn.setAuditTime(new Date());
        purchaseReturn.setUpdateBy(operator);
        purchaseReturn.setTotalQty(totalQuantity);
        purchaseReturn.setTotalAmount(totalAmount);
        purchaseReturn.setBizStatus("completed");
        return wmsPurchaseReturnMapper.updateWmsPurchaseReturn(purchaseReturn);
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
     * 计算可用库存
     * 
     * @param stock 库存
     * @return 可用库存
     */
    private BigDecimal calculateAvailableQuantity(WmsStock stock)
    {
        BigDecimal quantity = defaultBigDecimal(stock.getQuantity());
        BigDecimal lockedQuantity = defaultBigDecimal(stock.getLockedQuantity());
        BigDecimal frozenQuantity = defaultBigDecimal(stock.getFrozenQuantity());
        return quantity.subtract(lockedQuantity).subtract(frozenQuantity);
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
     * 获取存在的采购退货单
     * 
     * @param purchaseReturnId 采购退货id
     * @return 采购退货信息
     */
    private WmsPurchaseReturn getExistingPurchaseReturn(Long purchaseReturnId)
    {
        WmsPurchaseReturn purchaseReturn = wmsPurchaseReturnMapper.selectWmsPurchaseReturnById(purchaseReturnId);
        if (purchaseReturn == null)
        {
            throw new ServiceException("采购退货单不存在");
        }
        return purchaseReturn;
    }

    /**
     * 生成采购退货单号
     * 
     * @return 采购退货单号
     */
    private String generateReturnNo()
    {
        String returnNoPrefix = "prt" + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String currentMaxReturnNo = wmsPurchaseReturnMapper.selectMaxReturnNoByPrefix(returnNoPrefix);
        int nextSequence = 1;
        if (StringUtils.isNotEmpty(currentMaxReturnNo) && currentMaxReturnNo.length() > returnNoPrefix.length())
        {
            String sequenceText = currentMaxReturnNo.substring(returnNoPrefix.length());
            if (StringUtils.isNumeric(sequenceText))
            {
                nextSequence = Integer.parseInt(sequenceText) + 1;
            }
        }
        return returnNoPrefix + String.format("%03d", nextSequence);
    }

    /**
     * 校验采购退货单是否允许修改
     * 
     * @param purchaseReturn 采购退货信息
     */
    private void validatePurchaseReturnEditable(WmsPurchaseReturn purchaseReturn)
    {
        if (!"0".equals(purchaseReturn.getStatus()))
        {
            throw new ServiceException("采购退货单已审核，无法修改");
        }
    }

    /**
     * 校验采购退货是否可删除
     * 
     * @param purchaseReturnId 采购退货id
     */
    private void checkPurchaseReturnDeletable(Long purchaseReturnId)
    {
        WmsPurchaseReturn purchaseReturn = wmsPurchaseReturnMapper.selectWmsPurchaseReturnById(purchaseReturnId);
        if (purchaseReturn == null)
        {
            throw new ServiceException("采购退货单不存在");
        }
        if (!"0".equals(purchaseReturn.getStatus()))
        {
            throw new ServiceException("采购退货单状态已变更，无法删除");
        }
    }
}

