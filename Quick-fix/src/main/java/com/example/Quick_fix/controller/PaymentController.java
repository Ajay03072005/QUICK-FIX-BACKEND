package com.example.Quick_fix.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Quick_fix.ResponseModel.PaymentOrderResponseModel;
import com.example.Quick_fix.ResponseModel.PaymentResponseModel;
import com.example.Quick_fix.ResponseModel.PaymentVerificationResponseModel;
import com.example.Quick_fix.requestModel.CreateOrderRequestModel;
import com.example.Quick_fix.requestModel.PaymentVerificationRequestModel;
import com.example.Quick_fix.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	// CREATE OR REUSE ORDER
	@PostMapping("/api/payments/orders")
	public ResponseEntity<PaymentOrderResponseModel> createOrder(@Valid @RequestBody CreateOrderRequestModel request) {
		return ResponseEntity.ok(paymentService.createOrder(request));
	}

	// VERIFY CHECKOUT RESPONSE
	@PostMapping("/api/payments/verify")
	public ResponseEntity<PaymentVerificationResponseModel> verifyPayment(
			@Valid @RequestBody PaymentVerificationRequestModel request) {
		return ResponseEntity.ok(paymentService.verifyPayment(request));
	}

	// WEBHOOK ENDPOINT
	@PostMapping("/api/payments/webhook")
	public ResponseEntity<String> handleWebhook(
			@RequestBody String payload,
			@RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {
		String response = paymentService.processWebhook(payload, signature);
		return ResponseEntity.ok(response);
	}

	// FETCH PAYMENT DETAILS BY UNIQUE ID
	@GetMapping("/api/payments/{uniqueId}")
	public ResponseEntity<PaymentResponseModel> getPaymentDetails(@PathVariable String uniqueId) {
		return ResponseEntity.ok(paymentService.getPaymentByUniqueId(uniqueId));
	}

	// FETCH PAYMENT FOR A BOOKING
	@GetMapping("/api/bookings/{bookingUniqueId}/payment")
	public ResponseEntity<PaymentResponseModel> getBookingPayment(@PathVariable String bookingUniqueId) {
		return ResponseEntity.ok(paymentService.getBookingPayment(bookingUniqueId));
	}
}
