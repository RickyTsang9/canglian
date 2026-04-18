package com.canglian.business.service;

import java.math.BigDecimal;

/**
 * 价格解析服务接口
 * 
 * @author canglian
 */
public interface IPriceResolverService
{
    public BigDecimal resolveSalePrice(Long customerId, Long productId);

    public void saveRecentSalePrice(Long customerId, Long productId, BigDecimal salePrice, String sourceBillType, Long sourceBillId, java.util.Date businessDate, String operator);
}
