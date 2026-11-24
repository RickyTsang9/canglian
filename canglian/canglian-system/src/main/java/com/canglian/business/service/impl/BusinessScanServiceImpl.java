package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.MdLocation;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.MdProductUnit;
import com.canglian.business.domain.SalOrder;
import com.canglian.business.domain.SalOrderItem;
import com.canglian.business.domain.ScanInventoryCheckItem;
import com.canglian.business.domain.ScanInventoryCheckRequest;
import com.canglian.business.domain.ScanQuickSaleItem;
import com.canglian.business.domain.ScanQuickSaleRequest;
import com.canglian.business.domain.ScanTransferItem;
import com.canglian.business.domain.ScanTransferRequest;
import com.canglian.business.domain.WmsInbound;
import com.canglian.business.domain.WmsInboundItem;
import com.canglian.business.domain.WmsInventoryCheck;
import com.canglian.business.domain.WmsInventoryCheckItem;
import com.canglian.business.domain.WmsOutbound;
import com.canglian.business.domain.WmsOutboundItem;
import com.canglian.business.domain.WmsSerialNo;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsTransfer;
import com.canglian.business.domain.WmsTransferItem;
import com.canglian.business.mapper.MdLocationMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.MdProductUnitMapper;
import com.canglian.business.mapper.WmsOutboundMapper;
import com.canglian.business.mapper.WmsSerialNoMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IBusinessScanService;
import com.canglian.business.service.ISalOrderService;
import com.canglian.business.service.IWmsInboundService;
import com.canglian.business.service.IWmsInventoryCheckItemService;
import com.canglian.business.service.IWmsInventoryCheckService;
import com.canglian.business.service.IWmsOutboundService;
import com.canglian.business.service.IWmsTransferItemService;
import com.canglian.business.service.IWmsTransferService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 业务扫码服务实现
 *
 * @author canglian
 */
@Service
public class BusinessScanServiceImpl implements IBusinessScanService
{
    @Autowired
    private MdProductMapper mdProductMapper;

    @Autowired(required = false)
    private MdProductUnitMapper mdProductUnitMapper;

    @Autowired
    private MdLocationMapper mdLocationMapper;

    @Autowired
    private WmsStockMapper wmsStockMapper;

    @Autowired
    private ISalOrderService salOrderService;

    @Autowired
    private IWmsInventoryCheckService wmsInventoryCheckService;

    @Autowired
    private IWmsInventoryCheckItemService wmsInventoryCheckItemService;

    @Autowired
    private IWmsTransferService wmsTransferService;

    @Autowired
    private IWmsTransferItemService wmsTransferItemService;

    @Autowired(required = false)
    private IWmsInboundService wmsInboundService;

    @Autowired(required = false)
    private IWmsOutboundService wmsOutboundService;

    @Autowired(required = false)
    private WmsOutboundMapper wmsOutboundMapper;

    @Autowired(required = false)
    private WmsSerialNoMapper wmsSerialNoMapper;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Override
    public Map<String, Object> scanProduct(String barCode)
    {
        MdProductUnit matchedUnit = mdProductUnitMapper == null || StringUtils.isEmpty(barCode) ? null
            : mdProductUnitMapper.selectByBarCode(barCode.trim());
        MdProduct mdProduct = resolveProduct(barCode);
        Map<String, Object> scanResult = new LinkedHashMap<String, Object>();
        scanResult.put("product", mdProduct);
        if (matchedUnit != null)
        {
            scanResult.put("unit", matchedUnit);
            scanResult.put("matchedUnit", matchedUnit);
        }
        return scanResult;
    }

    @Override
    public Map<String, Object> scanLocation(String locationCode)
    {
        MdLocation mdLocation = resolveLocation(locationCode);
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(mdLocation.getWarehouseId());
        }
        Map<String, Object> scanResult = new LinkedHashMap<String, Object>();
        scanResult.put("location", mdLocation);
        return scanResult;
    }

    @Override
    public Map<String, Object> scanStock(String barCode, Long warehouseId, String locationCode)
    {
        return scanStock(barCode, warehouseId, locationCode, null);
    }

    @Override
    public Map<String, Object> scanStock(String barCode, Long warehouseId, String locationCode, String batchNo)
    {
        if (warehouseId == null) throw new ServiceException("仓库编号不能为空");
        if (bizScopeHelper != null) bizScopeHelper.assertWarehouseAccess(warehouseId);
        MdProduct mdProduct = resolveProduct(barCode);
        MdLocation mdLocation = StringUtils.isEmpty(locationCode) ? null : resolveLocation(locationCode, warehouseId);
        validateLocationWarehouse(mdLocation, warehouseId, "查询库位");
        WmsStock queryWmsStock = new WmsStock();
        queryWmsStock.setWarehouseId(warehouseId);
        queryWmsStock.setProductId(mdProduct.getProductId());
        if (mdLocation != null) queryWmsStock.setLocationId(mdLocation.getLocationId());
        if (StringUtils.isNotEmpty(batchNo)) queryWmsStock.setBatchNo(batchNo.trim());
        List<WmsStock> wmsStockList = wmsStockMapper.selectWmsStockList(queryWmsStock);
        Map<String, Object> scanResult = new LinkedHashMap<String, Object>();
        scanResult.put("product", mdProduct);
        scanResult.put("location", mdLocation);
        scanResult.put("stockList", wmsStockList);
        scanResult.put("totalQty", sumStockQty(wmsStockList));
        scanResult.put("availableQty", sumAvailableQty(wmsStockList));
        return scanResult;
    }

    @Override
    public SalOrder createQuickSaleOrder(ScanQuickSaleRequest scanQuickSaleRequest, String operator)
    {
        validateQuickSaleRequest(scanQuickSaleRequest);
        if (bizScopeHelper != null) bizScopeHelper.assertWarehouseAccess(scanQuickSaleRequest.getWarehouseId());
        SalOrder salOrder = new SalOrder();
        salOrder.setOrderType("sale");
        salOrder.setCustomerId(scanQuickSaleRequest.getCustomerId());
        salOrder.setWarehouseId(scanQuickSaleRequest.getWarehouseId());
        salOrder.setCreateBy(operator);
        salOrder.setRemark(scanQuickSaleRequest.getRemark());
        salOrder.setOrderItemList(buildQuickSaleOrderItemList(scanQuickSaleRequest));
        salOrderService.insertSalOrder(salOrder);
        return salOrderService.selectSalOrderById(salOrder.getOrderId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsInventoryCheck createInventoryCheck(ScanInventoryCheckRequest scanInventoryCheckRequest, String operator)
    {
        validateInventoryCheckRequest(scanInventoryCheckRequest);
        if (bizScopeHelper != null) bizScopeHelper.assertWarehouseAccess(scanInventoryCheckRequest.getWarehouseId());
        MdLocation mdLocation = resolveLocation(scanInventoryCheckRequest.getLocationCode(), scanInventoryCheckRequest.getWarehouseId());
        validateLocationWarehouse(mdLocation, scanInventoryCheckRequest.getWarehouseId(), "盘点库位");
        List<WmsInventoryCheckItem> inventoryCheckItemList = buildInventoryCheckItemList(scanInventoryCheckRequest, mdLocation);
        WmsInventoryCheck wmsInventoryCheck = new WmsInventoryCheck();
        wmsInventoryCheck.setWarehouseId(scanInventoryCheckRequest.getWarehouseId());
        wmsInventoryCheck.setBusinessDate(new Date());
        wmsInventoryCheck.setRemark(scanInventoryCheckRequest.getRemark());
        wmsInventoryCheck.setCreateBy(operator);
        wmsInventoryCheck.setItemList(inventoryCheckItemList);
        wmsInventoryCheckService.insertWmsInventoryCheck(wmsInventoryCheck);
        return wmsInventoryCheckService.selectWmsInventoryCheckById(wmsInventoryCheck.getCheckId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsTransfer createTransfer(ScanTransferRequest scanTransferRequest, String operator)
    {
        validateTransferRequest(scanTransferRequest);
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(scanTransferRequest.getOutWarehouseId());
            bizScopeHelper.assertWarehouseAccess(scanTransferRequest.getInWarehouseId());
        }
        MdLocation outLocation = resolveLocation(scanTransferRequest.getOutLocationCode(), scanTransferRequest.getOutWarehouseId());
        MdLocation inLocation = resolveLocation(scanTransferRequest.getInLocationCode(), scanTransferRequest.getInWarehouseId());
        validateLocationWarehouse(outLocation, scanTransferRequest.getOutWarehouseId(), "调出库位");
        validateLocationWarehouse(inLocation, scanTransferRequest.getInWarehouseId(), "调入库位");
        List<WmsTransferItem> transferItemList = buildTransferItemList(scanTransferRequest, outLocation, inLocation);
        WmsTransfer wmsTransfer = new WmsTransfer();
        wmsTransfer.setOutWarehouseId(scanTransferRequest.getOutWarehouseId());
        wmsTransfer.setInWarehouseId(scanTransferRequest.getInWarehouseId());
        wmsTransfer.setBusinessDate(new Date());
        wmsTransfer.setRemark(scanTransferRequest.getRemark());
        wmsTransfer.setCreateBy(operator);
        wmsTransfer.setItemList(transferItemList);
        wmsTransferService.insertWmsTransfer(wmsTransfer);
        return wmsTransferService.selectWmsTransferById(wmsTransfer.getTransferId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    public Map<String, Object> createQuickInbound(Map<String, Object> payload, String operator)
    {
        if (payload == null) throw new ServiceException("扫码入库请求不能为空");
        Long warehouseId = parseLong(payload.get("warehouseId"));
        Long supplierId = parseLong(payload.get("supplierId"));
        String locationCode = payload.get("locationCode") == null ? null : String.valueOf(payload.get("locationCode")).trim();
        String remark = payload.get("remark") == null ? "扫码快速入库建单" : String.valueOf(payload.get("remark"));
        boolean autoAudit = Boolean.TRUE.equals(payload.get("autoAudit")) || "true".equalsIgnoreCase(String.valueOf(payload.get("autoAudit")));
        if (warehouseId == null) throw new ServiceException("入库仓库不能为空");
        if (bizScopeHelper != null) bizScopeHelper.assertWarehouseAccess(warehouseId);
        MdLocation mdLocation = StringUtils.isEmpty(locationCode) ? null : resolveLocation(locationCode, warehouseId);
        validateLocationWarehouse(mdLocation, warehouseId, "入库库位");
        List<Map<String, Object>> rawItems = (List<Map<String, Object>>) payload.get("items");
        if (rawItems == null || rawItems.isEmpty()) throw new ServiceException("请先扫描至少一条入库商品明细");

        List<WmsInboundItem> itemList = new ArrayList<WmsInboundItem>();
        for (Map<String, Object> raw : rawItems)
        {
            String barCode = raw.get("barCode") == null ? null : String.valueOf(raw.get("barCode")).trim();
            BigDecimal qty = parseDecimal(raw.get("quantity"));
            if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) throw new ServiceException("扫码入库数量必须大于0");
            MdProductUnit unit = mdProductUnitMapper == null || StringUtils.isEmpty(barCode) ? null : mdProductUnitMapper.selectByBarCode(barCode);
            MdProduct product = resolveProduct(barCode);
            WmsInboundItem item = new WmsInboundItem();
            item.setProductId(product.getProductId());
            Long locId = mdLocation != null ? mdLocation.getLocationId() : (parseLong(raw.get("locationId")) != null ? parseLong(raw.get("locationId")) : 0L);
            item.setLocationId(locId);
            item.setBatchNo(raw.get("batchNo") == null ? "" : String.valueOf(raw.get("batchNo")).trim());
            item.setProductionDate(parseDate(raw.get("productionDate")));
            item.setExpiryDate(parseDate(raw.get("expiryDate")));
            if (raw.get("snCodes") != null) item.setSnCodes(String.valueOf(raw.get("snCodes")).trim());
            BigDecimal ratio = BigDecimal.ONE;
            if (unit != null)
            {
                item.setUnitName(unit.getUnitName());
                ratio = unit.getConvertRatio() != null && unit.getConvertRatio().compareTo(BigDecimal.ZERO) > 0 ? unit.getConvertRatio() : BigDecimal.ONE;
                item.setConvertRatio(ratio);
            }
            else
            {
                item.setUnitName(product.getUnitName());
                item.setConvertRatio(BigDecimal.ONE);
            }
            BigDecimal price = parseDecimal(raw.get("price"));
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0)
            {
                if (unit != null && unit.getPurchasePrice() != null && unit.getPurchasePrice().compareTo(BigDecimal.ZERO) > 0)
                {
                    price = unit.getPurchasePrice();
                }
                else
                {
                    // 包装条码未单独维护包装采购价时，按基准成本 × 换算率换算，杜绝整箱按单瓶计价
                    price = defaultAmount(product.getCostPrice()).multiply(ratio).setScale(2, RoundingMode.HALF_UP);
                }
            }
            item.setQuantity(qty);
            item.setPrice(price);
            item.setAmount(price.multiply(qty).setScale(2, RoundingMode.HALF_UP));
            itemList.add(item);
        }

        WmsInbound inbound = new WmsInbound();
        inbound.setInboundType(supplierId != null ? "purchase" : "other");
        inbound.setSupplierId(supplierId);
        inbound.setWarehouseId(warehouseId);
        inbound.setBusinessDate(new Date());
        inbound.setCreateBy(operator);
        inbound.setRemark(remark);
        inbound.setItemList(itemList);
        wmsInboundService.insertWmsInbound(inbound);
        if (autoAudit)
        {
            wmsInboundService.auditWmsInbound(inbound.getInboundId(), operator);
        }
        WmsInbound saved = wmsInboundService.selectWmsInboundById(inbound.getInboundId());
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("inbound", saved);
        return result;
    }

    @Override
    public Map<String, Object> verifyOutboundByScan(Long outboundId, List<Map<String, Object>> scannedItems)
    {
        return verifyOutboundByScan(outboundId, scannedItems, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> verifyOutboundByScan(Long outboundId, List<Map<String, Object>> scannedItems, String operator)
    {
        if (outboundId == null) throw new ServiceException("出库单ID不能为空");
        WmsOutbound outbound = wmsOutboundService.selectWmsOutboundById(outboundId);
        if (outbound == null) throw new ServiceException("出库单不存在");
        if (!"1".equals(outbound.getStatus())) throw new ServiceException("仅已审核出库单允许执行扫码出库复核");
        if ("1".equals(outbound.getIsReversal()) || "1".equals(outbound.getReversedFlag()))
        {
            throw new ServiceException("红冲单据或已红冲出库单禁止扫码复核");
        }
        List<WmsOutboundItem> expectedItems = outbound.getItemList();
        if (expectedItems == null || expectedItems.isEmpty()) throw new ServiceException("该出库单暂无明细可核验");

        // 统一换算为基准数量进行复核，支持大包装/拆箱条码混扫
        Map<Long, BigDecimal> scannedBaseQtyByProduct = new HashMap<Long, BigDecimal>();
        if (scannedItems != null)
        {
            for (Map<String, Object> row : scannedItems)
            {
                BigDecimal qty = parseDecimal(row.get("quantity"));
                if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) qty = BigDecimal.ONE;
                BigDecimal ratio = parseDecimal(row.get("convertRatio"));
                Long productId = parseLong(row.get("productId"));
                if (row.get("barCode") != null)
                {
                    String code = String.valueOf(row.get("barCode")).trim();
                    MdProductUnit unit = mdProductUnitMapper == null || code.isEmpty() ? null : mdProductUnitMapper.selectByBarCode(code);
                    if (unit != null)
                    {
                        if (productId == null) productId = unit.getProductId();
                        if (ratio == null || ratio.compareTo(BigDecimal.ZERO) <= 0) ratio = unit.getConvertRatio();
                    }
                    if (productId == null)
                    {
                        MdProduct p = resolveProduct(code);
                        productId = p.getProductId();
                    }
                }
                if (ratio == null || ratio.compareTo(BigDecimal.ZERO) <= 0) ratio = BigDecimal.ONE;
                if (productId != null)
                {
                    BigDecimal baseQty = qty.multiply(ratio).setScale(4, RoundingMode.HALF_UP);
                    scannedBaseQtyByProduct.put(productId, defaultAmount(scannedBaseQtyByProduct.get(productId)).add(baseQty));
                }
            }
        }

        Map<Long, BigDecimal> expectedBaseQtyByProduct = new LinkedHashMap<Long, BigDecimal>();
        for (WmsOutboundItem item : expectedItems)
        {
            BigDecimal baseQty = item.getBaseQuantity() != null && item.getBaseQuantity().compareTo(BigDecimal.ZERO) > 0
                ? item.getBaseQuantity()
                : defaultAmount(item.getQuantity()).multiply(item.getConvertRatio() != null && item.getConvertRatio().compareTo(BigDecimal.ZERO) > 0
                    ? item.getConvertRatio() : BigDecimal.ONE).setScale(4, RoundingMode.HALF_UP);
            expectedBaseQtyByProduct.put(item.getProductId(),
                defaultAmount(expectedBaseQtyByProduct.get(item.getProductId())).add(baseQty));
        }

        boolean allMatched = true;
        List<Map<String, Object>> details = new ArrayList<Map<String, Object>>();
        for (Map.Entry<Long, BigDecimal> entry : expectedBaseQtyByProduct.entrySet())
        {
            Long prodId = entry.getKey();
            BigDecimal expected = entry.getValue();
            BigDecimal scanned = defaultAmount(scannedBaseQtyByProduct.get(prodId));
            BigDecimal diff = scanned.subtract(expected);
            boolean matched = diff.compareTo(BigDecimal.ZERO) == 0;
            if (!matched) allMatched = false;
            MdProduct prod = mdProductMapper.selectMdProductById(prodId);
            Map<String, Object> line = new LinkedHashMap<String, Object>();
            line.put("productId", prodId);
            line.put("productName", prod == null ? ("商品#" + prodId) : prod.getProductName());
            line.put("barCode", prod == null ? "" : prod.getBarCode());
            line.put("unitName", prod == null ? "" : prod.getUnitName());
            line.put("expectedQty", expected);
            line.put("scannedQty", scanned);
            line.put("diffQty", diff);
            line.put("matched", matched);
            details.add(line);
        }
        // 检测错扫/多扫的清单外商品，避免“多塞货仍复核通过”漏检
        for (Map.Entry<Long, BigDecimal> extra : scannedBaseQtyByProduct.entrySet())
        {
            if (expectedBaseQtyByProduct.containsKey(extra.getKey())) continue;
            BigDecimal extraQty = defaultAmount(extra.getValue());
            if (extraQty.compareTo(BigDecimal.ZERO) <= 0) continue;
            allMatched = false;
            MdProduct prod = mdProductMapper.selectMdProductById(extra.getKey());
            Map<String, Object> line = new LinkedHashMap<String, Object>();
            line.put("productId", extra.getKey());
            line.put("productName", (prod == null ? ("商品#" + extra.getKey()) : prod.getProductName()) + "（错扫/非本单商品）");
            line.put("barCode", prod == null ? "" : prod.getBarCode());
            line.put("unitName", prod == null ? "" : prod.getUnitName());
            line.put("expectedQty", BigDecimal.ZERO);
            line.put("scannedQty", extraQty);
            line.put("diffQty", extraQty);
            line.put("matched", false);
            details.add(line);
        }
        // 扫码复核全部一致时落库复核标记与复核时间，形成出库复核闭环
        if (allMatched && wmsOutboundMapper != null)
        {
            WmsOutbound upd = new WmsOutbound();
            upd.setOutboundId(outboundId);
            upd.setVerifiedFlag("1");
            upd.setVerifiedBy(StringUtils.isNotEmpty(operator) ? operator : "scan");
            upd.setVerifiedTime(new Date());
            wmsOutboundMapper.updateWmsOutbound(upd);
        }
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("outboundId", outboundId);
        result.put("outboundNo", outbound.getOutboundNo());
        result.put("passed", allMatched);
        result.put("verifiedFlag", allMatched ? "1" : outbound.getVerifiedFlag());
        result.put("details", details);
        return result;
    }

    private MdProduct resolveProduct(String barCode)
    {
        if (StringUtils.isEmpty(barCode)) throw new ServiceException("商品条码不能为空");
        String code = barCode.trim();
        MdProduct queryMdProduct = new MdProduct();
        queryMdProduct.setBarCode(code);
        queryMdProduct.setStatus("0");
        List<MdProduct> mdProductList = mdProductMapper.selectMdProductList(queryMdProduct);
        if (mdProductList != null)
        {
            for (MdProduct p : mdProductList)
            {
                if (!"1".equals(p.getStatus()) && !"2".equals(p.getDelFlag()) && code.equals(p.getBarCode())) return p;
            }
            for (MdProduct p : mdProductList)
            {
                if (!"1".equals(p.getStatus()) && !"2".equals(p.getDelFlag())) return p;
            }
        }
        if (mdProductUnitMapper != null)
        {
            MdProductUnit unit = mdProductUnitMapper.selectByBarCode(code);
            if (unit != null && unit.getProductId() != null)
            {
                MdProduct prod = mdProductMapper.selectMdProductById(unit.getProductId());
                if (prod != null && !"1".equals(prod.getStatus()) && !"2".equals(prod.getDelFlag())) return prod;
            }
        }
        if (wmsSerialNoMapper != null)
        {
            WmsSerialNo serial = wmsSerialNoMapper.selectWmsSerialNoByCode(code);
            if (serial != null && serial.getProductId() != null)
            {
                MdProduct prod = mdProductMapper.selectMdProductById(serial.getProductId());
                if (prod != null && !"1".equals(prod.getStatus()) && !"2".equals(prod.getDelFlag())) return prod;
            }
        }
        throw new ServiceException("未找到对应条码的启用商品");
    }

    private MdLocation resolveLocation(String locationCode) { return resolveLocation(locationCode, null); }

    private MdLocation resolveLocation(String locationCode, Long warehouseId)
    {
        if (StringUtils.isEmpty(locationCode)) throw new ServiceException("库位编码不能为空");
        MdLocation queryMdLocation = new MdLocation();
        queryMdLocation.setLocationCode(locationCode);
        queryMdLocation.setWarehouseId(warehouseId);
        queryMdLocation.setStatus("0");
        List<MdLocation> mdLocationList = mdLocationMapper.selectMdLocationList(queryMdLocation);
        if (mdLocationList != null)
        {
            for (MdLocation mdLocation : mdLocationList)
            {
                if (!"1".equals(mdLocation.getStatus()) && !"2".equals(mdLocation.getDelFlag())
                    && locationCode.equals(mdLocation.getLocationCode())
                    && (warehouseId == null || warehouseId.equals(mdLocation.getWarehouseId())))
                {
                    return mdLocation;
                }
            }
        }
        throw new ServiceException("未找到对应库位编码的启用库位");
    }

    private void validateInventoryCheckRequest(ScanInventoryCheckRequest req)
    {
        if (req.getWarehouseId() == null) throw new ServiceException("盘点仓库不能为空");
        if (StringUtils.isEmpty(req.getLocationCode())) throw new ServiceException("盘点库位编码不能为空");
        if (!hasInventoryCheckItemList(req)) throw new ServiceException("扫码盘点明细不能为空");
        for (ScanInventoryCheckItem item : req.getInventoryCheckItemList())
        {
            if (item == null || StringUtils.isEmpty(item.getBarCode())) throw new ServiceException("盘点明细商品条码不能为空");
            if (item.getActualQty() == null || item.getActualQty().compareTo(BigDecimal.ZERO) < 0) throw new ServiceException("实盘数量不能小于0");
        }
    }

    private void validateQuickSaleRequest(ScanQuickSaleRequest req)
    {
        if (req.getCustomerId() == null) throw new ServiceException("客户编号不能为空");
        if (req.getWarehouseId() == null) throw new ServiceException("仓库编号不能为空");
        if (hasQuickSaleItemList(req))
        {
            for (ScanQuickSaleItem item : req.getQuickSaleItemList())
            {
                if (item == null || StringUtils.isEmpty(item.getBarCode())) throw new ServiceException("扫码明细商品条码不能为空");
                if (item.getQuantity() == null || item.getQuantity().compareTo(BigDecimal.ZERO) <= 0) throw new ServiceException("扫码明细数量必须大于0");
            }
            return;
        }
        if (req.getQuantity() == null || req.getQuantity().compareTo(BigDecimal.ZERO) <= 0) throw new ServiceException("扫码数量必须大于0");
        if (StringUtils.isEmpty(req.getBarCode())) throw new ServiceException("商品条码不能为空");
    }

    private void validateTransferRequest(ScanTransferRequest req)
    {
        if (req.getOutWarehouseId() == null) throw new ServiceException("调出仓库不能为空");
        if (req.getInWarehouseId() == null) throw new ServiceException("调入仓库不能为空");
        if (req.getOutWarehouseId().equals(req.getInWarehouseId())) throw new ServiceException("调入仓库与调出仓库不能相同");
        if (StringUtils.isEmpty(req.getOutLocationCode())) throw new ServiceException("调出库位编码不能为空");
        if (StringUtils.isEmpty(req.getInLocationCode())) throw new ServiceException("调入库位编码不能为空");
        if (!hasTransferItemList(req)) throw new ServiceException("扫码调拨明细不能为空");
        for (ScanTransferItem item : req.getTransferItemList())
        {
            if (item == null || StringUtils.isEmpty(item.getBarCode())) throw new ServiceException("调拨明细商品条码不能为空");
            if (item.getQuantity() == null || item.getQuantity().compareTo(BigDecimal.ZERO) <= 0) throw new ServiceException("调拨数量必须大于0");
        }
    }

    private List<SalOrderItem> buildQuickSaleOrderItemList(ScanQuickSaleRequest req)
    {
        Map<String, SalOrderItem> map = new LinkedHashMap<String, SalOrderItem>();
        if (hasQuickSaleItemList(req))
        {
            for (ScanQuickSaleItem item : req.getQuickSaleItemList()) appendQuickSaleItem(map, item.getBarCode(), item.getQuantity());
        }
        else
        {
            appendQuickSaleItem(map, req.getBarCode(), req.getQuantity());
        }
        return new ArrayList<SalOrderItem>(map.values());
    }

    private List<WmsInventoryCheckItem> buildInventoryCheckItemList(ScanInventoryCheckRequest req, MdLocation mdLocation)
    {
        Map<String, WmsInventoryCheckItem> map = new LinkedHashMap<String, WmsInventoryCheckItem>();
        for (ScanInventoryCheckItem item : req.getInventoryCheckItemList())
        {
            appendInventoryCheckItem(map, req.getWarehouseId(), mdLocation, item);
        }
        return new ArrayList<WmsInventoryCheckItem>(map.values());
    }

    private List<WmsTransferItem> buildTransferItemList(ScanTransferRequest req, MdLocation outLocation, MdLocation inLocation)
    {
        Map<String, WmsTransferItem> map = new LinkedHashMap<String, WmsTransferItem>();
        Map<String, BigDecimal> availMap = new HashMap<String, BigDecimal>();
        for (ScanTransferItem item : req.getTransferItemList())
        {
            appendTransferItem(map, availMap, req.getOutWarehouseId(), outLocation, inLocation, item);
        }
        return new ArrayList<WmsTransferItem>(map.values());
    }

    private void appendQuickSaleItem(Map<String, SalOrderItem> salOrderItemMap, String barCode, BigDecimal quantity)
    {
        String code = barCode == null ? "" : barCode.trim();
        MdProductUnit matchedUnit = mdProductUnitMapper == null || code.isEmpty() ? null : mdProductUnitMapper.selectByBarCode(code);
        MdProduct mdProduct = resolveProduct(code);
        String unitName = matchedUnit != null && StringUtils.isNotEmpty(matchedUnit.getUnitName())
            ? matchedUnit.getUnitName() : mdProduct.getUnitName();
        BigDecimal ratio = matchedUnit != null && matchedUnit.getConvertRatio() != null && matchedUnit.getConvertRatio().compareTo(BigDecimal.ZERO) > 0
            ? matchedUnit.getConvertRatio() : BigDecimal.ONE;
        String key = mdProduct.getProductId() + "#" + (unitName == null ? "" : unitName);
        SalOrderItem salOrderItem = salOrderItemMap.get(key);
        if (salOrderItem == null)
        {
            salOrderItem = new SalOrderItem();
            salOrderItem.setProductId(mdProduct.getProductId());
            salOrderItem.setUnitName(unitName);
            salOrderItem.setConvertRatio(ratio);
            salOrderItem.setQuantity(quantity);
            salOrderItem.setRemark("扫码开单");
            salOrderItemMap.put(key, salOrderItem);
            return;
        }
        salOrderItem.setQuantity(defaultAmount(salOrderItem.getQuantity()).add(defaultAmount(quantity)));
    }

    private void appendInventoryCheckItem(Map<String, WmsInventoryCheckItem> map, Long warehouseId, MdLocation mdLocation, ScanInventoryCheckItem scanItem)
    {
        String code = scanItem.getBarCode() == null ? "" : scanItem.getBarCode().trim();
        MdProductUnit matchedUnit = mdProductUnitMapper == null || code.isEmpty() ? null : mdProductUnitMapper.selectByBarCode(code);
        MdProduct mdProduct = resolveProduct(code);
        String batchNo = scanItem.getBatchNo() == null ? "" : scanItem.getBatchNo().trim();
        BigDecimal ratio = matchedUnit != null && matchedUnit.getConvertRatio() != null && matchedUnit.getConvertRatio().compareTo(BigDecimal.ZERO) > 0
            ? matchedUnit.getConvertRatio() : BigDecimal.ONE;
        BigDecimal actualBaseAdd = defaultAmount(scanItem.getActualQty()).multiply(ratio).setScale(4, RoundingMode.HALF_UP);
        String baseUnitName = StringUtils.isNotEmpty(mdProduct.getBaseUnit()) ? mdProduct.getBaseUnit() : mdProduct.getUnitName();
        String key = mdProduct.getProductId() + "#" + batchNo;
        WmsInventoryCheckItem existing = map.get(key);
        if (existing == null)
        {
            WmsStock wmsStock = selectStockByKey(warehouseId, mdProduct.getProductId(), mdLocation.getLocationId(), batchNo);
            BigDecimal stockQty = wmsStock == null ? BigDecimal.ZERO : defaultAmount(wmsStock.getQuantity());
            BigDecimal itemPrice = defaultAmount(mdProduct.getCostPrice());
            existing = new WmsInventoryCheckItem();
            existing.setProductId(mdProduct.getProductId());
            existing.setLocationId(mdLocation.getLocationId());
            existing.setBatchNo(batchNo);
            existing.setUnitName(baseUnitName);
            existing.setConvertRatio(BigDecimal.ONE);
            existing.setStockQty(stockQty);
            existing.setActualQty(actualBaseAdd);
            existing.setDiffQty(actualBaseAdd.subtract(stockQty));
            existing.setBaseDiffQty(actualBaseAdd.subtract(stockQty));
            existing.setPrice(itemPrice);
            existing.setDiffAmount(itemPrice.multiply(actualBaseAdd.subtract(stockQty)).setScale(2, RoundingMode.HALF_UP));
            if (StringUtils.isNotEmpty(scanItem.getSnCodes())) existing.setSnCodes(scanItem.getSnCodes().trim());
            map.put(key, existing);
            return;
        }
        existing.setActualQty(defaultAmount(existing.getActualQty()).add(actualBaseAdd));
        existing.setDiffQty(defaultAmount(existing.getActualQty()).subtract(defaultAmount(existing.getStockQty())));
        existing.setBaseDiffQty(existing.getDiffQty());
        existing.setDiffAmount(defaultAmount(existing.getPrice()).multiply(defaultAmount(existing.getDiffQty())).setScale(2, RoundingMode.HALF_UP));
        if (StringUtils.isNotEmpty(scanItem.getSnCodes()))
        {
            existing.setSnCodes(StringUtils.isEmpty(existing.getSnCodes())
                ? scanItem.getSnCodes().trim()
                : existing.getSnCodes() + "," + scanItem.getSnCodes().trim());
        }
    }

    private void appendTransferItem(Map<String, WmsTransferItem> map, Map<String, BigDecimal> availMap, Long warehouseId,
                                    MdLocation outLocation, MdLocation inLocation, ScanTransferItem scanItem)
    {
        String code = scanItem.getBarCode() == null ? "" : scanItem.getBarCode().trim();
        MdProductUnit matchedUnit = mdProductUnitMapper == null || code.isEmpty() ? null : mdProductUnitMapper.selectByBarCode(code);
        MdProduct mdProduct = resolveProduct(code);
        String batchNo = scanItem.getBatchNo() == null ? "" : scanItem.getBatchNo().trim();
        BigDecimal ratio = matchedUnit != null && matchedUnit.getConvertRatio() != null && matchedUnit.getConvertRatio().compareTo(BigDecimal.ZERO) > 0
            ? matchedUnit.getConvertRatio() : BigDecimal.ONE;
        BigDecimal transferBaseQty = defaultAmount(scanItem.getQuantity()).multiply(ratio).setScale(4, RoundingMode.HALF_UP);
        String baseUnitName = StringUtils.isNotEmpty(mdProduct.getBaseUnit()) ? mdProduct.getBaseUnit() : mdProduct.getUnitName();
        String key = mdProduct.getProductId() + "#" + batchNo;
        BigDecimal availableQty = loadAvailableQty(availMap, key, warehouseId, mdProduct.getProductId(), outLocation.getLocationId(), batchNo);
        WmsTransferItem existing = map.get(key);
        if (existing == null)
        {
            if (availableQty.compareTo(transferBaseQty) < 0) throw new ServiceException("商品条码" + scanItem.getBarCode() + "可用库存不足");
            BigDecimal itemPrice = defaultAmount(mdProduct.getCostPrice());
            existing = new WmsTransferItem();
            existing.setProductId(mdProduct.getProductId());
            existing.setOutLocationId(outLocation.getLocationId());
            existing.setInLocationId(inLocation.getLocationId());
            existing.setBatchNo(batchNo);
            existing.setUnitName(baseUnitName);
            existing.setConvertRatio(BigDecimal.ONE);
            existing.setQuantity(transferBaseQty);
            existing.setBaseQuantity(transferBaseQty);
            existing.setPrice(itemPrice);
            existing.setAmount(itemPrice.multiply(transferBaseQty).setScale(2, RoundingMode.HALF_UP));
            if (StringUtils.isNotEmpty(scanItem.getSnCodes())) existing.setSnCodes(scanItem.getSnCodes().trim());
            map.put(key, existing);
            return;
        }
        BigDecimal mergedQty = defaultAmount(existing.getQuantity()).add(transferBaseQty);
        if (availableQty.compareTo(mergedQty) < 0) throw new ServiceException("商品条码" + scanItem.getBarCode() + "可用库存不足");
        existing.setQuantity(mergedQty);
        existing.setBaseQuantity(mergedQty);
        existing.setAmount(defaultAmount(existing.getPrice()).multiply(mergedQty).setScale(2, RoundingMode.HALF_UP));
        if (StringUtils.isNotEmpty(scanItem.getSnCodes()))
        {
            existing.setSnCodes(StringUtils.isEmpty(existing.getSnCodes())
                ? scanItem.getSnCodes().trim()
                : existing.getSnCodes() + "," + scanItem.getSnCodes().trim());
        }
    }

    private boolean hasQuickSaleItemList(ScanQuickSaleRequest req) { return req.getQuickSaleItemList() != null && !req.getQuickSaleItemList().isEmpty(); }
    private boolean hasInventoryCheckItemList(ScanInventoryCheckRequest req) { return req.getInventoryCheckItemList() != null && !req.getInventoryCheckItemList().isEmpty(); }
    private boolean hasTransferItemList(ScanTransferRequest req) { return req.getTransferItemList() != null && !req.getTransferItemList().isEmpty(); }

    private BigDecimal sumStockQty(List<WmsStock> list)
    {
        BigDecimal total = BigDecimal.ZERO;
        for (WmsStock s : list) total = total.add(defaultAmount(s.getQuantity()));
        return total;
    }

    private BigDecimal sumAvailableQty(List<WmsStock> list)
    {
        BigDecimal total = BigDecimal.ZERO;
        for (WmsStock s : list) total = total.add(calculateAvailableQty(s));
        return total;
    }

    private BigDecimal loadAvailableQty(Map<String, BigDecimal> map, String key, Long warehouseId, Long productId, Long locationId, String batchNo)
    {
        if (!map.containsKey(key))
        {
            WmsStock s = selectStockByKey(warehouseId, productId, locationId, batchNo);
            map.put(key, calculateAvailableQty(s));
        }
        return map.get(key);
    }

    private WmsStock selectStockByKey(Long warehouseId, Long productId, Long locationId, String batchNo)
    {
        WmsStock q = new WmsStock();
        q.setWarehouseId(warehouseId);
        q.setProductId(productId);
        q.setLocationId(locationId);
        q.setBatchNo(batchNo);
        return wmsStockMapper.selectWmsStockByKey(q);
    }

    private BigDecimal calculateAvailableQty(WmsStock s)
    {
        if (s == null) return BigDecimal.ZERO;
        return defaultAmount(s.getQuantity()).subtract(defaultAmount(s.getLockedQuantity())).subtract(defaultAmount(s.getFrozenQuantity()));
    }

    private void validateLocationWarehouse(MdLocation loc, Long warehouseId, String scene)
    {
        if (loc != null && warehouseId != null && loc.getWarehouseId() != null && !warehouseId.equals(loc.getWarehouseId()))
        {
            throw new ServiceException(scene + "与仓库不匹配");
        }
    }

    private BigDecimal defaultAmount(BigDecimal q) { return q == null ? BigDecimal.ZERO : q; }

    private Long parseLong(Object v)
    {
        if (v == null || "".equals(String.valueOf(v).trim())) return null;
        try { return Long.valueOf(String.valueOf(v).trim()); } catch (Exception e) { return null; }
    }

    private BigDecimal parseDecimal(Object v)
    {
        if (v == null || "".equals(String.valueOf(v).trim())) return null;
        try { return new BigDecimal(String.valueOf(v).trim()); } catch (Exception e) { return null; }
    }

    private Date parseDate(Object v)
    {
        if (v == null || "".equals(String.valueOf(v).trim())) return null;
        if (v instanceof Date) return (Date) v;
        String s = String.valueOf(v).trim();
        for (String fmt : new String[] { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd" })
        {
            try { return new SimpleDateFormat(fmt).parse(s); } catch (Exception ignored) {}
        }
        return null;
    }
}
