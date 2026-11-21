package com.example.Quick_fix.ResponseModel;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    @JsonProperty("isWorkingDay")
    private boolean isWorkingDay;
}
