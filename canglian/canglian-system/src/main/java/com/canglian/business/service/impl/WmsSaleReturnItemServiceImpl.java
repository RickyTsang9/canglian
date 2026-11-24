package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.WmsSaleReturn;
import com.canglian.business.domain.WmsSaleReturnItem;
import com.canglian.business.mapper.WmsSaleReturnItemMapper;
import com.canglian.business.mapper.WmsSaleReturnMapper;
import com.canglian.business.service.IMdProductUnitService;
import com.canglian.business.service.IWmsSaleReturnItemService;
import com.canglian.common.exception.ServiceException;

@Service
public class WmsSaleReturnItemServiceImpl implements IWmsSaleReturnItemService
{
    @Autowired
    private WmsSaleReturnItemMapper wmsSaleReturnItemMapper;

    @Autowired
    private WmsSaleReturnMapper wmsSaleReturnMapper;

    @Autowired(required = false)
    private IMdProductUnitService mdProductUnitService;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Override
    public WmsSaleReturnItem selectWmsSaleReturnItemById(Long saleReturnItemId)
    {
        WmsSaleReturnItem item = wmsSaleReturnItemMapper.selectWmsSaleReturnItemById(saleReturnItemId);
        if (item != null && item.getSaleReturnId() != null) assertReturnAccess(item.getSaleReturnId());
        return item;
    }

    @Override
    public List<WmsSaleReturnItem> selectWmsSaleReturnItemList(WmsSaleReturnItem wmsSaleReturnItem)
    {
        if (wmsSaleReturnItem != null && wmsSaleReturnItem.getSaleReturnId() != null)
        {
            assertReturnAccess(wmsSaleReturnItem.getSaleReturnId());
            return wmsSaleReturnItemMapper.selectWmsSaleReturnItemList(wmsSaleReturnItem);
        }
        List<WmsSaleReturnItem> list = wmsSaleReturnItemMapper.selectWmsSaleReturnItemList(wmsSaleReturnItem);
        if (bizScopeHelper == null || list == null || list.isEmpty()) return list;
        java.util.List<WmsSaleReturnItem> filtered = new java.util.ArrayList<WmsSaleReturnItem>();
        java.util.Map<Long, Boolean> cache = new java.util.HashMap<Long, Boolean>();
        for (WmsSaleReturnItem it : list)
        {
            Long pid = it.getSaleReturnId();
            Boolean ok = cache.get(pid);
            if (ok == null)
            {
                WmsSaleReturn doc = pid == null ? null : wmsSaleReturnMapper.selectWmsSaleReturnById(pid);
                ok = doc != null && bizScopeHelper.canAccessRecord(doc.getDeptId(), doc.getOwnerUserId(), doc.getWarehouseId());
                cache.put(pid, ok);
            }
            if (Boolean.TRUE.equals(ok)) filtered.add(it);
        }
        return filtered;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWmsSaleReturnItem(WmsSaleReturnItem wmsSaleReturnItem)
    {
        WmsSaleReturn doc = assertDraftReturn(wmsSaleReturnItem.getSaleReturnId());
        normalizeItem(wmsSaleReturnItem);
        int rows = wmsSaleReturnItemMapper.insertWmsSaleReturnItem(wmsSaleReturnItem);
        if (rows > 0) recalculateHeader(doc);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWmsSaleReturnItem(WmsSaleReturnItem wmsSaleReturnItem)
    {
        WmsSaleReturnItem existed = wmsSaleReturnItemMapper.selectWmsSaleReturnItemById(wmsSaleReturnItem.getSaleReturnItemId());
        if (existed == null) throw new ServiceException("销售退货明细不存在");
        WmsSaleReturn doc = assertDraftReturn(existed.getSaleReturnId());
        if (wmsSaleReturnItem.getSaleReturnId() != null && !wmsSaleReturnItem.getSaleReturnId().equals(existed.getSaleReturnId()))
        {
            assertDraftReturn(wmsSaleReturnItem.getSaleReturnId());
        }
        normalizeItem(wmsSaleReturnItem);
        int rows = wmsSaleReturnItemMapper.updateWmsSaleReturnItem(wmsSaleReturnItem);
        if (rows > 0) recalculateHeader(doc);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsSaleReturnItemById(Long saleReturnItemId)
    {
        WmsSaleReturnItem existed = wmsSaleReturnItemMapper.selectWmsSaleReturnItemById(saleReturnItemId);
        WmsSaleReturn doc = existed == null ? null : assertDraftReturn(existed.getSaleReturnId());
        int rows = wmsSaleReturnItemMapper.deleteWmsSaleReturnItemById(saleReturnItemId);
        if (rows > 0 && doc != null) recalculateHeader(doc);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsSaleReturnItemByIds(Long[] saleReturnItemIds)
    {
        java.util.Map<Long, WmsSaleReturn> affected = new java.util.LinkedHashMap<Long, WmsSaleReturn>();
        for (Long id : saleReturnItemIds)
        {
            WmsSaleReturnItem existed = wmsSaleReturnItemMapper.selectWmsSaleReturnItemById(id);
            if (existed != null)
            {
                WmsSaleReturn doc = assertDraftReturn(existed.getSaleReturnId());
                affected.put(doc.getSaleReturnId(), doc);
            }
        }
        int rows = wmsSaleReturnItemMapper.deleteWmsSaleReturnItemByIds(saleReturnItemIds);
        if (rows > 0) for (WmsSaleReturn doc : affected.values()) recalculateHeader(doc);
        return rows;
    }

    private void assertReturnAccess(Long saleReturnId)
    {
        if (bizScopeHelper == null || saleReturnId == null) return;
        WmsSaleReturn doc = wmsSaleReturnMapper.selectWmsSaleReturnById(saleReturnId);
        if (doc != null) bizScopeHelper.assertRecordAccess(doc.getDeptId(), doc.getOwnerUserId(), doc.getWarehouseId());
    }

    private WmsSaleReturn assertDraftReturn(Long saleReturnId)
    {
        WmsSaleReturn doc = saleReturnId == null ? null : wmsSaleReturnMapper.selectWmsSaleReturnById(saleReturnId);
        if (doc == null) throw new ServiceException("关联销售退货单不存在");
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(doc.getDeptId(), doc.getOwnerUserId(), doc.getWarehouseId());
        if (!"0".equals(doc.getStatus())) throw new ServiceException("仅草稿态销售退货单允许维护明细");
        return doc;
    }

    private void normalizeItem(WmsSaleReturnItem item)
    {
        BigDecimal qty = item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity();
        if (qty.compareTo(BigDecimal.ZERO) <= 0) throw new ServiceException("销售退货明细数量必须大于0");
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

    private void recalculateHeader(WmsSaleReturn doc)
    {
        WmsSaleReturnItem q = new WmsSaleReturnItem();
        q.setSaleReturnId(doc.getSaleReturnId());
        List<WmsSaleReturnItem> list = wmsSaleReturnItemMapper.selectWmsSaleReturnItemList(q);
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmt = BigDecimal.ZERO;
        if (list != null)
        {
            for (WmsSaleReturnItem it : list)
            {
                if (it.getQuantity() != null) totalQty = totalQty.add(it.getQuantity());
                if (it.getAmount() != null) totalAmt = totalAmt.add(it.getAmount());
            }
        }
        doc.setTotalQty(totalQty);
        doc.setTotalAmount(totalAmt);
        wmsSaleReturnMapper.updateWmsSaleReturn(doc);
    }
}

