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
import com.canglian.business.domain.WmsOutbound;
import com.canglian.business.domain.WmsOutboundItem;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsStockLog;
import com.canglian.business.mapper.WmsOutboundMapper;
import com.canglian.business.mapper.WmsOutboundItemMapper;
import com.canglian.business.mapper.WmsStockLogMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IWmsOutboundService;
import com.canglian.common.utils.StringUtils;

/**
 * 出库单 服务层实现
 * 
 * @author canglian
 */
@Service
public class WmsOutboundServiceImpl implements IWmsOutboundService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private WmsOutboundMapper wmsOutboundMapper;

    @Autowired
    private WmsOutboundItemMapper wmsOutboundItemMapper;

    @Autowired
    private WmsStockMapper wmsStockMapper;

    @Autowired
    private WmsStockLogMapper wmsStockLogMapper;

    /**
     * 查询出库单信息
     * 
     * @param outboundId 出库单id
     * @return 出库单信息
     */
    @Override
    public WmsOutbound selectWmsOutboundById(Long outboundId)
    {
        return wmsOutboundMapper.selectWmsOutboundById(outboundId);
    }

    /**
     * 查询出库单列表
     * 
     * @param wmsOutbound 出库单信息
     * @return 出库单集合
     */
    @Override
    public List<WmsOutbound> selectWmsOutboundList(WmsOutbound wmsOutbound)
    {
        return wmsOutboundMapper.selectWmsOutboundList(wmsOutbound);
    }

    /**
     * 新增出库单
     * 
     * @param wmsOutbound 出库单信息
     * @return 结果
     */
    @Override
    public int insertWmsOutbound(WmsOutbound wmsOutbound)
    {
        if (StringUtils.isEmpty(wmsOutbound.getOutboundNo()))
        {
            wmsOutbound.setOutboundNo(generateOutboundNo());
        }
        return wmsOutboundMapper.insertWmsOutbound(wmsOutbound);
    }

    /**
     * 修改出库单
     * 
     * @param wmsOutbound 出库单信息
     * @return 结果
     */
    @Override
    public int updateWmsOutbound(WmsOutbound wmsOutbound)
    {
        return wmsOutboundMapper.updateWmsOutbound(wmsOutbound);
    }

    /**
     * 删除出库单
     * 
     * @param outboundId 出库单id
     * @return 结果
     */
    @Override
    public int deleteWmsOutboundById(Long outboundId)
    {
        checkOutboundDeletable(outboundId);
        return wmsOutboundMapper.deleteWmsOutboundById(outboundId);
    }

    /**
     * 批量删除出库单
     * 
     * @param outboundIds 需要删除的出库单id
     * @return 结果
     */
    @Override
    public int deleteWmsOutboundByIds(Long[] outboundIds)
    {
        for (Long outboundId : outboundIds)
        {
            checkOutboundDeletable(outboundId);
        }
        return wmsOutboundMapper.deleteWmsOutboundByIds(outboundIds);
    }

    /**
     * 出库单审核
     * 
     * @param outboundId 出库单id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    @Transactional
    public int auditWmsOutbound(Long outboundId, String operator)
    {
        WmsOutbound outbound = wmsOutboundMapper.selectWmsOutboundById(outboundId);
        if (outbound == null)
        {
            throw new ServiceException("出库单不存在");
        }
        if (!"0".equals(outbound.getStatus()))
        {
            throw new ServiceException("出库单状态已变更，无法审核");
        }
        WmsOutboundItem outboundItemQuery = new WmsOutboundItem();
        outboundItemQuery.setOutboundId(outboundId);
        List<WmsOutboundItem> outboundItemList = wmsOutboundItemMapper.selectWmsOutboundItemList(outboundItemQuery);
        if (outboundItemList == null || outboundItemList.isEmpty())
        {
            throw new ServiceException("出库单明细不能为空");
        }
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (WmsOutboundItem outboundItem : outboundItemList)
        {
            BigDecimal itemQuantity = defaultBigDecimal(outboundItem.getQuantity());
            if (itemQuantity.compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("出库数量必须大于0");
            }
            if (outboundItem.getProductId() == null)
            {
                throw new ServiceException("出库明细商品不能为空");
            }
            if (outbound.getWarehouseId() == null)
            {
                throw new ServiceException("出库仓库不能为空");
            }
            Long locationId = defaultLocationId(outboundItem.getLocationId());
            String batchNo = defaultBatchNo(outboundItem.getBatchNo());
            WmsStock stockQuery = new WmsStock();
            stockQuery.setWarehouseId(outbound.getWarehouseId());
            stockQuery.setProductId(outboundItem.getProductId());
            stockQuery.setLocationId(locationId);
            stockQuery.setBatchNo(batchNo);
            WmsStock stock = wmsStockMapper.selectWmsStockByKey(stockQuery);
            if (stock == null)
            {
                throw new ServiceException("库存不足，无法出库");
            }
            BigDecimal beforeQuantity = defaultBigDecimal(stock.getQuantity());
            BigDecimal availableQuantity = calculateAvailableQuantity(stock);
            if (availableQuantity.compareTo(itemQuantity) < 0)
            {
                throw new ServiceException("可用库存不足，无法出库");
            }
            BigDecimal afterQuantity = beforeQuantity.subtract(itemQuantity);
            if (afterQuantity.compareTo(BigDecimal.ZERO) < 0)
            {
                throw new ServiceException("库存不足，无法出库");
            }
            stock.setQuantity(afterQuantity);
            stock.setUpdateBy(operator);
            wmsStockMapper.updateWmsStock(stock);
            WmsStockLog stockLog = new WmsStockLog();
            stockLog.setWarehouseId(outbound.getWarehouseId());
            stockLog.setProductId(outboundItem.getProductId());
            stockLog.setLocationId(locationId);
            stockLog.setBatchNo(batchNo);
            stockLog.setBillType("outbound");
            stockLog.setBillId(outboundId);
            stockLog.setInOut("2");
            stockLog.setQuantity(itemQuantity);
            BigDecimal itemPrice = defaultBigDecimal(outboundItem.getPrice());
            BigDecimal itemAmount = outboundItem.getAmount();
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
        outbound.setStatus("1");
        outbound.setAuditBy(operator);
        outbound.setAuditTime(new Date());
        outbound.setUpdateBy(operator);
        outbound.setTotalQty(totalQuantity);
        outbound.setTotalAmount(totalAmount);
        return wmsOutboundMapper.updateWmsOutbound(outbound);
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

    private String generateOutboundNo()
    {
        String noPrefix = "out" + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String currentMaxNo = wmsOutboundMapper.selectMaxOutboundNoByPrefix(noPrefix);
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

    private void checkOutboundDeletable(Long outboundId)
    {
        WmsOutbound outbound = wmsOutboundMapper.selectWmsOutboundById(outboundId);
        if (outbound == null)
        {
            throw new ServiceException("出库单不存在");
        }
        if (!"0".equals(outbound.getStatus()))
        {
            throw new ServiceException("出库单状态已变更，无法删除");
        }
    }
}

