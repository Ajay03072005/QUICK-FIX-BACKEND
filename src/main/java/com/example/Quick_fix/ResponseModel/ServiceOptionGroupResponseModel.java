package com.example.Quick_fix.ResponseModel;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class ServiceOptionGroupResponseModel {
    private Long id;
    private String groupName;
    private String selectionType;
    private Boolean isRequired;
    private Integer minSelections;
    private Integer maxSelections;
    private Boolean active;
    private String inputType;
    private BigDecimal unitRate;
    private String unitLabel;
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private List<ServiceOptionResponseModel> options;
}
