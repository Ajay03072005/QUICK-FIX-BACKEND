package com.example.Quick_fix.ResponseModel;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.Quick_fix.Enums.BookingStatus;
import com.example.Quick_fix.Enums.PaymentStatus;

import lombok.Data;

@Data
public class BookingResponseModel {

    private String uniqueId;

    private String customerUniqueId;

    private String serviceUniqueId;

    private String providerUniqueId;

    private String addressUniqueId;

    private String recipientName;

    private String recipientPhone;

    private LocalDate bookingDate;

    private LocalTime bookingTime;

    private Double distance;

    private Double servicePrice;

    private Double distanceCharge;

    private Double totalAmount;

    private BookingStatus status;

    private PaymentStatus paymentStatus;
}