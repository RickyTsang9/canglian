package com.canglian.business.service;

import java.math.BigDecimal;
import java.util.List;
import com.canglian.business.domain.WmsOutbound;

/**
 * 出库单 服务层
 *
 * @author canglian
 */
public interface IWmsOutboundService
{
    public WmsOutbound selectWmsOutboundById(Long outboundId);

    public List<WmsOutbound> selectWmsOutboundList(WmsOutbound wmsOutbound);

    public int insertWmsOutbound(WmsOutbound wmsOutbound);

    public int updateWmsOutbound(WmsOutbound wmsOutbound);

    public int deleteWmsOutboundById(Long outboundId);

    public int deleteWmsOutboundByIds(Long[] outboundIds);

    public int auditWmsOutbound(Long outboundId, String operator);

    /** 反审核出库单（开放账期、未退货、无下游已收款/已生成凭证，回补库存并恢复草稿） */
    public int unauditWmsOutbound(Long outboundId, String operator);

    /** 一键红冲出库单（生成负数红冲出库单、回补库存/成本并生成红字应收） */
    public WmsOutbound reverseWmsOutbound(Long outboundId, String operator);

    public int shipWmsOutbound(Long outboundId, WmsOutbound wmsOutbound, String operator);

    public int signWmsOutbound(Long outboundId, String operator);

    public int returnWmsOutbound(Long outboundId, BigDecimal refundAmount, Long receivableId, String operator);

    public int returnWmsOutbound(Long outboundId, BigDecimal refundAmount, Long receivableId, Long fundAccountId, String operator);
}
