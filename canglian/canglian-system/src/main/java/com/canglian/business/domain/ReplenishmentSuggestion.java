package com.canglian.business.domain;

import java.math.BigDecimal;

/**
 * 补货建议实体
 * 
 * @author canglian
 */
public class ReplenishmentSuggestion
{
    private Long warehouseId;

    private Long productId;

    private String productCode;

    private String productName;

    private BigDecimal currentQty;

    private BigDecimal warningMinQty;

    private BigDecimal recentOutboundQty;

    private BigDecimal pendingPurchaseQty;

    private BigDecimal suggestedQty;

    private String suggestionReason;

    public Long getWarehouseId()
    {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId)
    {
        this.warehouseId = warehouseId;
    }

    public Long getProductId()
    {
        return productId;
    }

    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public String getProductCode()
    {
        return productCode;
    }

    public void setProductCode(String productCode)
    {
        this.productCode = productCode;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public BigDecimal getCurrentQty()
    {
        return currentQty;
    }

    public void setCurrentQty(BigDecimal currentQty)
    {
        this.currentQty = currentQty;
    }

    public BigDecimal getWarningMinQty()
    {
        return warningMinQty;
    }

    public void setWarningMinQty(BigDecimal warningMinQty)
    {
        this.warningMinQty = warningMinQty;
    }

    public BigDecimal getRecentOutboundQty()
    {
        return recentOutboundQty;
    }

    public void setRecentOutboundQty(BigDecimal recentOutboundQty)
    {
        this.recentOutboundQty = recentOutboundQty;
    }

    public BigDecimal getPendingPurchaseQty()
    {
        return pendingPurchaseQty;
    }

    public void setPendingPurchaseQty(BigDecimal pendingPurchaseQty)
    {
        this.pendingPurchaseQty = pendingPurchaseQty;
    }

    public BigDecimal getSuggestedQty()
    {
        return suggestedQty;
    }

    public void setSuggestedQty(BigDecimal suggestedQty)
    {
        this.suggestedQty = suggestedQty;
    }

    public String getSuggestionReason()
    {
        return suggestionReason;
    }

    public void setSuggestionReason(String suggestionReason)
    {
        this.suggestionReason = suggestionReason;
    }
}
