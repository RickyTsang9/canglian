package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.WmsTransferItem;
import com.canglian.business.mapper.WmsTransferItemMapper;
import com.canglian.business.service.IWmsTransferItemService;

@Service
public class WmsTransferItemServiceImpl implements IWmsTransferItemService
{
    @Autowired
    private WmsTransferItemMapper wmsTransferItemMapper;

    @Override
    public WmsTransferItem selectWmsTransferItemById(Long transferItemId)
    {
        return wmsTransferItemMapper.selectWmsTransferItemById(transferItemId);
    }

    @Override
    public List<WmsTransferItem> selectWmsTransferItemList(WmsTransferItem wmsTransferItem)
    {
        return wmsTransferItemMapper.selectWmsTransferItemList(wmsTransferItem);
    }

    @Override
    public int insertWmsTransferItem(WmsTransferItem wmsTransferItem)
    {
        return wmsTransferItemMapper.insertWmsTransferItem(wmsTransferItem);
    }

    @Override
    public int updateWmsTransferItem(WmsTransferItem wmsTransferItem)
    {
        return wmsTransferItemMapper.updateWmsTransferItem(wmsTransferItem);
    }

    @Override
    public int deleteWmsTransferItemById(Long transferItemId)
    {
        return wmsTransferItemMapper.deleteWmsTransferItemById(transferItemId);
    }

    @Override
    public int deleteWmsTransferItemByIds(Long[] transferItemIds)
    {
        return wmsTransferItemMapper.deleteWmsTransferItemByIds(transferItemIds);
    }
}

