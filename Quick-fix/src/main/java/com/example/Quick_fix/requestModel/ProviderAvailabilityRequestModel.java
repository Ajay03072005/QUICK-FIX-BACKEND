package com.example.Quick_fix.requestModel;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderAvailabilityRequestModel {
    private Integer weekday; // 0=Sunday, 1=Monday, ... 6=Saturday
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isWorkingDay = true;
}
