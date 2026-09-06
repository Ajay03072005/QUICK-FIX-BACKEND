package com.example.Quick_fix.requestModel;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class BookingRequestModel {

    private String serviceUniqueId;

    private String addressUniqueId;

    private BookingAddressRequestModel address;

    private String recipientName;

    private String recipientPhone;

    private LocalDate bookingDate;

    private LocalTime bookingTime;
}