package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.WmsPurchaseReturn;
import com.canglian.business.domain.WmsPurchaseReturnItem;
import com.canglian.business.mapper.WmsPurchaseReturnItemMapper;
import com.canglian.business.mapper.WmsPurchaseReturnMapper;
import com.canglian.business.service.IMdProductUnitService;
import com.canglian.business.service.IWmsPurchaseReturnItemService;
import com.canglian.common.exception.ServiceException;

@Service
public class WmsPurchaseReturnItemServiceImpl implements IWmsPurchaseReturnItemService
{
    @Autowired
    private WmsPurchaseReturnItemMapper wmsPurchaseReturnItemMapper;

    @Autowired
    private WmsPurchaseReturnMapper wmsPurchaseReturnMapper;

    @Autowired(required = false)
    private IMdProductUnitService mdProductUnitService;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Override
    public WmsPurchaseReturnItem selectWmsPurchaseReturnItemById(Long purchaseReturnItemId)
    {
        WmsPurchaseReturnItem item = wmsPurchaseReturnItemMapper.selectWmsPurchaseReturnItemById(purchaseReturnItemId);
        if (item != null && item.getPurchaseReturnId() != null) assertReturnAccess(item.getPurchaseReturnId());
        return item;
    }

    @Override
    public List<WmsPurchaseReturnItem> selectWmsPurchaseReturnItemList(WmsPurchaseReturnItem wmsPurchaseReturnItem)
    {
        if (wmsPurchaseReturnItem != null && wmsPurchaseReturnItem.getPurchaseReturnId() != null) assertReturnAccess(wmsPurchaseReturnItem.getPurchaseReturnId());
        return wmsPurchaseReturnItemMapper.selectWmsPurchaseReturnItemList(wmsPurchaseReturnItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWmsPurchaseReturnItem(WmsPurchaseReturnItem wmsPurchaseReturnItem)
    {
        WmsPurchaseReturn doc = assertDraftReturn(wmsPurchaseReturnItem.getPurchaseReturnId());
        normalizeItem(wmsPurchaseReturnItem);
        int rows = wmsPurchaseReturnItemMapper.insertWmsPurchaseReturnItem(wmsPurchaseReturnItem);
        if (rows > 0) recalculateHeader(doc);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWmsPurchaseReturnItem(WmsPurchaseReturnItem wmsPurchaseReturnItem)
    {
        WmsPurchaseReturnItem existed = wmsPurchaseReturnItemMapper.selectWmsPurchaseReturnItemById(wmsPurchaseReturnItem.getPurchaseReturnItemId());
        if (existed == null) throw new ServiceException("采购退货明细不存在");
        WmsPurchaseReturn doc = assertDraftReturn(existed.getPurchaseReturnId());
        normalizeItem(wmsPurchaseReturnItem);
        int rows = wmsPurchaseReturnItemMapper.updateWmsPurchaseReturnItem(wmsPurchaseReturnItem);
        if (rows > 0) recalculateHeader(doc);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsPurchaseReturnItemById(Long purchaseReturnItemId)
    {
        WmsPurchaseReturnItem existed = wmsPurchaseReturnItemMapper.selectWmsPurchaseReturnItemById(purchaseReturnItemId);
        WmsPurchaseReturn doc = existed == null ? null : assertDraftReturn(existed.getPurchaseReturnId());
        int rows = wmsPurchaseReturnItemMapper.deleteWmsPurchaseReturnItemById(purchaseReturnItemId);
        if (rows > 0 && doc != null) recalculateHeader(doc);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsPurchaseReturnItemByIds(Long[] purchaseReturnItemIds)
    {
        java.util.Map<Long, WmsPurchaseReturn> affected = new java.util.LinkedHashMap<Long, WmsPurchaseReturn>();
        for (Long id : purchaseReturnItemIds)
        {
            WmsPurchaseReturnItem existed = wmsPurchaseReturnItemMapper.selectWmsPurchaseReturnItemById(id);
            if (existed != null)
            {
                WmsPurchaseReturn doc = assertDraftReturn(existed.getPurchaseReturnId());
                affected.put(doc.getPurchaseReturnId(), doc);
            }
        }
        int rows = wmsPurchaseReturnItemMapper.deleteWmsPurchaseReturnItemByIds(purchaseReturnItemIds);
        if (rows > 0) for (WmsPurchaseReturn doc : affected.values()) recalculateHeader(doc);
        return rows;
    }

    private void assertReturnAccess(Long purchaseReturnId)
    {
        if (bizScopeHelper == null || purchaseReturnId == null) return;
        WmsPurchaseReturn doc = wmsPurchaseReturnMapper.selectWmsPurchaseReturnById(purchaseReturnId);
        if (doc != null) bizScopeHelper.assertRecordAccess(doc.getDeptId(), doc.getOwnerUserId(), doc.getWarehouseId());
    }

    private WmsPurchaseReturn assertDraftReturn(Long purchaseReturnId)
    {
        WmsPurchaseReturn doc = purchaseReturnId == null ? null : wmsPurchaseReturnMapper.selectWmsPurchaseReturnById(purchaseReturnId);
        if (doc == null) throw new ServiceException("关联采购退货单不存在");
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(doc.getDeptId(), doc.getOwnerUserId(), doc.getWarehouseId());
        if (!"0".equals(doc.getStatus())) throw new ServiceException("仅草稿态采购退货单允许维护明细");
        return doc;
    }

    private void normalizeItem(WmsPurchaseReturnItem item)
    {
        BigDecimal qty = item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity();
        if (qty.compareTo(BigDecimal.ZERO) <= 0) throw new ServiceException("采购退货明细数量必须大于0");
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

    private void recalculateHeader(WmsPurchaseReturn doc)
    {
        WmsPurchaseReturnItem q = new WmsPurchaseReturnItem();
        q.setPurchaseReturnId(doc.getPurchaseReturnId());
        List<WmsPurchaseReturnItem> list = wmsPurchaseReturnItemMapper.selectWmsPurchaseReturnItemList(q);
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmt = BigDecimal.ZERO;
        if (list != null)
        {
            for (WmsPurchaseReturnItem it : list)
            {
                if (it.getQuantity() != null) totalQty = totalQty.add(it.getQuantity());
                if (it.getAmount() != null) totalAmt = totalAmt.add(it.getAmount());
            }
        }
        doc.setTotalQty(totalQty);
        doc.setTotalAmount(totalAmt);
        wmsPurchaseReturnMapper.updateWmsPurchaseReturn(doc);
    }
}

