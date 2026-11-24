package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.WmsOutbound;
import com.canglian.business.domain.WmsOutboundItem;
import com.canglian.business.mapper.WmsOutboundItemMapper;
import com.canglian.business.mapper.WmsOutboundMapper;
import com.canglian.business.service.IMdProductUnitService;
import com.canglian.business.service.IWmsOutboundItemService;
import com.canglian.common.exception.ServiceException;

@Service
public class WmsOutboundItemServiceImpl implements IWmsOutboundItemService
{
    @Autowired
    private WmsOutboundItemMapper wmsOutboundItemMapper;

    @Autowired
    private WmsOutboundMapper wmsOutboundMapper;

    @Autowired(required = false)
    private IMdProductUnitService mdProductUnitService;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Override
    public WmsOutboundItem selectWmsOutboundItemById(Long outboundItemId)
    {
        WmsOutboundItem item = wmsOutboundItemMapper.selectWmsOutboundItemById(outboundItemId);
        if (item != null && item.getOutboundId() != null)
        {
            assertOutboundAccess(item.getOutboundId());
        }
        return item;
    }

    @Override
    public List<WmsOutboundItem> selectWmsOutboundItemList(WmsOutboundItem wmsOutboundItem)
    {
        if (wmsOutboundItem != null && wmsOutboundItem.getOutboundId() != null)
        {
            assertOutboundAccess(wmsOutboundItem.getOutboundId());
            return wmsOutboundItemMapper.selectWmsOutboundItemList(wmsOutboundItem);
        }
        List<WmsOutboundItem> list = wmsOutboundItemMapper.selectWmsOutboundItemList(wmsOutboundItem);
        if (bizScopeHelper == null || list == null || list.isEmpty()) return list;
        java.util.List<WmsOutboundItem> filtered = new java.util.ArrayList<WmsOutboundItem>();
        java.util.Map<Long, Boolean> cache = new java.util.HashMap<Long, Boolean>();
        for (WmsOutboundItem it : list)
        {
            Long pid = it.getOutboundId();
            Boolean ok = cache.get(pid);
            if (ok == null)
            {
                WmsOutbound doc = pid == null ? null : wmsOutboundMapper.selectWmsOutboundById(pid);
                ok = doc != null && bizScopeHelper.canAccessRecord(doc.getDeptId(), doc.getOwnerUserId(), doc.getWarehouseId());
                cache.put(pid, ok);
            }
            if (Boolean.TRUE.equals(ok)) filtered.add(it);
        }
        return filtered;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWmsOutboundItem(WmsOutboundItem wmsOutboundItem)
    {
        WmsOutbound doc = assertDraftOutbound(wmsOutboundItem.getOutboundId());
        normalizeItem(wmsOutboundItem);
        int rows = wmsOutboundItemMapper.insertWmsOutboundItem(wmsOutboundItem);
        if (rows > 0) recalculateHeader(doc);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWmsOutboundItem(WmsOutboundItem wmsOutboundItem)
    {
        WmsOutboundItem existed = wmsOutboundItemMapper.selectWmsOutboundItemById(wmsOutboundItem.getOutboundItemId());
        if (existed == null)
        {
            throw new ServiceException("出库单明细不存在");
        }
        WmsOutbound doc = assertDraftOutbound(existed.getOutboundId());
        if (wmsOutboundItem.getOutboundId() != null && !wmsOutboundItem.getOutboundId().equals(existed.getOutboundId()))
        {
            assertDraftOutbound(wmsOutboundItem.getOutboundId());
        }
        normalizeItem(wmsOutboundItem);
        int rows = wmsOutboundItemMapper.updateWmsOutboundItem(wmsOutboundItem);
        if (rows > 0) recalculateHeader(doc);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsOutboundItemById(Long outboundItemId)
    {
        WmsOutboundItem existed = wmsOutboundItemMapper.selectWmsOutboundItemById(outboundItemId);
        WmsOutbound doc = null;
        if (existed != null)
        {
            doc = assertDraftOutbound(existed.getOutboundId());
        }
        int rows = wmsOutboundItemMapper.deleteWmsOutboundItemById(outboundItemId);
        if (rows > 0 && doc != null) recalculateHeader(doc);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsOutboundItemByIds(Long[] outboundItemIds)
    {
        java.util.Map<Long, WmsOutbound> affected = new java.util.LinkedHashMap<Long, WmsOutbound>();
        for (Long id : outboundItemIds)
        {
            WmsOutboundItem existed = wmsOutboundItemMapper.selectWmsOutboundItemById(id);
            if (existed != null)
            {
                WmsOutbound doc = assertDraftOutbound(existed.getOutboundId());
                affected.put(doc.getOutboundId(), doc);
            }
        }
        int rows = wmsOutboundItemMapper.deleteWmsOutboundItemByIds(outboundItemIds);
        if (rows > 0)
        {
            for (WmsOutbound doc : affected.values()) recalculateHeader(doc);
        }
        return rows;
    }

    private void assertOutboundAccess(Long outboundId)
    {
        if (bizScopeHelper == null || outboundId == null) return;
        WmsOutbound doc = wmsOutboundMapper.selectWmsOutboundById(outboundId);
        if (doc != null)
        {
            bizScopeHelper.assertRecordAccess(doc.getDeptId(), doc.getOwnerUserId(), doc.getWarehouseId());
        }
    }

    private WmsOutbound assertDraftOutbound(Long outboundId)
    {
        WmsOutbound doc = outboundId == null ? null : wmsOutboundMapper.selectWmsOutboundById(outboundId);
        if (doc == null)
        {
            throw new ServiceException("关联出库单不存在");
        }
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(doc.getDeptId(), doc.getOwnerUserId(), doc.getWarehouseId());
        }
        if (!"0".equals(doc.getStatus()))
        {
            throw new ServiceException("仅草稿态出库单允许维护明细");
        }
        return doc;
    }

    private void normalizeItem(WmsOutboundItem item)
    {
        BigDecimal qty = item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity();
        if (qty.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("出库明细数量必须大于0");
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

    private void recalculateHeader(WmsOutbound doc)
    {
        WmsOutboundItem q = new WmsOutboundItem();
        q.setOutboundId(doc.getOutboundId());
        List<WmsOutboundItem> list = wmsOutboundItemMapper.selectWmsOutboundItemList(q);
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmt = BigDecimal.ZERO;
        if (list != null)
        {
            for (WmsOutboundItem it : list)
            {
                if (it.getQuantity() != null) totalQty = totalQty.add(it.getQuantity());
                if (it.getAmount() != null) totalAmt = totalAmt.add(it.getAmount());
            }
        }
        doc.setTotalQty(totalQty);
        doc.setTotalAmount(totalAmt);
        wmsOutboundMapper.updateWmsOutbound(doc);
    }
}

