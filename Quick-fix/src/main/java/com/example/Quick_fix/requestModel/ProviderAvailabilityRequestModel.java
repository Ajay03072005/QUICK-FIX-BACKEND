package com.example.Quick_fix.requestModel;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    @JsonProperty("isWorkingDay")
    private boolean isWorkingDay = true;
}
