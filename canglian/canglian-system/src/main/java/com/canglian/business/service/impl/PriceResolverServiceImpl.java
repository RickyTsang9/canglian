package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.MdCustomer;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.PriceCustomerLevel;
import com.canglian.business.domain.PriceCustomerSpecial;
import com.canglian.business.domain.PriceRecentTransaction;
import com.canglian.business.mapper.MdCustomerMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.PriceCustomerLevelMapper;
import com.canglian.business.mapper.PriceCustomerSpecialMapper;
import com.canglian.business.mapper.PriceRecentTransactionMapper;
import com.canglian.business.service.IPriceResolverService;

/**
 * 价格解析服务实现
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

    /**
     * 解析销售价格
     * 
     * @param customerId 客户id
     * @param productId 商品id
     * @return 销售价格
     */
    @Override
    public BigDecimal resolveSalePrice(Long customerId, Long productId)
    {
        BigDecimal customerSpecialPrice = findCustomerSpecialPrice(customerId, productId);
        if (customerSpecialPrice != null)
        {
            return customerSpecialPrice;
        }
        BigDecimal customerLevelPrice = findCustomerLevelPrice(customerId, productId);
        if (customerLevelPrice != null)
        {
            return customerLevelPrice;
        }
        BigDecimal recentSalePrice = findRecentSalePrice(customerId, productId);
        if (recentSalePrice != null)
        {
            return recentSalePrice;
        }
        MdProduct mdProduct = mdProductMapper.selectMdProductById(productId);
        return mdProduct == null ? BigDecimal.ZERO : defaultAmount(mdProduct.getSalePrice());
    }

    /**
     * 保存最近成交价格
     * 
     * @param customerId 客户id
     * @param productId 商品id
     * @param salePrice 销售价
     * @param sourceBillType 来源单据类型
     * @param sourceBillId 来源单据id
     * @param businessDate 业务日期
     * @param operator 操作人
     */
    @Override
    public void saveRecentSalePrice(Long customerId, Long productId, BigDecimal salePrice, String sourceBillType, Long sourceBillId, Date businessDate, String operator)
    {
        if (customerId == null || productId == null || salePrice == null)
        {
            return;
        }
        PriceRecentTransaction priceRecentTransactionQuery = new PriceRecentTransaction();
        priceRecentTransactionQuery.setCustomerId(customerId);
        priceRecentTransactionQuery.setProductId(productId);
        List<PriceRecentTransaction> priceRecentTransactionList = priceRecentTransactionMapper.selectPriceRecentTransactionList(priceRecentTransactionQuery);
        PriceRecentTransaction priceRecentTransaction = priceRecentTransactionList.isEmpty() ? new PriceRecentTransaction() : priceRecentTransactionList.get(0);
        priceRecentTransaction.setCustomerId(customerId);
        priceRecentTransaction.setProductId(productId);
        priceRecentTransaction.setSalePrice(salePrice);
        priceRecentTransaction.setSourceBillType(sourceBillType);
        priceRecentTransaction.setSourceBillId(sourceBillId);
        priceRecentTransaction.setBusinessDate(businessDate);
        if (priceRecentTransaction.getRecentPriceId() == null)
        {
            priceRecentTransaction.setCreateBy(operator);
            priceRecentTransactionMapper.insertPriceRecentTransaction(priceRecentTransaction);
        }
        else
        {
            priceRecentTransaction.setUpdateBy(operator);
            priceRecentTransactionMapper.updatePriceRecentTransaction(priceRecentTransaction);
        }
    }

    /**
     * 查询客户专属价格
     * 
     * @param customerId 客户id
     * @param productId 商品id
     * @return 客户专属价格
     */
    private BigDecimal findCustomerSpecialPrice(Long customerId, Long productId)
    {
        if (customerId == null || productId == null)
        {
            return null;
        }
        PriceCustomerSpecial priceCustomerSpecialQuery = new PriceCustomerSpecial();
        priceCustomerSpecialQuery.setCustomerId(customerId);
        priceCustomerSpecialQuery.setProductId(productId);
        priceCustomerSpecialQuery.setStatus("0");
        List<PriceCustomerSpecial> priceCustomerSpecialList = priceCustomerSpecialMapper.selectPriceCustomerSpecialList(priceCustomerSpecialQuery);
        if (priceCustomerSpecialList.isEmpty())
        {
            return null;
        }
        return priceCustomerSpecialList.get(0).getSalePrice();
    }

    /**
     * 查询客户等级价格
     * 
     * @param customerId 客户id
     * @param productId 商品id
     * @return 客户等级价格
     */
    private BigDecimal findCustomerLevelPrice(Long customerId, Long productId)
    {
        if (customerId == null || productId == null)
        {
            return null;
        }
        MdCustomer mdCustomer = mdCustomerMapper.selectMdCustomerById(customerId);
        if (mdCustomer == null || mdCustomer.getCustomerLevel() == null || "".equals(mdCustomer.getCustomerLevel()))
        {
            return null;
        }
        PriceCustomerLevel priceCustomerLevelQuery = new PriceCustomerLevel();
        priceCustomerLevelQuery.setCustomerLevel(mdCustomer.getCustomerLevel());
        priceCustomerLevelQuery.setProductId(productId);
        priceCustomerLevelQuery.setStatus("0");
        List<PriceCustomerLevel> priceCustomerLevelList = priceCustomerLevelMapper.selectPriceCustomerLevelList(priceCustomerLevelQuery);
        if (priceCustomerLevelList.isEmpty())
        {
            return null;
        }
        return priceCustomerLevelList.get(0).getSalePrice();
    }

    /**
     * 查询最近成交价格
     * 
     * @param customerId 客户id
     * @param productId 商品id
     * @return 最近成交价格
     */
    private BigDecimal findRecentSalePrice(Long customerId, Long productId)
    {
        if (customerId == null || productId == null)
        {
            return null;
        }
        PriceRecentTransaction priceRecentTransactionQuery = new PriceRecentTransaction();
        priceRecentTransactionQuery.setCustomerId(customerId);
        priceRecentTransactionQuery.setProductId(productId);
        List<PriceRecentTransaction> priceRecentTransactionList = priceRecentTransactionMapper.selectPriceRecentTransactionList(priceRecentTransactionQuery);
        if (priceRecentTransactionList.isEmpty())
        {
            return null;
        }
        return priceRecentTransactionList.get(0).getSalePrice();
    }

    /**
     * 处理空金额
     * 
     * @param amount 金额
     * @return 默认金额
     */
    private BigDecimal defaultAmount(BigDecimal amount)
    {
        return amount == null ? BigDecimal.ZERO : amount;
    }
}
