package com.example.Quick_fix.requestModel;

import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

@Data
public class TrackingLocationRequestModel {

    @NotNull
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double latitude;

    @NotNull
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double longitude;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "360.0")
    private Double heading;

    @DecimalMin(value = "0.0")
    private Double speed;

    @NotNull
    @PastOrPresent
    private LocalDateTime recordedAt;
}
