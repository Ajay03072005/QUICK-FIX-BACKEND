package com.example.Quick_fix.ResponseModel;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderAvailabilityResponseModel {
    private Long id;
    private Integer weekday;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isWorkingDay;
}
