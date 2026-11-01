package com.example.Quick_fix.ResponseModel;

import com.example.Quick_fix.Enums.PaymentStatus;
import lombok.Data;

@Data
public class PaymentVerificationResponseModel {

	private String paymentUniqueId;
	private String bookingUniqueId;
	private String razorpayOrderId;
	private String razorpayPaymentId;
	private PaymentStatus status;
	private boolean verified;
	private String message;
	private String paidAt;
}
