package com.canglian.business.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.canglian.business.domain.WmsStockLock;

/**
 * 销售订单库存锁库明细台账 Mapper
 *
 * @author canglian
 */
public interface WmsStockLockMapper
{
    public List<WmsStockLock> selectActiveLocksByOrderAndProductForUpdate(@Param("orderId") Long orderId, @Param("productId") Long productId);

    public List<WmsStockLock> selectActiveLocksByOrderForUpdate(@Param("orderId") Long orderId);

    public int insertWmsStockLock(WmsStockLock lock);

    public int updateWmsStockLock(WmsStockLock lock);
}
