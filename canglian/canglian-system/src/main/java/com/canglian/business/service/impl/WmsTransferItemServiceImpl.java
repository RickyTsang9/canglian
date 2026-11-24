package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.WmsTransfer;
import com.canglian.business.domain.WmsTransferItem;
import com.canglian.business.mapper.WmsTransferItemMapper;
import com.canglian.business.mapper.WmsTransferMapper;
import com.canglian.business.service.IMdProductUnitService;
import com.canglian.business.service.IWmsTransferItemService;
import com.canglian.common.exception.ServiceException;

@Service
public class WmsTransferItemServiceImpl implements IWmsTransferItemService
{
    @Autowired
    private WmsTransferItemMapper wmsTransferItemMapper;

    @Autowired
    private WmsTransferMapper wmsTransferMapper;

    @Autowired(required = false)
    private IMdProductUnitService mdProductUnitService;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Override
    public WmsTransferItem selectWmsTransferItemById(Long transferItemId)
    {
        WmsTransferItem item = wmsTransferItemMapper.selectWmsTransferItemById(transferItemId);
        if (item != null && item.getTransferId() != null) assertTransferAccess(item.getTransferId());
        return item;
    }

    @Override
    public List<WmsTransferItem> selectWmsTransferItemList(WmsTransferItem wmsTransferItem)
    {
        if (wmsTransferItem != null && wmsTransferItem.getTransferId() != null)
        {
            assertTransferAccess(wmsTransferItem.getTransferId());
            return wmsTransferItemMapper.selectWmsTransferItemList(wmsTransferItem);
        }
        List<WmsTransferItem> list = wmsTransferItemMapper.selectWmsTransferItemList(wmsTransferItem);
        if (bizScopeHelper == null || list == null || list.isEmpty()) return list;
        java.util.List<WmsTransferItem> filtered = new java.util.ArrayList<WmsTransferItem>();
        java.util.Map<Long, Boolean> cache = new java.util.HashMap<Long, Boolean>();
        for (WmsTransferItem it : list)
        {
            Long pid = it.getTransferId();
            Boolean ok = cache.get(pid);
            if (ok == null)
            {
                WmsTransfer doc = pid == null ? null : wmsTransferMapper.selectWmsTransferById(pid);
                ok = doc != null && (bizScopeHelper.canAccessRecord(doc.getDeptId(), doc.getOwnerUserId(), doc.getOutWarehouseId())
                    || bizScopeHelper.canAccessRecord(doc.getDeptId(), doc.getOwnerUserId(), doc.getInWarehouseId()));
                cache.put(pid, ok);
            }
            if (Boolean.TRUE.equals(ok)) filtered.add(it);
        }
        return filtered;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWmsTransferItem(WmsTransferItem wmsTransferItem)
    {
        WmsTransfer doc = assertDraftTransfer(wmsTransferItem.getTransferId());
        normalizeItem(wmsTransferItem);
        int rows = wmsTransferItemMapper.insertWmsTransferItem(wmsTransferItem);
        if (rows > 0) recalculateHeader(doc);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWmsTransferItem(WmsTransferItem wmsTransferItem)
    {
        WmsTransferItem existed = wmsTransferItemMapper.selectWmsTransferItemById(wmsTransferItem.getTransferItemId());
        if (existed == null) throw new ServiceException("调拨单明细不存在");
        WmsTransfer doc = assertDraftTransfer(existed.getTransferId());
        if (wmsTransferItem.getTransferId() != null && !wmsTransferItem.getTransferId().equals(existed.getTransferId()))
        {
            assertDraftTransfer(wmsTransferItem.getTransferId());
        }
        normalizeItem(wmsTransferItem);
        int rows = wmsTransferItemMapper.updateWmsTransferItem(wmsTransferItem);
        if (rows > 0) recalculateHeader(doc);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsTransferItemById(Long transferItemId)
    {
        WmsTransferItem existed = wmsTransferItemMapper.selectWmsTransferItemById(transferItemId);
        WmsTransfer doc = existed == null ? null : assertDraftTransfer(existed.getTransferId());
        int rows = wmsTransferItemMapper.deleteWmsTransferItemById(transferItemId);
        if (rows > 0 && doc != null) recalculateHeader(doc);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsTransferItemByIds(Long[] transferItemIds)
    {
        java.util.Map<Long, WmsTransfer> affected = new java.util.LinkedHashMap<Long, WmsTransfer>();
        for (Long id : transferItemIds)
        {
            WmsTransferItem existed = wmsTransferItemMapper.selectWmsTransferItemById(id);
            if (existed != null)
            {
                WmsTransfer doc = assertDraftTransfer(existed.getTransferId());
                affected.put(doc.getTransferId(), doc);
            }
        }
        int rows = wmsTransferItemMapper.deleteWmsTransferItemByIds(transferItemIds);
        if (rows > 0) for (WmsTransfer doc : affected.values()) recalculateHeader(doc);
        return rows;
    }

    private void assertTransferAccess(Long transferId)
    {
        if (bizScopeHelper == null || transferId == null) return;
        WmsTransfer doc = wmsTransferMapper.selectWmsTransferById(transferId);
        if (doc != null)
        {
            bizScopeHelper.assertRecordAccess(doc.getDeptId(), doc.getOwnerUserId(), null);
        }
    }

    private WmsTransfer assertDraftTransfer(Long transferId)
    {
        WmsTransfer doc = transferId == null ? null : wmsTransferMapper.selectWmsTransferById(transferId);
        if (doc == null) throw new ServiceException("关联调拨单不存在");
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(doc.getDeptId(), doc.getOwnerUserId(), doc.getOutWarehouseId());
            bizScopeHelper.assertWarehouseAccess(doc.getInWarehouseId());
        }
        if (!"0".equals(doc.getStatus())) throw new ServiceException("仅草稿态调拨单允许维护明细");
        return doc;
    }

    private void normalizeItem(WmsTransferItem item)
    {
        BigDecimal qty = item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity();
        if (qty.compareTo(BigDecimal.ZERO) <= 0) throw new ServiceException("调拨明细数量必须大于0");
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

    private void recalculateHeader(WmsTransfer doc)
    {
        WmsTransferItem q = new WmsTransferItem();
        q.setTransferId(doc.getTransferId());
        List<WmsTransferItem> list = wmsTransferItemMapper.selectWmsTransferItemList(q);
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmt = BigDecimal.ZERO;
        if (list != null)
        {
            for (WmsTransferItem it : list)
            {
                if (it.getQuantity() != null) totalQty = totalQty.add(it.getQuantity());
                if (it.getAmount() != null) totalAmt = totalAmt.add(it.getAmount());
            }
        }
        doc.setTotalQty(totalQty);
        doc.setTotalAmount(totalAmt);
        wmsTransferMapper.updateWmsTransfer(doc);
    }
}

