package com.example.Quick_fix.requestModel;

import lombok.Data;

@Data
public class ServiceGuidanceItemRequestModel {
    private String itemType; // SKILL, TOOL, MATERIAL
    private String name;
    private Boolean mandatory = true;
    private String notes;
}
