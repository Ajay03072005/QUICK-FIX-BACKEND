package com.example.Quick_fix.requestModel;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentVerificationRequestModel {

	@NotBlank(message = "Razorpay Order ID is required")
	private String razorpayOrderId;

	@NotBlank(message = "Razorpay Payment ID is required")
	private String razorpayPaymentId;

	@NotBlank(message = "Razorpay Signature is required")
	private String razorpaySignature;

	private String paymentUniqueId;
	private String customerUniqueId;
}
