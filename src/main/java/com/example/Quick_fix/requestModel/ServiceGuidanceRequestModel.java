package com.example.Quick_fix.requestModel;

import java.util.List;
import lombok.Data;

@Data
public class ServiceGuidanceRequestModel {
    private String serviceUniqueId;
    private String overview;
    private String inclusions;
    private String exclusions;
    private String responsibilities;
    private Integer minExperienceYears;
    private String estimatedDuration;
    private Double estimatedEarningMin;
    private Double estimatedEarningMax;
    private String earningBasis;
    private String safetyInstructions;
    private String eligibilityCriteria;
    private Boolean active;
    private List<ServiceGuidanceItemRequestModel> items;
}
