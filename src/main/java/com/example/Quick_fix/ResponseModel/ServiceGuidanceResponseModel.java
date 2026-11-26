package com.example.Quick_fix.ResponseModel;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ServiceGuidanceResponseModel {
    private Long id;
    private Integer serviceId;
    private String serviceUniqueId;
    private String serviceName;
    private String serviceType;
    private String pricingModel;
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
    private String currentApplicationStatus; // PENDING, APPROVED, REJECTED, MORE_INFO_REQUESTED, or null
    private String currentRejectionReason;
    private List<ServiceGuidanceItemResponseModel> skills = new ArrayList<>();
    private List<ServiceGuidanceItemResponseModel> tools = new ArrayList<>();
    private List<ServiceGuidanceItemResponseModel> materials = new ArrayList<>();
}
