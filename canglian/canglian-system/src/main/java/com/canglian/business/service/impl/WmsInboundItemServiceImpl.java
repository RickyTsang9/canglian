package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.WmsInbound;
import com.canglian.business.domain.WmsInboundItem;
import com.canglian.business.mapper.WmsInboundItemMapper;
import com.canglian.business.mapper.WmsInboundMapper;
import com.canglian.business.service.IMdProductUnitService;
import com.canglian.business.service.IWmsInboundItemService;
import com.canglian.common.exception.ServiceException;

@Service
public class WmsInboundItemServiceImpl implements IWmsInboundItemService
{
    @Autowired
    private WmsInboundItemMapper wmsInboundItemMapper;

    @Autowired
    private WmsInboundMapper wmsInboundMapper;

    @Autowired(required = false)
    private IMdProductUnitService mdProductUnitService;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Override
    public WmsInboundItem selectWmsInboundItemById(Long inboundItemId)
    {
        WmsInboundItem item = wmsInboundItemMapper.selectWmsInboundItemById(inboundItemId);
        if (item != null && item.getInboundId() != null)
        {
            assertInboundAccess(item.getInboundId());
        }
        return item;
    }

    @Override
    public List<WmsInboundItem> selectWmsInboundItemList(WmsInboundItem wmsInboundItem)
    {
        if (wmsInboundItem != null && wmsInboundItem.getInboundId() != null)
        {
            assertInboundAccess(wmsInboundItem.getInboundId());
            return wmsInboundItemMapper.selectWmsInboundItemList(wmsInboundItem);
        }
        List<WmsInboundItem> list = wmsInboundItemMapper.selectWmsInboundItemList(wmsInboundItem);
        if (bizScopeHelper == null || list == null || list.isEmpty()) return list;
        java.util.List<WmsInboundItem> filtered = new java.util.ArrayList<WmsInboundItem>();
        java.util.Map<Long, Boolean> cache = new java.util.HashMap<Long, Boolean>();
        for (WmsInboundItem it : list)
        {
            Long pid = it.getInboundId();
            Boolean ok = cache.get(pid);
            if (ok == null)
            {
                WmsInbound doc = pid == null ? null : wmsInboundMapper.selectWmsInboundById(pid);
                ok = doc != null && bizScopeHelper.canAccessRecord(doc.getDeptId(), doc.getOwnerUserId(), doc.getWarehouseId());
                cache.put(pid, ok);
            }
            if (Boolean.TRUE.equals(ok)) filtered.add(it);
        }
        return filtered;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWmsInboundItem(WmsInboundItem wmsInboundItem)
    {
        WmsInbound doc = assertDraftInbound(wmsInboundItem.getInboundId());
        normalizeItem(wmsInboundItem);
        int rows = wmsInboundItemMapper.insertWmsInboundItem(wmsInboundItem);
        if (rows > 0) recalculateHeader(doc);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWmsInboundItem(WmsInboundItem wmsInboundItem)
    {
        WmsInboundItem existed = wmsInboundItemMapper.selectWmsInboundItemById(wmsInboundItem.getInboundItemId());
        if (existed == null)
        {
            throw new ServiceException("入库单明细不存在");
        }
        WmsInbound doc = assertDraftInbound(existed.getInboundId());
        if (wmsInboundItem.getInboundId() != null && !wmsInboundItem.getInboundId().equals(existed.getInboundId()))
        {
            assertDraftInbound(wmsInboundItem.getInboundId());
        }
        normalizeItem(wmsInboundItem);
        int rows = wmsInboundItemMapper.updateWmsInboundItem(wmsInboundItem);
        if (rows > 0) recalculateHeader(doc);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsInboundItemById(Long inboundItemId)
    {
        WmsInboundItem existed = wmsInboundItemMapper.selectWmsInboundItemById(inboundItemId);
        WmsInbound doc = null;
        if (existed != null)
        {
            doc = assertDraftInbound(existed.getInboundId());
        }
        int rows = wmsInboundItemMapper.deleteWmsInboundItemById(inboundItemId);
        if (rows > 0 && doc != null) recalculateHeader(doc);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsInboundItemByIds(Long[] inboundItemIds)
    {
        java.util.Map<Long, WmsInbound> affected = new java.util.LinkedHashMap<Long, WmsInbound>();
        for (Long id : inboundItemIds)
        {
            WmsInboundItem existed = wmsInboundItemMapper.selectWmsInboundItemById(id);
            if (existed != null)
            {
                WmsInbound doc = assertDraftInbound(existed.getInboundId());
                affected.put(doc.getInboundId(), doc);
            }
        }
        int rows = wmsInboundItemMapper.deleteWmsInboundItemByIds(inboundItemIds);
        if (rows > 0)
        {
            for (WmsInbound doc : affected.values()) recalculateHeader(doc);
        }
        return rows;
    }

    private void assertInboundAccess(Long inboundId)
    {
        if (bizScopeHelper == null || inboundId == null) return;
        WmsInbound doc = wmsInboundMapper.selectWmsInboundById(inboundId);
        if (doc != null)
        {
            bizScopeHelper.assertRecordAccess(doc.getDeptId(), doc.getOwnerUserId(), doc.getWarehouseId());
        }
    }

    private WmsInbound assertDraftInbound(Long inboundId)
    {
        WmsInbound doc = inboundId == null ? null : wmsInboundMapper.selectWmsInboundById(inboundId);
        if (doc == null)
        {
            throw new ServiceException("关联入库单不存在");
        }
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(doc.getDeptId(), doc.getOwnerUserId(), doc.getWarehouseId());
        }
        if (!"0".equals(doc.getStatus()))
        {
            throw new ServiceException("仅草稿态入库单允许维护明细");
        }
        return doc;
    }

    private void normalizeItem(WmsInboundItem item)
    {
        BigDecimal qty = item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity();
        if (qty.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("入库明细数量必须大于0");
        }
        BigDecimal ratio = mdProductUnitService != null && item.getProductId() != null
            ? mdProductUnitService.resolveConvertRatio(item.getProductId(), item.getUnitName())
            : (item.getConvertRatio() != null && item.getConvertRatio().compareTo(BigDecimal.ZERO) > 0 ? item.getConvertRatio() : BigDecimal.ONE);
        item.setConvertRatio(ratio);
        item.setBaseQuantity(qty.multiply(ratio).setScale(4, RoundingMode.HALF_UP));
        if (item.getBatchNo() != null) item.setBatchNo(item.getBatchNo().trim());
        if (item.getPrice() != null && (item.getAmount() == null || item.getAmount().compareTo(BigDecimal.ZERO) <= 0))
        {
            item.setAmount(qty.multiply(item.getPrice()).setScale(2, RoundingMode.HALF_UP));
        }
    }

    private void recalculateHeader(WmsInbound doc)
    {
        WmsInboundItem q = new WmsInboundItem();
        q.setInboundId(doc.getInboundId());
        List<WmsInboundItem> list = wmsInboundItemMapper.selectWmsInboundItemList(q);
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmt = BigDecimal.ZERO;
        if (list != null)
        {
            for (WmsInboundItem it : list)
            {
                if (it.getQuantity() != null) totalQty = totalQty.add(it.getQuantity());
                if (it.getAmount() != null) totalAmt = totalAmt.add(it.getAmount());
            }
        }
        doc.setTotalQty(totalQty);
        doc.setTotalAmount(totalAmt);
        wmsInboundMapper.updateWmsInbound(doc);
    }
}

