package com.example.Quick_fix.ResponseModel;

import java.time.LocalDateTime;

import com.example.Quick_fix.Enums.PaymentStatus;

import lombok.Data;

@Data
public class PaymentResponseModel {

	private String uniqueId;
	private String bookingUniqueId;
	private String razorpayOrderId;
	private String razorpayPaymentId;
	private Double amount;
	private String currency;
	private String paymentMethod;
	private PaymentStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime paidAt;

	// Receipt breakdown metadata
	private String customerName;
	private String customerPhone;
	private String providerName;
	private String serviceName;
	private Double servicePrice;
	private Double distanceCharge;
	private Double totalAmount;
	private String serviceAddress;
}
