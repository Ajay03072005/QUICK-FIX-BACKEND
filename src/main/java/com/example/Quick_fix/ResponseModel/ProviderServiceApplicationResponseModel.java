package com.example.Quick_fix.ResponseModel;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ProviderServiceApplicationResponseModel {
    private Long id;
    private String uniqueId;
    private String providerUniqueId;
    private String providerName;
    private String providerEmail;
    private String providerPhone;
    private String serviceUniqueId;
    private String serviceName;
    private String serviceType;
    private String status;
    private Integer experienceYears;
    private String certifications;
    private String serviceArea;
    private String notes;
    private String rejectionReason;
    private String adminNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
