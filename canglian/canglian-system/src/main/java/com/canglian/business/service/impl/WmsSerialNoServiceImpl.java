package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.WmsSerialNo;
import com.canglian.business.domain.WmsSerialNoLog;
import com.canglian.business.mapper.WmsSerialNoMapper;
import com.canglian.business.service.IWmsSerialNoService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 商品序列号(SN)全生命周期服务实现（含入库/出库/调拨/采退/销退强管控联动与反审红冲回滚）
 *
 * @author canglian
 */
@Service
public class WmsSerialNoServiceImpl implements IWmsSerialNoService
{
    @Autowired
    private WmsSerialNoMapper wmsSerialNoMapper;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Override
    public List<WmsSerialNo> selectWmsSerialNoList(WmsSerialNo query)
    {
        if (bizScopeHelper != null)
        {
            bizScopeHelper.applyScopeFilter(query, "", false, true);
        }
        return wmsSerialNoMapper.selectWmsSerialNoList(query);
    }

    @Override
    public Map<String, Object> traceBySnCode(String snCode)
    {
        if (StringUtils.isEmpty(snCode))
        {
            throw new ServiceException("序列号不能为空");
        }
        WmsSerialNo serial = wmsSerialNoMapper.selectWmsSerialNoByCode(snCode.trim());
        if (serial == null)
        {
            throw new ServiceException("未查询到序列号档案：" + snCode);
        }
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(serial.getWarehouseId());
        }
        List<WmsSerialNoLog> logs = wmsSerialNoMapper.selectLogsBySnCode(serial.getSnCode());
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("serial", serial);
        result.put("logs", logs);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<WmsSerialNo> batchRegister(Long productId, Long warehouseId, Long locationId, String batchNo,
                                           Long inboundId, String billNo, Date warrantyEndDate,
                                           List<String> snCodes, String remark, String operator)
    {
        if (productId == null) throw new ServiceException("商品ID不能为空");
        if (warehouseId == null) throw new ServiceException("仓库ID不能为空");
        if (bizScopeHelper != null) bizScopeHelper.assertWarehouseAccess(warehouseId);
        Set<String> cleanCodes = normalizeCodes(snCodes);
        if (cleanCodes.isEmpty()) throw new ServiceException("请至少录入一个有效的唯一序列号(SN)");

        List<WmsSerialNo> created = new ArrayList<WmsSerialNo>();
        for (String code : cleanCodes)
        {
            WmsSerialNo existing = wmsSerialNoMapper.selectWmsSerialNoByCode(code);
            if (existing != null)
            {
                throw new ServiceException("序列号已存在，禁止重复登记：" + code);
            }
            WmsSerialNo sn = new WmsSerialNo();
            sn.setSnCode(code);
            sn.setProductId(productId);
            sn.setWarehouseId(warehouseId);
            sn.setLocationId(locationId == null ? 0L : locationId);
            sn.setBatchNo(batchNo == null ? "" : batchNo.trim());
            sn.setStatus("in_stock");
            sn.setInboundId(inboundId);
            sn.setWarrantyEndDate(warrantyEndDate);
            sn.setRemark(remark);
            sn.setCreateBy(operator);
            wmsSerialNoMapper.insertWmsSerialNo(sn);
            writeLog(sn, "inbound", inboundId != null ? "inbound" : "sn_register", inboundId, billNo, warehouseId, null,
                StringUtils.isNotEmpty(remark) ? remark : "序列号入库登记", operator);
            created.add(sn);
        }
        return created;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int consumeSerialNos(List<String> snCodes, Long outboundId, String billNo, Long customerId, String remark, String operator)
    {
        Set<String> cleanCodes = normalizeCodes(snCodes);
        if (cleanCodes.isEmpty()) throw new ServiceException("请指定要出库核销的序列号");
        int count = 0;
        for (String code : cleanCodes)
        {
            WmsSerialNo sn = wmsSerialNoMapper.selectWmsSerialNoByCode(code);
            if (sn == null) throw new ServiceException("序列号不存在：" + code);
            if (!"in_stock".equals(sn.getStatus()))
            {
                throw new ServiceException("序列号 " + code + " 当前状态(" + sn.getStatus() + ")不在库，无法出库");
            }
            if (bizScopeHelper != null) bizScopeHelper.assertWarehouseAccess(sn.getWarehouseId());
            sn.setStatus("out_stock");
            sn.setOutboundId(outboundId);
            sn.setCustomerId(customerId);
            sn.setUpdateBy(operator);
            wmsSerialNoMapper.updateWmsSerialNo(sn);
            writeLog(sn, "outbound", "outbound", outboundId, billNo, sn.getWarehouseId(), customerId,
                StringUtils.isNotEmpty(remark) ? remark : "序列号出库核销", operator);
            count++;
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int returnSerialNos(List<String> snCodes, Long warehouseId, Long locationId, Long saleReturnId, String billNo, String remark, String operator)
    {
        Set<String> cleanCodes = normalizeCodes(snCodes);
        if (cleanCodes.isEmpty()) throw new ServiceException("请指定要退货回库的序列号");
        int count = 0;
        for (String code : cleanCodes)
        {
            WmsSerialNo sn = wmsSerialNoMapper.selectWmsSerialNoByCode(code);
            if (sn == null) throw new ServiceException("序列号不存在：" + code);
            if (!"out_stock".equals(sn.getStatus()))
            {
                throw new ServiceException("仅已出库序列号允许退货回库：" + code);
            }
            Long targetWh = warehouseId != null ? warehouseId : sn.getWarehouseId();
            if (bizScopeHelper != null) bizScopeHelper.assertWarehouseAccess(targetWh);
            sn.setStatus("in_stock");
            sn.setWarehouseId(targetWh);
            if (locationId != null) sn.setLocationId(locationId);
            sn.setUpdateBy(operator);
            wmsSerialNoMapper.updateWmsSerialNo(sn);
            writeLog(sn, "sale_return", "sale_return", saleReturnId, billNo, targetWh, sn.getCustomerId(),
                StringUtils.isNotEmpty(remark) ? remark : "序列号退货回库", operator);
            count++;
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsSerialNoByIds(Long[] serialIds)
    {
        if (serialIds == null || serialIds.length == 0)
        {
            return 0;
        }
        for (Long id : serialIds)
        {
            WmsSerialNo sn = wmsSerialNoMapper.selectWmsSerialNoById(id);
            if (sn != null)
            {
                if (bizScopeHelper != null)
                {
                    bizScopeHelper.assertWarehouseAccess(sn.getWarehouseId());
                }
                if (!"in_stock".equals(sn.getStatus()))
                {
                    throw new ServiceException("序列号 " + sn.getSnCode() + " 已流转出库，禁止删除");
                }
            }
        }
        return wmsSerialNoMapper.deleteWmsSerialNoByIds(serialIds);
    }

    @Override
    public List<String> parseSnCodes(String rawSnCodes)
    {
        List<String> input = new ArrayList<String>();
        if (StringUtils.isNotEmpty(rawSnCodes))
        {
            input.add(rawSnCodes);
        }
        return new ArrayList<String>(normalizeCodes(input));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleInboundSerials(MdProduct product, Long warehouseId, Long locationId, String batchNo,
                                     Long inboundId, String billNo, Date expiryDate, String rawSnCodes,
                                     BigDecimal baseQty, String operator)
    {
        if (!isSerialEnabled(product))
        {
            return;
        }
        List<String> codes = requireExactSnCount(product, rawSnCodes, baseQty, "入库审核");
        batchRegister(product.getProductId(), warehouseId, locationId, batchNo, inboundId, billNo, expiryDate,
            codes, "入库单[" + billNo + "]自动登记SN", operator);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollbackInboundSerials(MdProduct product, Long inboundId, String billNo, String rawSnCodes,
                                       boolean deleteRecord, String operator)
    {
        if (!isSerialEnabled(product))
        {
            return;
        }
        List<String> codes = parseSnCodes(rawSnCodes);
        if (codes.isEmpty())
        {
            return;
        }
        List<Long> idsToDelete = new ArrayList<Long>();
        for (String code : codes)
        {
            WmsSerialNo sn = wmsSerialNoMapper.selectWmsSerialNoByCode(code);
            if (sn == null)
            {
                continue;
            }
            if (!"in_stock".equals(sn.getStatus()))
            {
                throw new ServiceException("序列号[" + code + "]已发生出库或退货流转(状态:" + sn.getStatus() + ")，禁止反审核/红冲原入库单");
            }
            if (deleteRecord)
            {
                idsToDelete.add(sn.getSerialId());
            }
            else
            {
                sn.setStatus("returned");
                sn.setUpdateBy(operator);
                wmsSerialNoMapper.updateWmsSerialNo(sn);
                writeLog(sn, "inbound_reverse", "inbound_reverse", inboundId, billNo, sn.getWarehouseId(), null,
                    "入库单红冲核销SN", operator);
            }
        }
        if (deleteRecord && !idsToDelete.isEmpty())
        {
            wmsSerialNoMapper.deleteWmsSerialNoByIds(idsToDelete.toArray(new Long[0]));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleOutboundSerials(MdProduct product, Long warehouseId, Long outboundId, String billNo,
                                      Long customerId, String rawSnCodes, BigDecimal baseQty, String operator)
    {
        if (!isSerialEnabled(product))
        {
            return;
        }
        List<String> codes = requireExactSnCount(product, rawSnCodes, baseQty, "出库审核");
        for (String code : codes)
        {
            WmsSerialNo sn = wmsSerialNoMapper.selectWmsSerialNoByCode(code);
            if (sn == null)
            {
                throw new ServiceException("序列号[" + code + "]尚未入库登记，无法出库");
            }
            if (!product.getProductId().equals(sn.getProductId()))
            {
                throw new ServiceException("序列号[" + code + "]不属于商品[" + product.getProductName() + "]");
            }
            if (warehouseId != null && !warehouseId.equals(sn.getWarehouseId()))
            {
                throw new ServiceException("序列号[" + code + "]当前所在仓库(#" + sn.getWarehouseId() + ")与出库仓库(#" + warehouseId + ")不符");
            }
        }
        consumeSerialNos(codes, outboundId, billNo, customerId, "出库单[" + billNo + "]审核核销SN", operator);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollbackOutboundSerials(MdProduct product, Long warehouseId, Long locationId, Long outboundId,
                                        String billNo, String rawSnCodes, String remark, String operator)
    {
        if (!isSerialEnabled(product))
        {
            return;
        }
        List<String> codes = parseSnCodes(rawSnCodes);
        if (codes.isEmpty())
        {
            return;
        }
        for (String code : codes)
        {
            WmsSerialNo sn = wmsSerialNoMapper.selectWmsSerialNoByCode(code);
            if (sn == null)
            {
                continue;
            }
            if (!"out_stock".equals(sn.getStatus()))
            {
                throw new ServiceException("序列号[" + code + "]当前状态不是已出库，无法回退入库");
            }
            sn.setStatus("in_stock");
            if (warehouseId != null) sn.setWarehouseId(warehouseId);
            if (locationId != null) sn.setLocationId(locationId);
            sn.setOutboundId(null);
            sn.setCustomerId(null);
            sn.setUpdateBy(operator);
            wmsSerialNoMapper.updateWmsSerialNo(sn);
            writeLog(sn, "outbound_rollback", "outbound", outboundId, billNo, sn.getWarehouseId(), null,
                StringUtils.isNotEmpty(remark) ? remark : "出库撤销回滚SN至在库", operator);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleTransferSerials(MdProduct product, Long outWarehouseId, Long inWarehouseId, Long inLocationId,
                                      Long transferId, String billNo, String rawSnCodes, BigDecimal baseQty, String operator)
    {
        if (!isSerialEnabled(product))
        {
            return;
        }
        List<String> codes = requireExactSnCount(product, rawSnCodes, baseQty, "调拨审核");
        for (String code : codes)
        {
            WmsSerialNo sn = wmsSerialNoMapper.selectWmsSerialNoByCode(code);
            if (sn == null || !"in_stock".equals(sn.getStatus()))
            {
                throw new ServiceException("序列号[" + code + "]不存在或不在库，无法调拨");
            }
            if (!product.getProductId().equals(sn.getProductId()))
            {
                throw new ServiceException("序列号[" + code + "]与商品[" + product.getProductName() + "]不匹配");
            }
            if (outWarehouseId != null && !outWarehouseId.equals(sn.getWarehouseId()))
            {
                throw new ServiceException("序列号[" + code + "]不在调出仓库 #" + outWarehouseId + "，无法调拨");
            }
            sn.setWarehouseId(inWarehouseId);
            sn.setLocationId(inLocationId == null ? 0L : inLocationId);
            sn.setUpdateBy(operator);
            wmsSerialNoMapper.updateWmsSerialNo(sn);
            writeLog(sn, "transfer", "transfer", transferId, billNo, inWarehouseId, null,
                "调拨单[" + billNo + "]从仓库#" + outWarehouseId + "调入仓库#" + inWarehouseId, operator);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePurchaseReturnSerials(MdProduct product, Long warehouseId, Long returnId, String billNo,
                                            String rawSnCodes, BigDecimal baseQty, String operator)
    {
        if (!isSerialEnabled(product))
        {
            return;
        }
        List<String> codes = requireExactSnCount(product, rawSnCodes, baseQty, "采购退货审核");
        for (String code : codes)
        {
            WmsSerialNo sn = wmsSerialNoMapper.selectWmsSerialNoByCode(code);
            if (sn == null || !"in_stock".equals(sn.getStatus()))
            {
                throw new ServiceException("序列号[" + code + "]不存在或不在库，无法采购退货");
            }
            if (!product.getProductId().equals(sn.getProductId()))
            {
                throw new ServiceException("序列号[" + code + "]不属于商品[" + product.getProductName() + "]");
            }
            if (warehouseId != null && !warehouseId.equals(sn.getWarehouseId()))
            {
                throw new ServiceException("序列号[" + code + "]不在退货仓库 #" + warehouseId);
            }
            sn.setStatus("returned");
            sn.setUpdateBy(operator);
            wmsSerialNoMapper.updateWmsSerialNo(sn);
            writeLog(sn, "purchase_return", "purchase_return", returnId, billNo, warehouseId, null,
                "采购退货单[" + billNo + "]退回供应商", operator);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollbackPurchaseReturnSerials(MdProduct product, Long warehouseId, Long locationId, Long returnId,
                                              String billNo, String rawSnCodes, String operator)
    {
        if (!isSerialEnabled(product))
        {
            return;
        }
        List<String> codes = parseSnCodes(rawSnCodes);
        for (String code : codes)
        {
            WmsSerialNo sn = wmsSerialNoMapper.selectWmsSerialNoByCode(code);
            if (sn == null)
            {
                continue;
            }
            sn.setStatus("in_stock");
            if (warehouseId != null) sn.setWarehouseId(warehouseId);
            if (locationId != null) sn.setLocationId(locationId);
            sn.setUpdateBy(operator);
            wmsSerialNoMapper.updateWmsSerialNo(sn);
            writeLog(sn, "purchase_return_rollback", "purchase_return", returnId, billNo, sn.getWarehouseId(), null,
                "采购退货撤销恢复SN在库", operator);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleSaleReturnSerials(MdProduct product, Long warehouseId, Long locationId, Long saleReturnId,
                                        String billNo, String rawSnCodes, BigDecimal baseQty, String operator)
    {
        if (!isSerialEnabled(product))
        {
            return;
        }
        List<String> codes = requireExactSnCount(product, rawSnCodes, baseQty, "销售退货审核");
        for (String code : codes)
        {
            WmsSerialNo sn = wmsSerialNoMapper.selectWmsSerialNoByCode(code);
            if (sn == null || !"out_stock".equals(sn.getStatus()))
            {
                throw new ServiceException("序列号[" + code + "]不是已出库状态，无法办理销售退货");
            }
            if (!product.getProductId().equals(sn.getProductId()))
            {
                throw new ServiceException("序列号[" + code + "]不属于商品[" + product.getProductName() + "]");
            }
        }
        returnSerialNos(codes, warehouseId, locationId, saleReturnId, billNo, "销售退货单[" + billNo + "]退货回库", operator);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollbackSaleReturnSerials(MdProduct product, Long saleReturnId, String billNo, Long customerId,
                                          String rawSnCodes, String operator)
    {
        if (!isSerialEnabled(product))
        {
            return;
        }
        List<String> codes = parseSnCodes(rawSnCodes);
        if (codes.isEmpty())
        {
            return;
        }
        consumeSerialNos(codes, saleReturnId, billNo, customerId, "销售退货撤销回退SN至已出库", operator);
    }

    private boolean isSerialEnabled(MdProduct product)
    {
        return product != null && "1".equals(product.getEnableSerial());
    }

    private List<String> requireExactSnCount(MdProduct product, String rawSnCodes, BigDecimal baseQty, String scene)
    {
        if (baseQty == null || baseQty.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("商品[" + product.getProductName() + "]数量必须大于0");
        }
        BigDecimal stripped = baseQty.stripTrailingZeros();
        if (stripped.scale() > 0)
        {
            throw new ServiceException("序列号管控商品[" + product.getProductName() + "]的基准数量必须为整数，当前：" + baseQty);
        }
        int expected = stripped.intValueExact();
        List<String> codes = parseSnCodes(rawSnCodes);
        if (codes.size() != expected)
        {
            throw new ServiceException(scene + "失败：商品[" + product.getProductName() + "]已启用序列号强管控，须录入 "
                + expected + " 个唯一SN码，当前仅录入 " + codes.size() + " 个");
        }
        return codes;
    }

    private Set<String> normalizeCodes(List<String> snCodes)
    {
        Set<String> set = new LinkedHashSet<String>();
        if (snCodes != null)
        {
            for (String raw : snCodes)
            {
                if (StringUtils.isNotEmpty(raw))
                {
                    String[] parts = raw.split("[,;\\r\\n\\s]+");
                    for (String p : parts)
                    {
                        if (StringUtils.isNotEmpty(p.trim()))
                        {
                            set.add(p.trim());
                        }
                    }
                }
            }
        }
        return set;
    }

    private void writeLog(WmsSerialNo sn, String actionType, String billType, Long billId, String billNo,
                          Long warehouseId, Long counterpartyId, String remark, String operator)
    {
        WmsSerialNoLog log = new WmsSerialNoLog();
        log.setSerialId(sn.getSerialId());
        log.setSnCode(sn.getSnCode());
        log.setActionType(actionType);
        log.setBillType(billType);
        log.setBillId(billId);
        log.setBillNo(billNo);
        log.setWarehouseId(warehouseId);
        log.setCounterpartyId(counterpartyId);
        log.setRemark(remark);
        log.setCreateBy(operator);
        wmsSerialNoMapper.insertWmsSerialNoLog(log);
    }
}
