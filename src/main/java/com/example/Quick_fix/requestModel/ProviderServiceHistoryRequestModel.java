package com.example.Quick_fix.requestModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ProviderServiceHistoryRequestModel {

    private String service;

    private LocalDate startDate;

    private LocalDate endDate;

    private String status;
    
    private String notes;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
