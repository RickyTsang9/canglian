package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.WmsTransferItem;

public interface IWmsTransferItemService
{
    public WmsTransferItem selectWmsTransferItemById(Long transferItemId);

    public List<WmsTransferItem> selectWmsTransferItemList(WmsTransferItem wmsTransferItem);

    public int insertWmsTransferItem(WmsTransferItem wmsTransferItem);

    public int updateWmsTransferItem(WmsTransferItem wmsTransferItem);

    public int deleteWmsTransferItemById(Long transferItemId);

    public int deleteWmsTransferItemByIds(Long[] transferItemIds);
}

