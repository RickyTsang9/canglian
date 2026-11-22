package com.canglian.business.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 价格解析服务接口
 *
 * @author canglian
 */
public interface IPriceResolverService
{
    public BigDecimal resolveSalePrice(Long customerId, Long productId);

    /**
     * 按商品与单位解析采购单价（优先匹配包装单位采购价，其次基准采购价 × 换算率）
     */
    public BigDecimal resolvePurchasePrice(Long productId, String unitName);

    /**
     * 解析销售价格并附带命中的价格来源（用于前端开单实时展示价格策略标签）
     */
    public Map<String, Object> resolveSalePriceDetail(Long customerId, Long productId, String unitName);

    /**
     * 保存最近成交价（统一换算为基准单位单价，杜绝多单位复乘膨胀）
     */
    public void saveRecentSalePrice(Long customerId, Long productId, BigDecimal salePrice, BigDecimal convertRatio, String sourceBillType, Long sourceBillId, Date businessDate, String operator);

    /**
     * 兼容旧签名的保存最近成交价（按基准单位单价写入）
     */
    public void saveRecentSalePrice(Long customerId, Long productId, BigDecimal salePrice, String sourceBillType, Long sourceBillId, Date businessDate, String operator);

    /**
     * 出库反审核时回滚由该单据写入的最近成交价
     */
    public void rollbackRecentSalePrice(String sourceBillType, Long sourceBillId);
}
