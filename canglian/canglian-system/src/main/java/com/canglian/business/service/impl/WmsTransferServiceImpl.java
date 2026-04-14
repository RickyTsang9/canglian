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
import com.canglian.business.domain.WmsTransfer;
import com.canglian.business.domain.WmsTransferItem;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsStockLog;
import com.canglian.business.mapper.WmsTransferMapper;
import com.canglian.business.mapper.WmsTransferItemMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.mapper.WmsStockLogMapper;
import com.canglian.business.service.IWmsTransferService;
import com.canglian.common.utils.StringUtils;

@Service
public class WmsTransferServiceImpl implements IWmsTransferService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private WmsTransferMapper wmsTransferMapper;

    @Autowired
    private WmsTransferItemMapper wmsTransferItemMapper;

    @Autowired
    private WmsStockMapper wmsStockMapper;

    @Autowired
    private WmsStockLogMapper wmsStockLogMapper;

    @Override
    public WmsTransfer selectWmsTransferById(Long transferId)
    {
        return wmsTransferMapper.selectWmsTransferById(transferId);
    }

    @Override
    public List<WmsTransfer> selectWmsTransferList(WmsTransfer wmsTransfer)
    {
        return wmsTransferMapper.selectWmsTransferList(wmsTransfer);
    }

    @Override
    public int insertWmsTransfer(WmsTransfer wmsTransfer)
    {
        if (StringUtils.isEmpty(wmsTransfer.getTransferNo()))
        {
            wmsTransfer.setTransferNo(generateTransferNo());
        }
        return wmsTransferMapper.insertWmsTransfer(wmsTransfer);
    }

    @Override
    public int updateWmsTransfer(WmsTransfer wmsTransfer)
    {
        return wmsTransferMapper.updateWmsTransfer(wmsTransfer);
    }

    @Override
    public int deleteWmsTransferById(Long transferId)
    {
        checkTransferDeletable(transferId);
        return wmsTransferMapper.deleteWmsTransferById(transferId);
    }

    @Override
    public int deleteWmsTransferByIds(Long[] transferIds)
    {
        for (Long transferId : transferIds)
        {
            checkTransferDeletable(transferId);
        }
        return wmsTransferMapper.deleteWmsTransferByIds(transferIds);
    }

    /**
     * 调拨单审核
     * 
     * @param transferId 调拨单id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    @Transactional
    public int auditWmsTransfer(Long transferId, String operator)
    {
        WmsTransfer transfer = wmsTransferMapper.selectWmsTransferById(transferId);
        if (transfer == null)
        {
            throw new ServiceException("调拨单不存在");
        }
        if (!"0".equals(transfer.getStatus()))
        {
            throw new ServiceException("调拨单状态已变更，无法审核");
        }
        if (transfer.getOutWarehouseId() == null)
        {
            throw new ServiceException("调出仓库不能为空");
        }
        if (transfer.getInWarehouseId() == null)
        {
            throw new ServiceException("调入仓库不能为空");
        }
        if (transfer.getOutWarehouseId().equals(transfer.getInWarehouseId()))
        {
            throw new ServiceException("调入仓库与调出仓库不能相同");
        }
        WmsTransferItem itemQuery = new WmsTransferItem();
        itemQuery.setTransferId(transferId);
        List<WmsTransferItem> itemList = wmsTransferItemMapper.selectWmsTransferItemList(itemQuery);
        if (itemList == null || itemList.isEmpty())
        {
            throw new ServiceException("调拨单明细不能为空");
        }
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (WmsTransferItem transferItem : itemList)
        {
            BigDecimal itemQuantity = defaultBigDecimal(transferItem.getQuantity());
            if (itemQuantity.compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("调拨数量必须大于0");
            }
            if (transferItem.getProductId() == null)
            {
                throw new ServiceException("调拨明细商品不能为空");
            }
            Long outLocationId = defaultLocationId(transferItem.getOutLocationId());
            Long inLocationId = defaultLocationId(transferItem.getInLocationId());
            String batchNo = defaultBatchNo(transferItem.getBatchNo());
            // 调出仓库存扣减
            WmsStock outStockQuery = new WmsStock();
            outStockQuery.setWarehouseId(transfer.getOutWarehouseId());
            outStockQuery.setProductId(transferItem.getProductId());
            outStockQuery.setLocationId(outLocationId);
            outStockQuery.setBatchNo(batchNo);
            WmsStock outStock = wmsStockMapper.selectWmsStockByKey(outStockQuery);
            if (outStock == null)
            {
                throw new ServiceException("库存不足，无法调出");
            }
            BigDecimal outBeforeQuantity = defaultBigDecimal(outStock.getQuantity());
            BigDecimal outAvailableQuantity = calculateAvailableQuantity(outStock);
            if (outAvailableQuantity.compareTo(itemQuantity) < 0)
            {
                throw new ServiceException("可用库存不足，无法调出");
            }
            BigDecimal outAfterQuantity = outBeforeQuantity.subtract(itemQuantity);
            if (outAfterQuantity.compareTo(BigDecimal.ZERO) < 0)
            {
                throw new ServiceException("库存不足，无法调出");
            }
            outStock.setQuantity(outAfterQuantity);
            outStock.setUpdateBy(operator);
            wmsStockMapper.updateWmsStock(outStock);
            // 记录调出流水
            WmsStockLog outLog = new WmsStockLog();
            outLog.setWarehouseId(transfer.getOutWarehouseId());
            outLog.setProductId(transferItem.getProductId());
            outLog.setLocationId(outLocationId);
            outLog.setBatchNo(batchNo);
            outLog.setBillType("transfer");
            outLog.setBillId(transferId);
            outLog.setInOut("2");
            outLog.setQuantity(itemQuantity);
            BigDecimal itemPrice = defaultBigDecimal(transferItem.getPrice());
            BigDecimal itemAmount = transferItem.getAmount();
            if (itemAmount == null)
            {
                itemAmount = itemPrice.multiply(itemQuantity);
            }
            outLog.setPrice(itemPrice);
            outLog.setAmount(itemAmount);
            outLog.setBeforeQty(outBeforeQuantity);
            outLog.setAfterQty(outAfterQuantity);
            outLog.setCreateBy(operator);
            wmsStockLogMapper.insertWmsStockLog(outLog);

            // 调入仓库存增加
            WmsStock inStockQuery = new WmsStock();
            inStockQuery.setWarehouseId(transfer.getInWarehouseId());
            inStockQuery.setProductId(transferItem.getProductId());
            inStockQuery.setLocationId(inLocationId);
            inStockQuery.setBatchNo(batchNo);
            WmsStock inStock = wmsStockMapper.selectWmsStockByKey(inStockQuery);
            BigDecimal inBeforeQuantity;
            if (inStock == null)
            {
                inStock = new WmsStock();
                inStock.setWarehouseId(transfer.getInWarehouseId());
                inStock.setProductId(transferItem.getProductId());
                inStock.setLocationId(inLocationId);
                inStock.setBatchNo(batchNo);
                inStock.setQuantity(itemQuantity);
                inStock.setLockedQuantity(BigDecimal.ZERO);
                inStock.setFrozenQuantity(BigDecimal.ZERO);
                inStock.setWarningMinQty(BigDecimal.ZERO);
                inStock.setWarningMaxQty(BigDecimal.ZERO);
                inStock.setVersion(0L);
                inStock.setCreateBy(operator);
                wmsStockMapper.insertWmsStock(inStock);
                inBeforeQuantity = BigDecimal.ZERO;
            }
            else
            {
                inBeforeQuantity = defaultBigDecimal(inStock.getQuantity());
                inStock.setQuantity(inBeforeQuantity.add(itemQuantity));
                inStock.setUpdateBy(operator);
                wmsStockMapper.updateWmsStock(inStock);
            }
            BigDecimal inAfterQuantity = inBeforeQuantity.add(itemQuantity);
            // 记录调入流水
            WmsStockLog inLog = new WmsStockLog();
            inLog.setWarehouseId(transfer.getInWarehouseId());
            inLog.setProductId(transferItem.getProductId());
            inLog.setLocationId(inLocationId);
            inLog.setBatchNo(batchNo);
            inLog.setBillType("transfer");
            inLog.setBillId(transferId);
            inLog.setInOut("1");
            inLog.setQuantity(itemQuantity);
            inLog.setPrice(itemPrice);
            inLog.setAmount(itemAmount);
            inLog.setBeforeQty(inBeforeQuantity);
            inLog.setAfterQty(inAfterQuantity);
            inLog.setCreateBy(operator);
            wmsStockLogMapper.insertWmsStockLog(inLog);

            totalQuantity = totalQuantity.add(itemQuantity);
            totalAmount = totalAmount.add(itemAmount);
        }
        transfer.setStatus("1");
        transfer.setUpdateBy(operator);
        transfer.setUpdateTime(new Date());
        transfer.setTotalQty(totalQuantity);
        transfer.setTotalAmount(totalAmount);
        return wmsTransferMapper.updateWmsTransfer(transfer);
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

    private String generateTransferNo()
    {
        String noPrefix = "trf" + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String currentMaxNo = wmsTransferMapper.selectMaxTransferNoByPrefix(noPrefix);
        int nextSequence = 1;
        if (StringUtils.isNotEmpty(currentMaxNo) && currentMaxNo.length() > noPrefix.length())
        {
            String sequenceText = currentMaxNo.substring(noPrefix.length());
            if (StringUtils.isNumeric(sequenceText))
            {
                nextSequence = Integer.parseInt(sequenceText) + 1;
            }
        }
        return noPrefix + String.format("%03d", nextSequence);
    }

    private void checkTransferDeletable(Long transferId)
    {
        WmsTransfer transfer = wmsTransferMapper.selectWmsTransferById(transferId);
        if (transfer == null)
        {
            throw new ServiceException("调拨单不存在");
        }
        if (!"0".equals(transfer.getStatus()))
        {
            throw new ServiceException("调拨单状态已变更，无法删除");
        }
    }
}

