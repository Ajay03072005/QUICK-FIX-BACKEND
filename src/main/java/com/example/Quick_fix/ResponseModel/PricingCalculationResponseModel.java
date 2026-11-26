package com.example.Quick_fix.ResponseModel;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class PricingCalculationResponseModel {
    private String serviceUniqueId;
    private String serviceName;
    private String pricingModel;
    private BigDecimal baseCharge;
    private BigDecimal optionsCharge;
    private BigDecimal packageCharge;
    private BigDecimal unitRate;
    private BigDecimal quantity;
    private String unitLabel;
    private BigDecimal subtotal; // Authoritative calculated price before distance
    private Boolean isQuotation;
    private String quotationStatus;
    private List<BreakdownItem> breakdown;

    @Data
    public static class BreakdownItem {
        private String label;
        private String type; // BASE, OPTION, PACKAGE, UNIT, HOURLY, QUOTATION
        private BigDecimal amount;
        private String details;
    }
}
