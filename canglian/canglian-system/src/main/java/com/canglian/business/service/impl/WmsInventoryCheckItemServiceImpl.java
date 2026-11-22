package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.WmsInventoryCheck;
import com.canglian.business.mapper.WmsInventoryCheckItemMapper;
import com.canglian.business.mapper.WmsInventoryCheckMapper;
import com.canglian.business.domain.WmsInventoryCheckItem;
import com.canglian.business.service.IMdProductUnitService;
import com.canglian.business.service.IWmsInventoryCheckItemService;
import com.canglian.common.exception.ServiceException;

@Service
public class WmsInventoryCheckItemServiceImpl implements IWmsInventoryCheckItemService
{
    @Autowired
    private WmsInventoryCheckItemMapper wmsInventoryCheckItemMapper;

    @Autowired
    private WmsInventoryCheckMapper wmsInventoryCheckMapper;

    @Autowired(required = false)
    private IMdProductUnitService mdProductUnitService;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Override
    public WmsInventoryCheckItem selectWmsInventoryCheckItemById(Long checkItemId)
    {
        WmsInventoryCheckItem item = wmsInventoryCheckItemMapper.selectWmsInventoryCheckItemById(checkItemId);
        if (item != null && item.getCheckId() != null) assertCheckAccess(item.getCheckId());
        return item;
    }

    @Override
    public List<WmsInventoryCheckItem> selectWmsInventoryCheckItemList(WmsInventoryCheckItem wmsInventoryCheckItem)
    {
        if (wmsInventoryCheckItem != null && wmsInventoryCheckItem.getCheckId() != null) assertCheckAccess(wmsInventoryCheckItem.getCheckId());
        return wmsInventoryCheckItemMapper.selectWmsInventoryCheckItemList(wmsInventoryCheckItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWmsInventoryCheckItem(WmsInventoryCheckItem wmsInventoryCheckItem)
    {
        WmsInventoryCheck doc = assertDraftCheck(wmsInventoryCheckItem.getCheckId());
        autoFillDiff(wmsInventoryCheckItem);
        int rows = wmsInventoryCheckItemMapper.insertWmsInventoryCheckItem(wmsInventoryCheckItem);
        if (rows > 0) recalculateHeader(doc);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWmsInventoryCheckItem(WmsInventoryCheckItem wmsInventoryCheckItem)
    {
        WmsInventoryCheckItem existed = wmsInventoryCheckItemMapper.selectWmsInventoryCheckItemById(wmsInventoryCheckItem.getCheckItemId());
        if (existed == null) throw new ServiceException("盘点单明细不存在");
        WmsInventoryCheck doc = assertDraftCheck(existed.getCheckId());
        autoFillDiff(wmsInventoryCheckItem);
        int rows = wmsInventoryCheckItemMapper.updateWmsInventoryCheckItem(wmsInventoryCheckItem);
        if (rows > 0) recalculateHeader(doc);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsInventoryCheckItemById(Long checkItemId)
    {
        WmsInventoryCheckItem existed = wmsInventoryCheckItemMapper.selectWmsInventoryCheckItemById(checkItemId);
        WmsInventoryCheck doc = existed == null ? null : assertDraftCheck(existed.getCheckId());
        int rows = wmsInventoryCheckItemMapper.deleteWmsInventoryCheckItemById(checkItemId);
        if (rows > 0 && doc != null) recalculateHeader(doc);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsInventoryCheckItemByIds(Long[] checkItemIds)
    {
        java.util.Map<Long, WmsInventoryCheck> affected = new java.util.LinkedHashMap<Long, WmsInventoryCheck>();
        for (Long id : checkItemIds)
        {
            WmsInventoryCheckItem existed = wmsInventoryCheckItemMapper.selectWmsInventoryCheckItemById(id);
            if (existed != null)
            {
                WmsInventoryCheck doc = assertDraftCheck(existed.getCheckId());
                affected.put(doc.getCheckId(), doc);
            }
        }
        int rows = wmsInventoryCheckItemMapper.deleteWmsInventoryCheckItemByIds(checkItemIds);
        if (rows > 0) for (WmsInventoryCheck doc : affected.values()) recalculateHeader(doc);
        return rows;
    }

    private void assertCheckAccess(Long checkId)
    {
        if (bizScopeHelper == null || checkId == null) return;
        WmsInventoryCheck doc = wmsInventoryCheckMapper.selectWmsInventoryCheckById(checkId);
        if (doc != null) bizScopeHelper.assertRecordAccess(doc.getDeptId(), doc.getOwnerUserId(), doc.getWarehouseId());
    }

    private WmsInventoryCheck assertDraftCheck(Long checkId)
    {
        WmsInventoryCheck doc = checkId == null ? null : wmsInventoryCheckMapper.selectWmsInventoryCheckById(checkId);
        if (doc == null) throw new ServiceException("关联盘点单不存在");
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(doc.getDeptId(), doc.getOwnerUserId(), doc.getWarehouseId());
        if (!"0".equals(doc.getStatus())) throw new ServiceException("仅草稿态盘点单允许维护明细");
        return doc;
    }

    private void autoFillDiff(WmsInventoryCheckItem item)
    {
        BigDecimal stockQty = item.getStockQty() == null ? BigDecimal.ZERO : item.getStockQty();
        BigDecimal actualQty = item.getActualQty() == null ? BigDecimal.ZERO : item.getActualQty();
        BigDecimal ratio = mdProductUnitService != null && item.getProductId() != null
            ? mdProductUnitService.resolveConvertRatio(item.getProductId(), item.getUnitName())
            : (item.getConvertRatio() != null && item.getConvertRatio().compareTo(BigDecimal.ZERO) > 0 ? item.getConvertRatio() : BigDecimal.ONE);
        BigDecimal diffQty = actualQty.subtract(stockQty);
        BigDecimal baseDiffQty = diffQty.multiply(ratio).setScale(4, RoundingMode.HALF_UP);
        item.setConvertRatio(ratio);
        item.setDiffQty(diffQty);
        item.setBaseDiffQty(baseDiffQty);
        if (item.getBatchNo() != null) item.setBatchNo(item.getBatchNo().trim());
        if (item.getPrice() != null)
        {
            item.setDiffAmount(baseDiffQty.multiply(item.getPrice()).setScale(2, RoundingMode.HALF_UP));
        }
    }

    private void recalculateHeader(WmsInventoryCheck doc)
    {
        WmsInventoryCheckItem q = new WmsInventoryCheckItem();
        q.setCheckId(doc.getCheckId());
        List<WmsInventoryCheckItem> list = wmsInventoryCheckItemMapper.selectWmsInventoryCheckItemList(q);
        BigDecimal totalDiffQty = BigDecimal.ZERO;
        BigDecimal totalDiffAmt = BigDecimal.ZERO;
        if (list != null)
        {
            for (WmsInventoryCheckItem it : list)
            {
                if (it.getBaseDiffQty() != null) totalDiffQty = totalDiffQty.add(it.getBaseDiffQty());
                else if (it.getDiffQty() != null) totalDiffQty = totalDiffQty.add(it.getDiffQty());
                if (it.getDiffAmount() != null) totalDiffAmt = totalDiffAmt.add(it.getDiffAmount());
            }
        }
        doc.setTotalDiffQty(totalDiffQty);
        doc.setTotalDiffAmount(totalDiffAmt);
        wmsInventoryCheckMapper.updateWmsInventoryCheck(doc);
    }
}

