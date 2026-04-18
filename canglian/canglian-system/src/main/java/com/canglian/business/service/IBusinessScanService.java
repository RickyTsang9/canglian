package com.canglian.business.service;

import java.util.Map;
import com.canglian.business.domain.SalOrder;
import com.canglian.business.domain.ScanInventoryCheckRequest;
import com.canglian.business.domain.ScanQuickSaleRequest;
import com.canglian.business.domain.ScanTransferRequest;
import com.canglian.business.domain.WmsInventoryCheck;
import com.canglian.business.domain.WmsTransfer;

/**
 * 业务扫码服务接口
 * 
 * @author canglian
 */
public interface IBusinessScanService
{
    /**
     * 按条码解析商品
     * 
     * @param barCode 商品条码
     * @return 商品扫码结果
     */
    public Map<String, Object> scanProduct(String barCode);

    /**
     * 按库位码解析库位
     * 
     * @param locationCode 库位编码
     * @return 库位扫码结果
     */
    public Map<String, Object> scanLocation(String locationCode);

    /**
     * 查询扫码库存结果
     * 
     * @param barCode 商品条码
     * @param warehouseId 仓库编号
     * @param locationCode 库位编码
     * @return 库存扫码结果
     */
    public Map<String, Object> scanStock(String barCode, Long warehouseId, String locationCode);

    /**
     * 扫码快速生成销货订单
     * 
     * @param scanQuickSaleRequest 扫码开单请求
     * @param operator 操作人
     * @return 销货订单
     */
    public SalOrder createQuickSaleOrder(ScanQuickSaleRequest scanQuickSaleRequest, String operator);

    /**
     * 扫码快速生成盘点单
     * 
     * @param scanInventoryCheckRequest 扫码盘点请求
     * @param operator 操作人
     * @return 盘点单
     */
    public WmsInventoryCheck createInventoryCheck(ScanInventoryCheckRequest scanInventoryCheckRequest, String operator);

    /**
     * 扫码快速生成调拨单
     * 
     * @param scanTransferRequest 扫码调拨请求
     * @param operator 操作人
     * @return 调拨单
     */
    public WmsTransfer createTransfer(ScanTransferRequest scanTransferRequest, String operator);
}
