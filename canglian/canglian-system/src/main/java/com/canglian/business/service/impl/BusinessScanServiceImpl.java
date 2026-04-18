package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.MdLocation;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.SalOrder;
import com.canglian.business.domain.SalOrderItem;
import com.canglian.business.domain.ScanInventoryCheckItem;
import com.canglian.business.domain.ScanInventoryCheckRequest;
import com.canglian.business.domain.ScanQuickSaleItem;
import com.canglian.business.domain.ScanQuickSaleRequest;
import com.canglian.business.domain.ScanTransferItem;
import com.canglian.business.domain.ScanTransferRequest;
import com.canglian.business.domain.WmsInventoryCheck;
import com.canglian.business.domain.WmsInventoryCheckItem;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsTransfer;
import com.canglian.business.domain.WmsTransferItem;
import com.canglian.business.mapper.MdLocationMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IBusinessScanService;
import com.canglian.business.service.ISalOrderService;
import com.canglian.business.service.IWmsInventoryCheckItemService;
import com.canglian.business.service.IWmsInventoryCheckService;
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

    /**
     * 按条码解析商品
     * 
     * @param barCode 商品条码
     * @return 商品扫码结果
     */
    @Override
    public Map<String, Object> scanProduct(String barCode)
    {
        MdProduct mdProduct = resolveProduct(barCode);
        Map<String, Object> scanResult = new LinkedHashMap<String, Object>();
        scanResult.put("product", mdProduct);
        return scanResult;
    }

    /**
     * 按库位码解析库位
     * 
     * @param locationCode 库位编码
     * @return 库位扫码结果
     */
    @Override
    public Map<String, Object> scanLocation(String locationCode)
    {
        MdLocation mdLocation = resolveLocation(locationCode);
        Map<String, Object> scanResult = new LinkedHashMap<String, Object>();
        scanResult.put("location", mdLocation);
        return scanResult;
    }

    /**
     * 查询扫码库存结果
     * 
     * @param barCode 商品条码
     * @param warehouseId 仓库编号
     * @param locationCode 库位编码
     * @return 库存扫码结果
     */
    @Override
    public Map<String, Object> scanStock(String barCode, Long warehouseId, String locationCode)
    {
        if (warehouseId == null)
        {
            throw new ServiceException("仓库编号不能为空");
        }
        MdProduct mdProduct = resolveProduct(barCode);
        MdLocation mdLocation = StringUtils.isEmpty(locationCode) ? null : resolveLocation(locationCode);
        validateLocationWarehouse(mdLocation, warehouseId, "查询库位");
        WmsStock queryWmsStock = new WmsStock();
        queryWmsStock.setWarehouseId(warehouseId);
        queryWmsStock.setProductId(mdProduct.getProductId());
        if (mdLocation != null)
        {
            queryWmsStock.setLocationId(mdLocation.getLocationId());
        }
        List<WmsStock> wmsStockList = wmsStockMapper.selectWmsStockList(queryWmsStock);
        Map<String, Object> scanResult = new LinkedHashMap<String, Object>();
        scanResult.put("product", mdProduct);
        scanResult.put("location", mdLocation);
        scanResult.put("stockList", wmsStockList);
        scanResult.put("totalQty", sumStockQty(wmsStockList));
        scanResult.put("availableQty", sumAvailableQty(wmsStockList));
        return scanResult;
    }

    /**
     * 扫码快速生成销货订单
     * 
     * @param scanQuickSaleRequest 扫码开单请求
     * @param operator 操作人
     * @return 销货订单
     */
    @Override
    public SalOrder createQuickSaleOrder(ScanQuickSaleRequest scanQuickSaleRequest, String operator)
    {
        validateQuickSaleRequest(scanQuickSaleRequest);
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

    /**
     * 扫码快速生成盘点单
     * 
     * @param scanInventoryCheckRequest 扫码盘点请求
     * @param operator 操作人
     * @return 盘点单
     */
    @Override
    @Transactional
    public WmsInventoryCheck createInventoryCheck(ScanInventoryCheckRequest scanInventoryCheckRequest, String operator)
    {
        validateInventoryCheckRequest(scanInventoryCheckRequest);
        MdLocation mdLocation = resolveLocation(scanInventoryCheckRequest.getLocationCode());
        validateLocationWarehouse(mdLocation, scanInventoryCheckRequest.getWarehouseId(), "盘点库位");
        WmsInventoryCheck wmsInventoryCheck = new WmsInventoryCheck();
        wmsInventoryCheck.setWarehouseId(scanInventoryCheckRequest.getWarehouseId());
        wmsInventoryCheck.setRemark(scanInventoryCheckRequest.getRemark());
        wmsInventoryCheck.setCreateBy(operator);
        wmsInventoryCheckService.insertWmsInventoryCheck(wmsInventoryCheck);
        List<WmsInventoryCheckItem> inventoryCheckItemList = buildInventoryCheckItemList(scanInventoryCheckRequest, mdLocation);
        for (WmsInventoryCheckItem wmsInventoryCheckItem : inventoryCheckItemList)
        {
            wmsInventoryCheckItem.setCheckId(wmsInventoryCheck.getCheckId());
            wmsInventoryCheckItemService.insertWmsInventoryCheckItem(wmsInventoryCheckItem);
        }
        return wmsInventoryCheckService.selectWmsInventoryCheckById(wmsInventoryCheck.getCheckId());
    }

    /**
     * 扫码快速生成调拨单
     * 
     * @param scanTransferRequest 扫码调拨请求
     * @param operator 操作人
     * @return 调拨单
     */
    @Override
    @Transactional
    public WmsTransfer createTransfer(ScanTransferRequest scanTransferRequest, String operator)
    {
        validateTransferRequest(scanTransferRequest);
        MdLocation outLocation = resolveLocation(scanTransferRequest.getOutLocationCode());
        MdLocation inLocation = resolveLocation(scanTransferRequest.getInLocationCode());
        validateLocationWarehouse(outLocation, scanTransferRequest.getOutWarehouseId(), "调出库位");
        validateLocationWarehouse(inLocation, scanTransferRequest.getInWarehouseId(), "调入库位");
        WmsTransfer wmsTransfer = new WmsTransfer();
        wmsTransfer.setOutWarehouseId(scanTransferRequest.getOutWarehouseId());
        wmsTransfer.setInWarehouseId(scanTransferRequest.getInWarehouseId());
        wmsTransfer.setRemark(scanTransferRequest.getRemark());
        wmsTransfer.setCreateBy(operator);
        wmsTransferService.insertWmsTransfer(wmsTransfer);
        List<WmsTransferItem> transferItemList = buildTransferItemList(scanTransferRequest, outLocation, inLocation);
        for (WmsTransferItem wmsTransferItem : transferItemList)
        {
            wmsTransferItem.setTransferId(wmsTransfer.getTransferId());
            wmsTransferItemService.insertWmsTransferItem(wmsTransferItem);
        }
        return wmsTransferService.selectWmsTransferById(wmsTransfer.getTransferId());
    }

    /**
     * 解析商品信息
     * 
     * @param barCode 商品条码
     * @return 商品信息
     */
    private MdProduct resolveProduct(String barCode)
    {
        if (StringUtils.isEmpty(barCode))
        {
            throw new ServiceException("商品条码不能为空");
        }
        MdProduct queryMdProduct = new MdProduct();
        queryMdProduct.setBarCode(barCode);
        List<MdProduct> mdProductList = mdProductMapper.selectMdProductList(queryMdProduct);
        if (mdProductList == null || mdProductList.isEmpty())
        {
            throw new ServiceException("未找到对应条码商品");
        }
        return mdProductList.get(0);
    }

    /**
     * 解析库位信息
     * 
     * @param locationCode 库位编码
     * @return 库位信息
     */
    private MdLocation resolveLocation(String locationCode)
    {
        if (StringUtils.isEmpty(locationCode))
        {
            throw new ServiceException("库位编码不能为空");
        }
        MdLocation queryMdLocation = new MdLocation();
        queryMdLocation.setLocationCode(locationCode);
        List<MdLocation> mdLocationList = mdLocationMapper.selectMdLocationList(queryMdLocation);
        for (MdLocation mdLocation : mdLocationList)
        {
            if (locationCode.equals(mdLocation.getLocationCode()))
            {
                return mdLocation;
            }
        }
        throw new ServiceException("未找到对应库位编码");
    }

    /**
     * 校验扫码盘点请求
     * 
     * @param scanInventoryCheckRequest 扫码盘点请求
     */
    private void validateInventoryCheckRequest(ScanInventoryCheckRequest scanInventoryCheckRequest)
    {
        if (scanInventoryCheckRequest.getWarehouseId() == null)
        {
            throw new ServiceException("盘点仓库不能为空");
        }
        if (StringUtils.isEmpty(scanInventoryCheckRequest.getLocationCode()))
        {
            throw new ServiceException("盘点库位编码不能为空");
        }
        if (!hasInventoryCheckItemList(scanInventoryCheckRequest))
        {
            throw new ServiceException("扫码盘点明细不能为空");
        }
        for (ScanInventoryCheckItem scanInventoryCheckItem : scanInventoryCheckRequest.getInventoryCheckItemList())
        {
            if (scanInventoryCheckItem == null || StringUtils.isEmpty(scanInventoryCheckItem.getBarCode()))
            {
                throw new ServiceException("盘点明细商品条码不能为空");
            }
            if (scanInventoryCheckItem.getActualQty() == null || scanInventoryCheckItem.getActualQty().compareTo(BigDecimal.ZERO) < 0)
            {
                throw new ServiceException("实盘数量不能小于0");
            }
        }
    }

    /**
     * 校验扫码开单请求
     * 
     * @param scanQuickSaleRequest 扫码开单请求
     */
    private void validateQuickSaleRequest(ScanQuickSaleRequest scanQuickSaleRequest)
    {
        if (scanQuickSaleRequest.getCustomerId() == null)
        {
            throw new ServiceException("客户编号不能为空");
        }
        if (scanQuickSaleRequest.getWarehouseId() == null)
        {
            throw new ServiceException("仓库编号不能为空");
        }
        if (hasQuickSaleItemList(scanQuickSaleRequest))
        {
            for (ScanQuickSaleItem scanQuickSaleItem : scanQuickSaleRequest.getQuickSaleItemList())
            {
                if (scanQuickSaleItem == null || StringUtils.isEmpty(scanQuickSaleItem.getBarCode()))
                {
                    throw new ServiceException("扫码明细商品条码不能为空");
                }
                if (scanQuickSaleItem.getQuantity() == null || scanQuickSaleItem.getQuantity().compareTo(BigDecimal.ZERO) <= 0)
                {
                    throw new ServiceException("扫码明细数量必须大于0");
                }
            }
            return;
        }
        if (scanQuickSaleRequest.getQuantity() == null || scanQuickSaleRequest.getQuantity().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("扫码数量必须大于0");
        }
        if (StringUtils.isEmpty(scanQuickSaleRequest.getBarCode()))
        {
            throw new ServiceException("商品条码不能为空");
        }
    }

    /**
     * 校验扫码调拨请求
     * 
     * @param scanTransferRequest 扫码调拨请求
     */
    private void validateTransferRequest(ScanTransferRequest scanTransferRequest)
    {
        if (scanTransferRequest.getOutWarehouseId() == null)
        {
            throw new ServiceException("调出仓库不能为空");
        }
        if (scanTransferRequest.getInWarehouseId() == null)
        {
            throw new ServiceException("调入仓库不能为空");
        }
        if (scanTransferRequest.getOutWarehouseId().equals(scanTransferRequest.getInWarehouseId()))
        {
            throw new ServiceException("调入仓库与调出仓库不能相同");
        }
        if (StringUtils.isEmpty(scanTransferRequest.getOutLocationCode()))
        {
            throw new ServiceException("调出库位编码不能为空");
        }
        if (StringUtils.isEmpty(scanTransferRequest.getInLocationCode()))
        {
            throw new ServiceException("调入库位编码不能为空");
        }
        if (!hasTransferItemList(scanTransferRequest))
        {
            throw new ServiceException("扫码调拨明细不能为空");
        }
        for (ScanTransferItem scanTransferItem : scanTransferRequest.getTransferItemList())
        {
            if (scanTransferItem == null || StringUtils.isEmpty(scanTransferItem.getBarCode()))
            {
                throw new ServiceException("调拨明细商品条码不能为空");
            }
            if (scanTransferItem.getQuantity() == null || scanTransferItem.getQuantity().compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("调拨数量必须大于0");
            }
        }
    }

    /**
     * 构建扫码开单明细
     * 
     * @param scanQuickSaleRequest 扫码开单请求
     * @return 销售单据明细
     */
    private List<SalOrderItem> buildQuickSaleOrderItemList(ScanQuickSaleRequest scanQuickSaleRequest)
    {
        Map<Long, SalOrderItem> salOrderItemMap = new LinkedHashMap<Long, SalOrderItem>();
        if (hasQuickSaleItemList(scanQuickSaleRequest))
        {
            for (ScanQuickSaleItem scanQuickSaleItem : scanQuickSaleRequest.getQuickSaleItemList())
            {
                appendQuickSaleItem(salOrderItemMap, scanQuickSaleItem.getBarCode(), scanQuickSaleItem.getQuantity());
            }
        }
        else
        {
            appendQuickSaleItem(salOrderItemMap, scanQuickSaleRequest.getBarCode(), scanQuickSaleRequest.getQuantity());
        }
        return new ArrayList<SalOrderItem>(salOrderItemMap.values());
    }

    /**
     * 构建扫码盘点明细
     * 
     * @param scanInventoryCheckRequest 扫码盘点请求
     * @param mdLocation 盘点库位
     * @return 盘点明细列表
     */
    private List<WmsInventoryCheckItem> buildInventoryCheckItemList(ScanInventoryCheckRequest scanInventoryCheckRequest, MdLocation mdLocation)
    {
        Map<Long, WmsInventoryCheckItem> inventoryCheckItemMap = new LinkedHashMap<Long, WmsInventoryCheckItem>();
        for (ScanInventoryCheckItem scanInventoryCheckItem : scanInventoryCheckRequest.getInventoryCheckItemList())
        {
            appendInventoryCheckItem(inventoryCheckItemMap, scanInventoryCheckRequest.getWarehouseId(), mdLocation, scanInventoryCheckItem);
        }
        return new ArrayList<WmsInventoryCheckItem>(inventoryCheckItemMap.values());
    }

    /**
     * 构建扫码调拨明细
     * 
     * @param scanTransferRequest 扫码调拨请求
     * @param outLocation 调出库位
     * @param inLocation 调入库位
     * @return 调拨明细列表
     */
    private List<WmsTransferItem> buildTransferItemList(ScanTransferRequest scanTransferRequest, MdLocation outLocation, MdLocation inLocation)
    {
        Map<Long, WmsTransferItem> transferItemMap = new LinkedHashMap<Long, WmsTransferItem>();
        Map<Long, BigDecimal> availableQtyMap = new HashMap<Long, BigDecimal>();
        for (ScanTransferItem scanTransferItem : scanTransferRequest.getTransferItemList())
        {
            appendTransferItem(transferItemMap, availableQtyMap, scanTransferRequest.getOutWarehouseId(), outLocation, inLocation, scanTransferItem);
        }
        return new ArrayList<WmsTransferItem>(transferItemMap.values());
    }

    /**
     * 追加扫码开单明细
     * 
     * @param salOrderItemMap 销售明细映射
     * @param barCode 商品条码
     * @param quantity 数量
     */
    private void appendQuickSaleItem(Map<Long, SalOrderItem> salOrderItemMap, String barCode, BigDecimal quantity)
    {
        MdProduct mdProduct = resolveProduct(barCode);
        SalOrderItem salOrderItem = salOrderItemMap.get(mdProduct.getProductId());
        if (salOrderItem == null)
        {
            salOrderItem = new SalOrderItem();
            salOrderItem.setProductId(mdProduct.getProductId());
            salOrderItem.setQuantity(quantity);
            salOrderItem.setRemark("扫码开单");
            salOrderItemMap.put(mdProduct.getProductId(), salOrderItem);
            return;
        }
        salOrderItem.setQuantity(defaultAmount(salOrderItem.getQuantity()).add(defaultAmount(quantity)));
    }

    /**
     * 追加扫码盘点明细
     * 
     * @param inventoryCheckItemMap 盘点明细映射
     * @param warehouseId 仓库编号
     * @param mdLocation 库位信息
     * @param scanInventoryCheckItem 扫码盘点明细
     */
    private void appendInventoryCheckItem(Map<Long, WmsInventoryCheckItem> inventoryCheckItemMap, Long warehouseId, MdLocation mdLocation,
        ScanInventoryCheckItem scanInventoryCheckItem)
    {
        MdProduct mdProduct = resolveProduct(scanInventoryCheckItem.getBarCode());
        WmsInventoryCheckItem wmsInventoryCheckItem = inventoryCheckItemMap.get(mdProduct.getProductId());
        if (wmsInventoryCheckItem == null)
        {
            WmsStock wmsStock = selectStockByKey(warehouseId, mdProduct.getProductId(), mdLocation.getLocationId(), "");
            BigDecimal stockQty = wmsStock == null ? BigDecimal.ZERO : defaultAmount(wmsStock.getQuantity());
            BigDecimal actualQty = defaultAmount(scanInventoryCheckItem.getActualQty());
            BigDecimal itemPrice = defaultAmount(mdProduct.getCostPrice());
            wmsInventoryCheckItem = new WmsInventoryCheckItem();
            wmsInventoryCheckItem.setProductId(mdProduct.getProductId());
            wmsInventoryCheckItem.setLocationId(mdLocation.getLocationId());
            wmsInventoryCheckItem.setBatchNo("");
            wmsInventoryCheckItem.setStockQty(stockQty);
            wmsInventoryCheckItem.setActualQty(actualQty);
            wmsInventoryCheckItem.setDiffQty(actualQty.subtract(stockQty));
            wmsInventoryCheckItem.setPrice(itemPrice);
            wmsInventoryCheckItem.setDiffAmount(itemPrice.multiply(actualQty.subtract(stockQty)));
            inventoryCheckItemMap.put(mdProduct.getProductId(), wmsInventoryCheckItem);
            return;
        }
        wmsInventoryCheckItem.setActualQty(defaultAmount(wmsInventoryCheckItem.getActualQty()).add(defaultAmount(scanInventoryCheckItem.getActualQty())));
        wmsInventoryCheckItem.setDiffQty(defaultAmount(wmsInventoryCheckItem.getActualQty()).subtract(defaultAmount(wmsInventoryCheckItem.getStockQty())));
        wmsInventoryCheckItem.setDiffAmount(defaultAmount(wmsInventoryCheckItem.getPrice()).multiply(defaultAmount(wmsInventoryCheckItem.getDiffQty())));
    }

    /**
     * 追加扫码调拨明细
     * 
     * @param transferItemMap 调拨明细映射
     * @param availableQtyMap 可用库存映射
     * @param warehouseId 调出仓库编号
     * @param outLocation 调出库位
     * @param inLocation 调入库位
     * @param scanTransferItem 扫码调拨明细
     */
    private void appendTransferItem(Map<Long, WmsTransferItem> transferItemMap, Map<Long, BigDecimal> availableQtyMap, Long warehouseId,
        MdLocation outLocation, MdLocation inLocation, ScanTransferItem scanTransferItem)
    {
        MdProduct mdProduct = resolveProduct(scanTransferItem.getBarCode());
        BigDecimal availableQty = loadAvailableQty(availableQtyMap, warehouseId, mdProduct.getProductId(), outLocation.getLocationId());
        WmsTransferItem wmsTransferItem = transferItemMap.get(mdProduct.getProductId());
        BigDecimal transferQty = defaultAmount(scanTransferItem.getQuantity());
        if (wmsTransferItem == null)
        {
            if (availableQty.compareTo(transferQty) < 0)
            {
                throw new ServiceException("商品条码" + scanTransferItem.getBarCode() + "可用库存不足");
            }
            BigDecimal itemPrice = defaultAmount(mdProduct.getCostPrice());
            wmsTransferItem = new WmsTransferItem();
            wmsTransferItem.setProductId(mdProduct.getProductId());
            wmsTransferItem.setOutLocationId(outLocation.getLocationId());
            wmsTransferItem.setInLocationId(inLocation.getLocationId());
            wmsTransferItem.setBatchNo("");
            wmsTransferItem.setQuantity(transferQty);
            wmsTransferItem.setPrice(itemPrice);
            wmsTransferItem.setAmount(itemPrice.multiply(transferQty));
            transferItemMap.put(mdProduct.getProductId(), wmsTransferItem);
            return;
        }
        BigDecimal mergedQty = defaultAmount(wmsTransferItem.getQuantity()).add(transferQty);
        if (availableQty.compareTo(mergedQty) < 0)
        {
            throw new ServiceException("商品条码" + scanTransferItem.getBarCode() + "可用库存不足");
        }
        wmsTransferItem.setQuantity(mergedQty);
        wmsTransferItem.setAmount(defaultAmount(wmsTransferItem.getPrice()).multiply(mergedQty));
    }

    /**
     * 判断是否传入扫码开单明细
     * 
     * @param scanQuickSaleRequest 扫码开单请求
     * @return 判断结果
     */
    private boolean hasQuickSaleItemList(ScanQuickSaleRequest scanQuickSaleRequest)
    {
        return scanQuickSaleRequest.getQuickSaleItemList() != null && !scanQuickSaleRequest.getQuickSaleItemList().isEmpty();
    }

    /**
     * 判断是否传入扫码盘点明细
     * 
     * @param scanInventoryCheckRequest 扫码盘点请求
     * @return 判断结果
     */
    private boolean hasInventoryCheckItemList(ScanInventoryCheckRequest scanInventoryCheckRequest)
    {
        return scanInventoryCheckRequest.getInventoryCheckItemList() != null && !scanInventoryCheckRequest.getInventoryCheckItemList().isEmpty();
    }

    /**
     * 判断是否传入扫码调拨明细
     * 
     * @param scanTransferRequest 扫码调拨请求
     * @return 判断结果
     */
    private boolean hasTransferItemList(ScanTransferRequest scanTransferRequest)
    {
        return scanTransferRequest.getTransferItemList() != null && !scanTransferRequest.getTransferItemList().isEmpty();
    }

    /**
     * 汇总库存数量
     * 
     * @param wmsStockList 库存集合
     * @return 库存数量
     */
    private BigDecimal sumStockQty(List<WmsStock> wmsStockList)
    {
        BigDecimal totalQty = BigDecimal.ZERO;
        for (WmsStock wmsStock : wmsStockList)
        {
            totalQty = totalQty.add(wmsStock.getQuantity() == null ? BigDecimal.ZERO : wmsStock.getQuantity());
        }
        return totalQty;
    }

    /**
     * 汇总可用库存数量
     * 
     * @param wmsStockList 库存集合
     * @return 可用库存数量
     */
    private BigDecimal sumAvailableQty(List<WmsStock> wmsStockList)
    {
        BigDecimal availableQty = BigDecimal.ZERO;
        for (WmsStock wmsStock : wmsStockList)
        {
            availableQty = availableQty.add(calculateAvailableQty(wmsStock));
        }
        return availableQty;
    }

    /**
     * 加载商品可用库存
     * 
     * @param availableQtyMap 可用库存缓存
     * @param warehouseId 仓库编号
     * @param productId 商品编号
     * @param locationId 库位编号
     * @return 可用库存
     */
    private BigDecimal loadAvailableQty(Map<Long, BigDecimal> availableQtyMap, Long warehouseId, Long productId, Long locationId)
    {
        if (!availableQtyMap.containsKey(productId))
        {
            WmsStock wmsStock = selectStockByKey(warehouseId, productId, locationId, "");
            availableQtyMap.put(productId, calculateAvailableQty(wmsStock));
        }
        return availableQtyMap.get(productId);
    }

    /**
     * 按主键查询库存
     * 
     * @param warehouseId 仓库编号
     * @param productId 商品编号
     * @param locationId 库位编号
     * @param batchNo 批次号
     * @return 库存信息
     */
    private WmsStock selectStockByKey(Long warehouseId, Long productId, Long locationId, String batchNo)
    {
        WmsStock queryWmsStock = new WmsStock();
        queryWmsStock.setWarehouseId(warehouseId);
        queryWmsStock.setProductId(productId);
        queryWmsStock.setLocationId(locationId);
        queryWmsStock.setBatchNo(batchNo);
        return wmsStockMapper.selectWmsStockByKey(queryWmsStock);
    }

    /**
     * 计算可用库存数量
     * 
     * @param wmsStock 库存信息
     * @return 可用库存数量
     */
    private BigDecimal calculateAvailableQty(WmsStock wmsStock)
    {
        if (wmsStock == null)
        {
            return BigDecimal.ZERO;
        }
        return defaultAmount(wmsStock.getQuantity()).subtract(defaultAmount(wmsStock.getLockedQuantity())).subtract(defaultAmount(wmsStock.getFrozenQuantity()));
    }

    /**
     * 校验库位与仓库是否匹配
     * 
     * @param mdLocation 库位信息
     * @param warehouseId 仓库编号
     * @param sceneName 场景名称
     */
    private void validateLocationWarehouse(MdLocation mdLocation, Long warehouseId, String sceneName)
    {
        if (mdLocation == null || warehouseId == null)
        {
            return;
        }
        if (mdLocation.getWarehouseId() != null && !warehouseId.equals(mdLocation.getWarehouseId()))
        {
            throw new ServiceException(sceneName + "与仓库不匹配");
        }
    }

    /**
     * 空数量转默认值
     * 
     * @param quantity 数量
     * @return 默认数量
     */
    private BigDecimal defaultAmount(BigDecimal quantity)
    {
        return quantity == null ? BigDecimal.ZERO : quantity;
    }
}
