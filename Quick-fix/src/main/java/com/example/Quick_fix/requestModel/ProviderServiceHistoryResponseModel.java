package com.example.Quick_fix.requestModel;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.Quick_fix.Enums.ServiceType;

import lombok.Data;

@Data

public class ProviderServiceHistoryResponseModel {

    private ServiceType service;

    private LocalDate startDate;

    private LocalDate endDate;

    private String status;

    
    private String notes;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
