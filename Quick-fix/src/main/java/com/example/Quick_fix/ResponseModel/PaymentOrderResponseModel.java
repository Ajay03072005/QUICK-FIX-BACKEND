package com.example.Quick_fix.ResponseModel;

import com.example.Quick_fix.Enums.PaymentStatus;
import lombok.Data;

@Data
public class PaymentOrderResponseModel {

	private String paymentUniqueId;
	private String bookingUniqueId;
	private String razorpayOrderId;
	private String keyId;
	private Long amountInPaise;
	private Double amountInRupees;
	private String currency;
	private PaymentStatus status;

	// Customer prefill info for Razorpay checkout
	private String customerName;
	private String customerEmail;
	private String customerPhone;
	private String serviceName;
}
