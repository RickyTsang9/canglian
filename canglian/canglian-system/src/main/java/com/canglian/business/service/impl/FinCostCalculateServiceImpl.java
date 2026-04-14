package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.common.utils.SecurityUtils;
import com.canglian.common.utils.StringUtils;
import com.canglian.business.domain.FinCostAccount;
import com.canglian.business.domain.FinCostCalculate;
import com.canglian.business.domain.FinCostLayer;
import com.canglian.business.domain.FinCostLog;
import com.canglian.business.mapper.FinCostAccountMapper;
import com.canglian.business.mapper.FinCostLayerMapper;
import com.canglian.business.mapper.FinCostLogMapper;
import com.canglian.business.service.IFinCostCalculateService;

@Service
public class FinCostCalculateServiceImpl implements IFinCostCalculateService
{
    @Autowired
    private FinCostAccountMapper finCostAccountMapper;

    @Autowired
    private FinCostLayerMapper finCostLayerMapper;

    @Autowired
    private FinCostLogMapper finCostLogMapper;

    @Override
    @Transactional
    public FinCostLog calculateCost(FinCostCalculate finCostCalculate)
    {
        if (finCostCalculate == null)
        {
            return null;
        }
        if (finCostCalculate.getProductId() == null || finCostCalculate.getWarehouseId() == null)
        {
            return null;
        }
        if (finCostCalculate.getQuantity() == null || finCostCalculate.getQuantity().compareTo(BigDecimal.ZERO) <= 0)
        {
            return null;
        }
        if (StringUtils.isEmpty(finCostCalculate.getInOut()))
        {
            return null;
        }

        String costMethod = finCostCalculate.getCostMethod();
        if (StringUtils.isEmpty(costMethod))
        {
            costMethod = "moving";
        }

        String operatorName = SecurityUtils.getUsername();
        FinCostAccount finCostAccount = finCostAccountMapper.selectFinCostAccountByUnique(finCostCalculate.getProductId(), finCostCalculate.getWarehouseId(), costMethod);
        if (finCostAccount == null)
        {
            finCostAccount = buildDefaultCostAccount(finCostCalculate, costMethod, operatorName);
            finCostAccountMapper.insertFinCostAccount(finCostAccount);
        }

        if ("1".equals(finCostCalculate.getInOut()))
        {
            return handleInbound(finCostCalculate, finCostAccount, costMethod, operatorName);
        }
        if ("2".equals(finCostCalculate.getInOut()))
        {
            return handleOutbound(finCostCalculate, finCostAccount, costMethod, operatorName);
        }
        return null;
    }

    private FinCostLog handleInbound(FinCostCalculate finCostCalculate, FinCostAccount finCostAccount, String costMethod, String operatorName)
    {
        BigDecimal inboundPrice = getSafeDecimal(finCostCalculate.getPrice());
        BigDecimal inboundAmount = finCostCalculate.getQuantity().multiply(inboundPrice);

        BigDecimal totalQuantity = getSafeDecimal(finCostAccount.getTotalQty()).add(finCostCalculate.getQuantity());
        BigDecimal totalAmount = getSafeDecimal(finCostAccount.getTotalAmount()).add(inboundAmount);
        finCostAccount.setTotalQty(totalQuantity);
        finCostAccount.setTotalAmount(totalAmount);
        finCostAccount.setAvgCost(calculateAverageCost(totalQuantity, totalAmount));
        finCostAccount.setLastCostPrice(inboundPrice);
        finCostAccount.setLastCostQty(finCostCalculate.getQuantity());
        finCostAccount.setUpdateBy(operatorName);
        finCostAccountMapper.updateFinCostAccount(finCostAccount);

        if ("fifo".equalsIgnoreCase(costMethod))
        {
            FinCostLayer finCostLayer = new FinCostLayer();
            finCostLayer.setCostAccountId(finCostAccount.getCostAccountId());
            finCostLayer.setProductId(finCostAccount.getProductId());
            finCostLayer.setWarehouseId(finCostAccount.getWarehouseId());
            finCostLayer.setQuantity(finCostCalculate.getQuantity());
            finCostLayer.setRemainingQty(finCostCalculate.getQuantity());
            finCostLayer.setPrice(inboundPrice);
            finCostLayer.setAmount(inboundAmount);
            finCostLayer.setCreateBy(operatorName);
            finCostLayerMapper.insertFinCostLayer(finCostLayer);
        }

        FinCostLog finCostLog = buildCostLog(finCostCalculate, finCostAccount.getCostAccountId(), inboundPrice, inboundAmount, costMethod, operatorName);
        finCostLogMapper.insertFinCostLog(finCostLog);
        return finCostLog;
    }

    private FinCostLog handleOutbound(FinCostCalculate finCostCalculate, FinCostAccount finCostAccount, String costMethod, String operatorName)
    {
        BigDecimal outboundPrice;
        BigDecimal outboundAmount;
        if ("fifo".equalsIgnoreCase(costMethod))
        {
            CostConsumeResult costConsumeResult = consumeFifoLayers(finCostCalculate.getQuantity(), finCostAccount.getCostAccountId());
            BigDecimal remainingQuantity = costConsumeResult.getRemainingQuantity();
            BigDecimal costAmountFromLayers = costConsumeResult.getConsumedAmount();
            BigDecimal avgCost = getSafeDecimal(finCostAccount.getAvgCost());
            BigDecimal remainingAmount = remainingQuantity.multiply(avgCost);
            outboundAmount = costAmountFromLayers.add(remainingAmount);
            outboundPrice = calculateAverageCost(finCostCalculate.getQuantity(), outboundAmount);
        }
        else
        {
            outboundPrice = getSafeDecimal(finCostAccount.getAvgCost());
            if (outboundPrice.compareTo(BigDecimal.ZERO) == 0)
            {
                outboundPrice = getSafeDecimal(finCostCalculate.getPrice());
            }
            outboundAmount = finCostCalculate.getQuantity().multiply(outboundPrice);
        }

        BigDecimal totalQuantity = getSafeDecimal(finCostAccount.getTotalQty()).subtract(finCostCalculate.getQuantity());
        if (totalQuantity.compareTo(BigDecimal.ZERO) < 0)
        {
            totalQuantity = BigDecimal.ZERO;
        }
        BigDecimal totalAmount = getSafeDecimal(finCostAccount.getTotalAmount()).subtract(outboundAmount);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0)
        {
            totalAmount = BigDecimal.ZERO;
        }
        finCostAccount.setTotalQty(totalQuantity);
        finCostAccount.setTotalAmount(totalAmount);
        finCostAccount.setAvgCost(calculateAverageCost(totalQuantity, totalAmount));
        finCostAccount.setUpdateBy(operatorName);
        finCostAccountMapper.updateFinCostAccount(finCostAccount);

        FinCostLog finCostLog = buildCostLog(finCostCalculate, finCostAccount.getCostAccountId(), outboundPrice, outboundAmount, costMethod, operatorName);
        finCostLogMapper.insertFinCostLog(finCostLog);
        return finCostLog;
    }

    private CostConsumeResult consumeFifoLayers(BigDecimal requiredQuantity, Long costAccountId)
    {
        BigDecimal remainingQuantity = requiredQuantity;
        BigDecimal consumedAmount = BigDecimal.ZERO;
        List<FinCostLayer> costLayerList = finCostLayerMapper.selectAvailableCostLayers(costAccountId);
        for (int layerIndex = 0; layerIndex < costLayerList.size(); layerIndex++)
        {
            if (remainingQuantity.compareTo(BigDecimal.ZERO) <= 0)
            {
                break;
            }
            FinCostLayer finCostLayer = costLayerList.get(layerIndex);
            BigDecimal layerRemainingQuantity = getSafeDecimal(finCostLayer.getRemainingQty());
            if (layerRemainingQuantity.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            BigDecimal consumeQuantity = layerRemainingQuantity.min(remainingQuantity);
            BigDecimal layerAmount = consumeQuantity.multiply(getSafeDecimal(finCostLayer.getPrice()));
            consumedAmount = consumedAmount.add(layerAmount);
            BigDecimal newRemainingQuantity = layerRemainingQuantity.subtract(consumeQuantity);
            finCostLayer.setRemainingQty(newRemainingQuantity);
            finCostLayer.setAmount(newRemainingQuantity.multiply(getSafeDecimal(finCostLayer.getPrice())));
            finCostLayerMapper.updateFinCostLayer(finCostLayer);
            remainingQuantity = remainingQuantity.subtract(consumeQuantity);
        }
        return new CostConsumeResult(remainingQuantity, consumedAmount);
    }

    private FinCostAccount buildDefaultCostAccount(FinCostCalculate finCostCalculate, String costMethod, String operatorName)
    {
        FinCostAccount finCostAccount = new FinCostAccount();
        finCostAccount.setProductId(finCostCalculate.getProductId());
        finCostAccount.setWarehouseId(finCostCalculate.getWarehouseId());
        finCostAccount.setCostMethod(costMethod);
        finCostAccount.setTotalQty(BigDecimal.ZERO);
        finCostAccount.setTotalAmount(BigDecimal.ZERO);
        finCostAccount.setAvgCost(BigDecimal.ZERO);
        finCostAccount.setLastCostPrice(BigDecimal.ZERO);
        finCostAccount.setLastCostQty(BigDecimal.ZERO);
        finCostAccount.setCreateBy(operatorName);
        return finCostAccount;
    }

    private FinCostLog buildCostLog(FinCostCalculate finCostCalculate, Long costAccountId, BigDecimal price, BigDecimal amount, String costMethod, String operatorName)
    {
        FinCostLog finCostLog = new FinCostLog();
        finCostLog.setCostAccountId(costAccountId);
        finCostLog.setProductId(finCostCalculate.getProductId());
        finCostLog.setWarehouseId(finCostCalculate.getWarehouseId());
        finCostLog.setBillType(finCostCalculate.getBillType());
        finCostLog.setBillId(finCostCalculate.getBillId());
        finCostLog.setInOut(finCostCalculate.getInOut());
        finCostLog.setQuantity(finCostCalculate.getQuantity());
        finCostLog.setPrice(price);
        finCostLog.setAmount(amount);
        finCostLog.setCostMethod(costMethod);
        finCostLog.setCreateBy(operatorName);
        return finCostLog;
    }

    private BigDecimal getSafeDecimal(BigDecimal sourceDecimal)
    {
        return sourceDecimal == null ? BigDecimal.ZERO : sourceDecimal;
    }

    private BigDecimal calculateAverageCost(BigDecimal quantity, BigDecimal amount)
    {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0)
        {
            return BigDecimal.ZERO;
        }
        if (amount == null)
        {
            return BigDecimal.ZERO;
        }
        return amount.divide(quantity, 2, RoundingMode.HALF_UP);
    }

    private static class CostConsumeResult
    {
        private final BigDecimal remainingQuantity;
        private final BigDecimal consumedAmount;

        private CostConsumeResult(BigDecimal remainingQuantity, BigDecimal consumedAmount)
        {
            this.remainingQuantity = remainingQuantity;
            this.consumedAmount = consumedAmount;
        }

        public BigDecimal getRemainingQuantity()
        {
            return remainingQuantity;
        }

        public BigDecimal getConsumedAmount()
        {
            return consumedAmount;
        }
    }
}

