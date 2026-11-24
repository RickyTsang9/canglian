package com.canglian.business.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.WmsSerialNo;

/**
 * 商品序列号(SN)全生命周期服务接口（含 WMS 全单据强管控联动）
 *
 * @author canglian
 */
public interface IWmsSerialNoService
{
    public List<WmsSerialNo> selectWmsSerialNoList(WmsSerialNo query);

    public Map<String, Object> traceBySnCode(String snCode);

    public List<WmsSerialNo> batchRegister(Long productId, Long warehouseId, Long locationId, String batchNo,
                                           Long inboundId, String billNo, Date warrantyEndDate,
                                           List<String> snCodes, String remark, String operator);

    public int consumeSerialNos(List<String> snCodes, Long outboundId, String billNo, Long customerId, String remark, String operator);

    public int returnSerialNos(List<String> snCodes, Long warehouseId, Long locationId, Long saleReturnId, String billNo, String remark, String operator);

    public int deleteWmsSerialNoByIds(Long[] serialIds);

    /** 解析逗号/换行/空格分隔的 SN 清单 */
    public List<String> parseSnCodes(String rawSnCodes);

    /** 入库审核强管控：若商品启用序列号，必须录满 baseQty 个唯一 SN 并自动入库建档 */
    public void handleInboundSerials(MdProduct product, Long warehouseId, Long locationId, String batchNo,
                                     Long inboundId, String billNo, Date expiryDate, String rawSnCodes,
                                     BigDecimal baseQty, String operator);

    /** 入库反审核/红冲回滚序列号（必须全部仍在库才允许撤销入库） */
    public void rollbackInboundSerials(MdProduct product, Long inboundId, String billNo, String rawSnCodes,
                                       boolean deleteRecord, String operator);

    /** 出库审核强管控：若商品启用序列号，必须录满 baseQty 个在库 SN 并核销出库 */
    public void handleOutboundSerials(MdProduct product, Long warehouseId, Long outboundId, String billNo,
                                      Long customerId, String rawSnCodes, BigDecimal baseQty, String operator);

    /** 出库审核强管控（含库位与批次一致性校验） */
    public void handleOutboundSerials(MdProduct product, Long warehouseId, Long locationId, String batchNo,
                                      Long outboundId, String billNo, Long customerId, String rawSnCodes,
                                      BigDecimal baseQty, String operator);

    /** 出库反审核/红冲/整单退货回滚序列号至在库 */
    public void rollbackOutboundSerials(MdProduct product, Long warehouseId, Long locationId, Long outboundId,
                                        String billNo, String rawSnCodes, String remark, String operator);

    /** 调拨审核强管控：将足额在库 SN 调拨至目标仓/库位 */
    public void handleTransferSerials(MdProduct product, Long outWarehouseId, Long inWarehouseId, Long inLocationId,
                                      Long transferId, String billNo, String rawSnCodes, BigDecimal baseQty, String operator);

    /** 调拨审核强管控（含调出库位与批次一致性校验） */
    public void handleTransferSerials(MdProduct product, Long outWarehouseId, Long outLocationId, String batchNo,
                                      Long inWarehouseId, Long inLocationId, Long transferId, String billNo,
                                      String rawSnCodes, BigDecimal baseQty, String operator);

    /** 采购退货审核强管控：核验足额在库 SN 并置为 returned（已退供应商） */
    public void handlePurchaseReturnSerials(MdProduct product, Long warehouseId, Long returnId, String billNo,
                                            String rawSnCodes, BigDecimal baseQty, String operator);

    /** 采购退货审核强管控（含库位与批次一致性校验） */
    public void handlePurchaseReturnSerials(MdProduct product, Long warehouseId, Long locationId, String batchNo,
                                            Long returnId, String billNo, String rawSnCodes, BigDecimal baseQty, String operator);

    /** 采购退货反审/红冲回滚 SN 至在库 */
    public void rollbackPurchaseReturnSerials(MdProduct product, Long warehouseId, Long locationId, Long returnId,
                                              String billNo, String rawSnCodes, String operator);

    /** 销售退货审核强管控：核验足额已出库 SN 并回库至在库 */
    public void handleSaleReturnSerials(MdProduct product, Long warehouseId, Long locationId, Long saleReturnId,
                                        String billNo, String rawSnCodes, BigDecimal baseQty, String operator);

    /** 销售退货审核强管控（含来源出库单与客户归属校验） */
    public void handleSaleReturnSerials(MdProduct product, Long warehouseId, Long locationId, Long sourceOutboundId,
                                        Long customerId, Long saleReturnId, String billNo, String rawSnCodes,
                                        BigDecimal baseQty, String operator);

    /** 销售退货反审/红冲回滚 SN 至已出库 */
    public void rollbackSaleReturnSerials(MdProduct product, Long saleReturnId, String billNo, Long customerId,
                                          String rawSnCodes, String operator);

    /** 盘点审核强管控：盘盈登记足额唯一 SN，盘亏核销足额在库 SN */
    public void handleInventoryCheckSerials(MdProduct product, Long warehouseId, Long locationId, String batchNo,
                                            Long checkId, String billNo, Date expiryDate, String rawSnCodes,
                                            BigDecimal baseDiffQty, String operator);

    /** 盘点反审/红冲回滚 SN */
    public void rollbackInventoryCheckSerials(MdProduct product, Long warehouseId, Long locationId, Long checkId,
                                              String billNo, String rawSnCodes, BigDecimal originalBaseDiffQty,
                                              boolean deleteRecord, String operator);
}
