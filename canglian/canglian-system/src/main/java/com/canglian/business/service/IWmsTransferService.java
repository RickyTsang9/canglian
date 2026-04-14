package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.WmsTransfer;

public interface IWmsTransferService
{
    public WmsTransfer selectWmsTransferById(Long transferId);

    public List<WmsTransfer> selectWmsTransferList(WmsTransfer wmsTransfer);

    public int insertWmsTransfer(WmsTransfer wmsTransfer);

    public int updateWmsTransfer(WmsTransfer wmsTransfer);

    public int deleteWmsTransferById(Long transferId);

    public int deleteWmsTransferByIds(Long[] transferIds);

    /**
     * 调拨单审核
     * 
     * @param transferId 调拨单id
     * @param operator 操作人
     * @return 结果
     */
    public int auditWmsTransfer(Long transferId, String operator);
}

