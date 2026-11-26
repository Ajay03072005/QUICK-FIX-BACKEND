package com.example.Quick_fix.requestModel;

import lombok.Data;

@Data
public class ProviderServiceApplicationRequestModel {
    private String serviceUniqueId;
    private Integer experienceYears;
    private String certifications;
    private String serviceArea;
    private String notes;
}
