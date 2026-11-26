package com.example.Quick_fix.ResponseModel;

import lombok.Data;

@Data
public class ServiceGuidanceItemResponseModel {
    private Long id;
    private String itemType; // SKILL, TOOL, MATERIAL
    private String name;
    private Boolean mandatory;
    private String notes;
}
