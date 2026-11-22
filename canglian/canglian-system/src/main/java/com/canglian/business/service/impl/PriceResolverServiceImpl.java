package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.MdCustomer;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.MdProductUnit;
import com.canglian.business.domain.PriceCustomerLevel;
import com.canglian.business.domain.PriceCustomerSpecial;
import com.canglian.business.domain.PriceRecentTransaction;
import com.canglian.business.mapper.MdCustomerMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.MdProductUnitMapper;
import com.canglian.business.mapper.PriceCustomerLevelMapper;
import com.canglian.business.mapper.PriceCustomerSpecialMapper;
import com.canglian.business.mapper.PriceRecentTransactionMapper;
import com.canglian.business.service.IMdProductUnitService;
import com.canglian.business.service.IPriceResolverService;
import com.canglian.common.utils.StringUtils;

/**
 * 价格解析服务实现（支持客户价格策略优先级、价格有效期窗口、多单位基准价归一化）
 *
 * @author canglian
 */
@Service
public class PriceResolverServiceImpl implements IPriceResolverService
{
    @Autowired
    private PriceCustomerSpecialMapper priceCustomerSpecialMapper;

    @Autowired
    private PriceCustomerLevelMapper priceCustomerLevelMapper;

    @Autowired
    private PriceRecentTransactionMapper priceRecentTransactionMapper;

    @Autowired
    private MdCustomerMapper mdCustomerMapper;

    @Autowired
    private MdProductMapper mdProductMapper;

    @Autowired(required = false)
    private MdProductUnitMapper mdProductUnitMapper;

    @Autowired(required = false)
    private IMdProductUnitService mdProductUnitService;

    @Override
    public BigDecimal resolveSalePrice(Long customerId, Long productId)
    {
        Map<String, Object> detail = resolveSalePriceDetail(customerId, productId, null);
        return (BigDecimal) detail.get("price");
    }

    @Override
    public BigDecimal resolvePurchasePrice(Long productId, String unitName)
    {
        if (productId == null)
        {
            return BigDecimal.ZERO;
        }
        MdProduct product = mdProductMapper.selectMdProductById(productId);
        BigDecimal convertRatio = BigDecimal.ONE;
        if (StringUtils.isNotEmpty(unitName))
        {
            String trimmed = unitName.trim();
            if (mdProductUnitMapper != null)
            {
                MdProductUnit matched = mdProductUnitMapper.selectByProductAndUnit(productId, trimmed);
                if (matched != null && matched.getPurchasePrice() != null && matched.getPurchasePrice().compareTo(BigDecimal.ZERO) > 0)
                {
                    return matched.getPurchasePrice().setScale(2, RoundingMode.HALF_UP);
                }
            }
            if (mdProductUnitService != null)
            {
                convertRatio = mdProductUnitService.resolveConvertRatio(productId, trimmed);
            }
        }
        BigDecimal baseCost = product == null ? BigDecimal.ZERO : defaultAmount(product.getCostPrice());
        return baseCost.multiply(convertRatio).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Map<String, Object> resolveSalePriceDetail(Long customerId, Long productId, String unitName)
    {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        MdProduct mdProduct = productId == null ? null : mdProductMapper.selectMdProductById(productId);
        MdCustomer mdCustomer = customerId == null ? null : mdCustomerMapper.selectMdCustomerById(customerId);

        String resolvedUnitName = StringUtils.isNotEmpty(unitName) ? unitName.trim() : null;
        if (StringUtils.isEmpty(resolvedUnitName) && mdProduct != null)
        {
            resolvedUnitName = StringUtils.isNotEmpty(mdProduct.getBaseUnit()) ? mdProduct.getBaseUnit()
                : (StringUtils.isNotEmpty(mdProduct.getUnitName()) ? mdProduct.getUnitName() : "件");
        }

        BigDecimal convertRatio = BigDecimal.ONE;
        MdProductUnit matchedUnit = null;
        if (productId != null && StringUtils.isNotEmpty(resolvedUnitName))
        {
            if (mdProductUnitMapper != null)
            {
                matchedUnit = mdProductUnitMapper.selectByProductAndUnit(productId, resolvedUnitName);
            }
            if (mdProductUnitService != null)
            {
                convertRatio = mdProductUnitService.resolveConvertRatio(productId, resolvedUnitName);
            }
            else if (matchedUnit != null && matchedUnit.getConvertRatio() != null && matchedUnit.getConvertRatio().compareTo(BigDecimal.ZERO) > 0)
            {
                convertRatio = matchedUnit.getConvertRatio();
            }
        }

        // 按客户配置的价格策略顺序解析基础单价
        String policy = mdCustomer == null || StringUtils.isEmpty(mdCustomer.getPricePolicyType())
            ? "special_first" : mdCustomer.getPricePolicyType().trim().toLowerCase();

        String[] order;
        if ("level_first".equals(policy) || "level".equals(policy))
        {
            order = new String[] { "level", "special", "recent", "unit", "default" };
        }
        else if ("recent_first".equals(policy) || "recent".equals(policy) || "last_price".equals(policy))
        {
            order = new String[] { "recent", "special", "level", "unit", "default" };
        }
        else if ("standard_only".equals(policy) || "standard".equals(policy))
        {
            order = new String[] { "unit", "default" };
        }
        else
        {
            order = new String[] { "special", "level", "recent", "unit", "default" };
        }

        BigDecimal basePrice = null;
        String source = "product_default";
        String sourceLabel = "商品标准售价";

        for (String strategy : order)
        {
            if ("special".equals(strategy))
            {
                BigDecimal special = findCustomerSpecialPrice(customerId, productId);
                if (special != null)
                {
                    basePrice = special;
                    source = "customer_special";
                    sourceLabel = "客户专属价";
                    break;
                }
            }
            else if ("level".equals(strategy))
            {
                BigDecimal level = findCustomerLevelPrice(mdCustomer, productId);
                if (level != null)
                {
                    basePrice = level;
                    source = "customer_level";
                    sourceLabel = "客户等级价";
                    break;
                }
            }
            else if ("recent".equals(strategy))
            {
                BigDecimal recent = findRecentSalePrice(customerId, productId);
                if (recent != null)
                {
                    basePrice = recent;
                    source = "recent_transaction";
                    sourceLabel = "最近成交价";
                    break;
                }
            }
            else if ("unit".equals(strategy))
            {
                if (matchedUnit != null && matchedUnit.getSalePrice() != null && matchedUnit.getSalePrice().compareTo(BigDecimal.ZERO) > 0)
                {
                    result.put("price", matchedUnit.getSalePrice().setScale(2, RoundingMode.HALF_UP));
                    result.put("basePrice", matchedUnit.getSalePrice().divide(convertRatio, 2, RoundingMode.HALF_UP));
                    result.put("priceSource", "product_unit");
                    result.put("priceSourceLabel", "包装单位定价");
                    result.put("unitName", matchedUnit.getUnitName());
                    result.put("convertRatio", convertRatio);
                    return result;
                }
            }
            else if ("default".equals(strategy))
            {
                basePrice = mdProduct == null ? BigDecimal.ZERO : defaultAmount(mdProduct.getSalePrice());
                source = "product_default";
                sourceLabel = "商品标准售价";
                break;
            }
        }

        if (basePrice == null)
        {
            basePrice = mdProduct == null ? BigDecimal.ZERO : defaultAmount(mdProduct.getSalePrice());
        }
        BigDecimal effectivePrice = basePrice.multiply(convertRatio).setScale(2, RoundingMode.HALF_UP);
        result.put("price", effectivePrice);
        result.put("basePrice", basePrice.setScale(2, RoundingMode.HALF_UP));
        result.put("priceSource", source);
        result.put("priceSourceLabel", sourceLabel);
        result.put("unitName", resolvedUnitName);
        result.put("convertRatio", convertRatio);
        return result;
    }

    @Override
    public void saveRecentSalePrice(Long customerId, Long productId, BigDecimal salePrice, String sourceBillType, Long sourceBillId, Date businessDate, String operator)
    {
        saveRecentSalePrice(customerId, productId, salePrice, BigDecimal.ONE, sourceBillType, sourceBillId, businessDate, operator);
    }

    @Override
    public void saveRecentSalePrice(Long customerId, Long productId, BigDecimal salePrice, BigDecimal convertRatio, String sourceBillType, Long sourceBillId, Date businessDate, String operator)
    {
        if (customerId == null || productId == null || salePrice == null || salePrice.compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }
        BigDecimal ratio = (convertRatio == null || convertRatio.compareTo(BigDecimal.ZERO) <= 0) ? BigDecimal.ONE : convertRatio;
        // 统一归一化为基准单位单价落库，杜绝包装单位售价落库后被二次乘以换算率造成价格膨胀
        BigDecimal normalizedBasePrice = salePrice.divide(ratio, 4, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
        MdProduct product = mdProductMapper.selectMdProductById(productId);
        String baseUnitName = product == null ? null
            : (StringUtils.isNotEmpty(product.getBaseUnit()) ? product.getBaseUnit() : product.getUnitName());

        PriceRecentTransaction query = new PriceRecentTransaction();
        query.setCustomerId(customerId);
        query.setProductId(productId);
        List<PriceRecentTransaction> list = priceRecentTransactionMapper.selectPriceRecentTransactionList(query);
        PriceRecentTransaction record = (list == null || list.isEmpty()) ? new PriceRecentTransaction() : list.get(0);
        record.setCustomerId(customerId);
        record.setProductId(productId);
        record.setSalePrice(normalizedBasePrice);
        record.setUnitName(baseUnitName);
        record.setSourceBillType(sourceBillType);
        record.setSourceBillId(sourceBillId);
        record.setBusinessDate(businessDate == null ? new Date() : businessDate);
        if (record.getRecentPriceId() == null)
        {
            record.setCreateBy(operator);
            priceRecentTransactionMapper.insertPriceRecentTransaction(record);
        }
        else
        {
            record.setUpdateBy(operator);
            priceRecentTransactionMapper.updatePriceRecentTransaction(record);
        }
    }

    @Override
    public void rollbackRecentSalePrice(String sourceBillType, Long sourceBillId)
    {
        if (StringUtils.isEmpty(sourceBillType) || sourceBillId == null)
        {
            return;
        }
        priceRecentTransactionMapper.deleteBySourceBill(sourceBillType, sourceBillId);
    }

    private BigDecimal findCustomerSpecialPrice(Long customerId, Long productId)
    {
        if (customerId == null || productId == null)
        {
            return null;
        }
        PriceCustomerSpecial query = new PriceCustomerSpecial();
        query.setCustomerId(customerId);
        query.setProductId(productId);
        query.setStatus("0");
        List<PriceCustomerSpecial> list = priceCustomerSpecialMapper.selectPriceCustomerSpecialList(query);
        if (list == null || list.isEmpty())
        {
            return null;
        }
        Date today = startOfToday();
        for (PriceCustomerSpecial item : list)
        {
            if (isInDateWindow(today, item.getEffectiveDate(), item.getExpireDate()) && item.getSalePrice() != null)
            {
                return item.getSalePrice();
            }
        }
        return null;
    }

    private BigDecimal findCustomerLevelPrice(MdCustomer mdCustomer, Long productId)
    {
        if (mdCustomer == null || productId == null || StringUtils.isEmpty(mdCustomer.getCustomerLevel()))
        {
            return null;
        }
        PriceCustomerLevel query = new PriceCustomerLevel();
        query.setCustomerLevel(mdCustomer.getCustomerLevel());
        query.setProductId(productId);
        query.setStatus("0");
        List<PriceCustomerLevel> list = priceCustomerLevelMapper.selectPriceCustomerLevelList(query);
        if (list == null || list.isEmpty())
        {
            return null;
        }
        Date today = startOfToday();
        for (PriceCustomerLevel item : list)
        {
            if (isInDateWindow(today, item.getEffectiveDate(), item.getExpireDate()) && item.getSalePrice() != null)
            {
                return item.getSalePrice();
            }
        }
        return null;
    }

    private BigDecimal findRecentSalePrice(Long customerId, Long productId)
    {
        if (customerId == null || productId == null)
        {
            return null;
        }
        PriceRecentTransaction query = new PriceRecentTransaction();
        query.setCustomerId(customerId);
        query.setProductId(productId);
        List<PriceRecentTransaction> list = priceRecentTransactionMapper.selectPriceRecentTransactionList(query);
        if (list == null || list.isEmpty())
        {
            return null;
        }
        return list.get(0).getSalePrice();
    }

    private boolean isInDateWindow(Date today, Date effectiveDate, Date expireDate)
    {
        if (effectiveDate != null && today.before(truncateDate(effectiveDate)))
        {
            return false;
        }
        if (expireDate != null && today.after(truncateDate(expireDate)))
        {
            return false;
        }
        return true;
    }

    private Date startOfToday()
    {
        return truncateDate(new Date());
    }

    private Date truncateDate(Date d)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private BigDecimal defaultAmount(BigDecimal amount)
    {
        return amount == null ? BigDecimal.ZERO : amount;
    }
}
