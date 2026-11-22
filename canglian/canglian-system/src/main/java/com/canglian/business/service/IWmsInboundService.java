package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.WmsInbound;

/**
 * 入库单 服务层
 *
 * @author canglian
 */
public interface IWmsInboundService
{
    public WmsInbound selectWmsInboundById(Long inboundId);

    public List<WmsInbound> selectWmsInboundList(WmsInbound wmsInbound);

    public int insertWmsInbound(WmsInbound wmsInbound);

    public int updateWmsInbound(WmsInbound wmsInbound);

    public int deleteWmsInboundById(Long inboundId);

    public int deleteWmsInboundByIds(Long[] inboundIds);

    public int auditWmsInbound(Long inboundId, String operator);

    /** 反审核入库单（开放账期、无下游退货/已付款/已生成凭证，回退库存成本并恢复草稿） */
    public int unauditWmsInbound(Long inboundId, String operator);

    /** 一键红冲入库单（生成负数红冲入库单与红字应付） */
    public WmsInbound reverseWmsInbound(Long inboundId, String operator);
}
