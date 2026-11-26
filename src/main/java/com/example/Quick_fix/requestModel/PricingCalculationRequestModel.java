package com.example.Quick_fix.requestModel;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class PricingCalculationRequestModel {
    private String serviceUniqueId; // e.g. XYws56 or 3d8b643ae966
    private Integer serviceId;      // optional fallback
    private Long packageId;         // for PACKAGE pricing model
    private List<SelectedOptionGroupRequest> selectedOptions;
    private List<QuantityInputRequest> quantities;
    private String customerNotes;   // For QUOTATION models

    @Data
    public static class SelectedOptionGroupRequest {
        private Long groupId;
        private List<Long> optionIds;
    }

    @Data
    public static class QuantityInputRequest {
        private Long groupId;
        private BigDecimal value;
        private String textValue;
    }
}
