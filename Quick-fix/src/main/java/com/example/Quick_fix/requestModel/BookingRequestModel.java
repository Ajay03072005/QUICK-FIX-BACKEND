package com.example.Quick_fix.requestModel;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequestModel {

    @NotBlank
    private String serviceUniqueId;

    @NotBlank
    private String addressUniqueId;

    @NotBlank
    private String recipientName;

    @NotBlank
    private String recipientPhone;

    @NotNull
    @FutureOrPresent
    private LocalDate bookingDate;

    @NotNull
    private LocalTime bookingTime;
}