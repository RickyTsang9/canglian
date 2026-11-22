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

    public int auditWmsTransfer(Long transferId, String operator);

    /** 反审核调拨单（调入仓回退至调出仓并恢复草稿） */
    public int unauditWmsTransfer(Long transferId, String operator);

    /** 一键红冲调拨单（生成反向红冲调拨单） */
    public WmsTransfer reverseWmsTransfer(Long transferId, String operator);
}
