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
import com.canglian.business.domain.WmsInbound;
import com.canglian.business.domain.WmsInboundItem;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsStockLog;
import com.canglian.business.mapper.WmsInboundMapper;
import com.canglian.business.mapper.WmsInboundItemMapper;
import com.canglian.business.mapper.WmsStockLogMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IWmsInboundService;
import com.canglian.common.utils.StringUtils;

/**
 * 入库单 服务层实现
 * 
 * @author canglian
 */
@Service
public class WmsInboundServiceImpl implements IWmsInboundService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private WmsInboundMapper wmsInboundMapper;

    @Autowired
    private WmsInboundItemMapper wmsInboundItemMapper;

    @Autowired
    private WmsStockMapper wmsStockMapper;

    @Autowired
    private WmsStockLogMapper wmsStockLogMapper;

    /**
     * 查询入库单信息
     * 
     * @param inboundId 入库单id
     * @return 入库单信息
     */
    @Override
    public WmsInbound selectWmsInboundById(Long inboundId)
    {
        return wmsInboundMapper.selectWmsInboundById(inboundId);
    }

    /**
     * 查询入库单列表
     * 
     * @param wmsInbound 入库单信息
     * @return 入库单集合
     */
    @Override
    public List<WmsInbound> selectWmsInboundList(WmsInbound wmsInbound)
    {
        return wmsInboundMapper.selectWmsInboundList(wmsInbound);
    }

    /**
     * 新增入库单
     * 
     * @param wmsInbound 入库单信息
     * @return 结果
     */
    @Override
    public int insertWmsInbound(WmsInbound wmsInbound)
    {
        if (StringUtils.isEmpty(wmsInbound.getInboundNo()))
        {
            wmsInbound.setInboundNo(generateInboundNo());
        }
        wmsInbound.setStatus("0");
        wmsInbound.setAuditBy(null);
        wmsInbound.setAuditTime(null);
        return wmsInboundMapper.insertWmsInbound(wmsInbound);
    }

    /**
     * 修改入库单
     * 
     * @param wmsInbound 入库单信息
     * @return 结果
     */
    @Override
    public int updateWmsInbound(WmsInbound wmsInbound)
    {
        WmsInbound inbound = getExistingInbound(wmsInbound.getInboundId());
        validateInboundEditable(inbound);
        wmsInbound.setStatus(inbound.getStatus());
        wmsInbound.setAuditBy(inbound.getAuditBy());
        wmsInbound.setAuditTime(inbound.getAuditTime());
        return wmsInboundMapper.updateWmsInbound(wmsInbound);
    }

    /**
     * 删除入库单
     * 
     * @param inboundId 入库单id
     * @return 结果
     */
    @Override
    public int deleteWmsInboundById(Long inboundId)
    {
        checkInboundDeletable(inboundId);
        return wmsInboundMapper.deleteWmsInboundById(inboundId);
    }

    /**
     * 批量删除入库单
     * 
     * @param inboundIds 需要删除的入库单id
     * @return 结果
     */
    @Override
    public int deleteWmsInboundByIds(Long[] inboundIds)
    {
        for (Long inboundId : inboundIds)
        {
            checkInboundDeletable(inboundId);
        }
        return wmsInboundMapper.deleteWmsInboundByIds(inboundIds);
    }

    /**
     * 入库单审核
     * 
     * @param inboundId 入库单id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    @Transactional
    public int auditWmsInbound(Long inboundId, String operator)
    {
        WmsInbound inbound = getExistingInbound(inboundId);
        if (!"0".equals(inbound.getStatus()))
        {
            throw new ServiceException("入库单状态已变更，无法审核");
        }
        WmsInboundItem inboundItemQuery = new WmsInboundItem();
        inboundItemQuery.setInboundId(inboundId);
        List<WmsInboundItem> inboundItemList = wmsInboundItemMapper.selectWmsInboundItemList(inboundItemQuery);
        if (inboundItemList == null || inboundItemList.isEmpty())
        {
            throw new ServiceException("入库单明细不能为空");
        }
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (WmsInboundItem inboundItem : inboundItemList)
        {
            BigDecimal itemQuantity = defaultBigDecimal(inboundItem.getQuantity());
            if (itemQuantity.compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("入库数量必须大于0");
            }
            if (inboundItem.getProductId() == null)
            {
                throw new ServiceException("入库明细商品不能为空");
            }
            if (inbound.getWarehouseId() == null)
            {
                throw new ServiceException("入库仓库不能为空");
            }
            Long locationId = defaultLocationId(inboundItem.getLocationId());
            String batchNo = defaultBatchNo(inboundItem.getBatchNo());
            WmsStock stockQuery = new WmsStock();
            stockQuery.setWarehouseId(inbound.getWarehouseId());
            stockQuery.setProductId(inboundItem.getProductId());
            stockQuery.setLocationId(locationId);
            stockQuery.setBatchNo(batchNo);
            WmsStock stock = wmsStockMapper.selectWmsStockByKey(stockQuery);
            BigDecimal beforeQuantity;
            if (stock == null)
            {
                stock = new WmsStock();
                stock.setWarehouseId(inbound.getWarehouseId());
                stock.setProductId(inboundItem.getProductId());
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
            stockLog.setWarehouseId(inbound.getWarehouseId());
            stockLog.setProductId(inboundItem.getProductId());
            stockLog.setLocationId(locationId);
            stockLog.setBatchNo(batchNo);
            stockLog.setBillType("inbound");
            stockLog.setBillId(inboundId);
            stockLog.setInOut("1");
            stockLog.setQuantity(itemQuantity);
            BigDecimal itemPrice = defaultBigDecimal(inboundItem.getPrice());
            BigDecimal itemAmount = inboundItem.getAmount();
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
        inbound.setStatus("1");
        inbound.setAuditBy(operator);
        inbound.setAuditTime(new Date());
        inbound.setUpdateBy(operator);
        inbound.setTotalQty(totalQuantity);
        inbound.setTotalAmount(totalAmount);
        return wmsInboundMapper.updateWmsInbound(inbound);
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
     * 获取存在的入库单
     * 
     * @param inboundId 入库单id
     * @return 入库单信息
     */
    private WmsInbound getExistingInbound(Long inboundId)
    {
        WmsInbound inbound = wmsInboundMapper.selectWmsInboundById(inboundId);
        if (inbound == null)
        {
            throw new ServiceException("入库单不存在");
        }
        return inbound;
    }

    /**
     * 校验入库单是否允许修改
     * 
     * @param inbound 入库单信息
     */
    private void validateInboundEditable(WmsInbound inbound)
    {
        if (!"0".equals(inbound.getStatus()))
        {
            throw new ServiceException("入库单已审核，无法修改");
        }
    }

    /**
     * 生成入库单号
     * 
     * @return 入库单号
     */
    private String generateInboundNo()
    {
        String noPrefix = "inb" + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String currentMaxNo = wmsInboundMapper.selectMaxInboundNoByPrefix(noPrefix);
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

    /**
     * 校验入库单是否可删除
     * 
     * @param inboundId 入库单id
     */
    private void checkInboundDeletable(Long inboundId)
    {
        WmsInbound inbound = wmsInboundMapper.selectWmsInboundById(inboundId);
        if (inbound == null)
        {
            throw new ServiceException("入库单不存在");
        }
        if (!"0".equals(inbound.getStatus()))
        {
            throw new ServiceException("入库单状态已变更，无法删除");
        }
    }
}

