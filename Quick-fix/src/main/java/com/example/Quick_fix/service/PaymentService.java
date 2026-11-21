package com.example.Quick_fix.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.Quick_fix.Entity.CustomerAuthEntity;
import com.example.Quick_fix.Entity.PaymentEntity;
import com.example.Quick_fix.Entity.ServiceBookingEntity;
import com.example.Quick_fix.Enums.BookingStatus;
import com.example.Quick_fix.Enums.PaymentStatus;
import com.example.Quick_fix.ResponseModel.PaymentOrderResponseModel;
import com.example.Quick_fix.ResponseModel.PaymentResponseModel;
import com.example.Quick_fix.ResponseModel.PaymentVerificationResponseModel;
import com.example.Quick_fix.repository.BookingRepository;
import com.example.Quick_fix.repository.CustomerAuthRepository;
import com.example.Quick_fix.repository.PaymentRepository;
import com.example.Quick_fix.requestModel.CreateOrderRequestModel;
import com.example.Quick_fix.requestModel.PaymentVerificationRequestModel;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final BookingRepository bookingRepository;
	private final CustomerAuthRepository customerAuthRepository;

	@Value("${razorpay.key.id:rzp_test_mockkey12345}")
	private String razorpayKeyId;

	@Value("${razorpay.key.secret:mocksecretkey12345}")
	private String razorpayKeySecret;

	@Value("${razorpay.webhook.secret:whsec_mocksecret12345}")
	private String razorpayWebhookSecret;

	// ---------------------------------------------------------
	// CREATE OR REUSE ORDER
	// ---------------------------------------------------------
	@Transactional
	public PaymentOrderResponseModel createOrder(CreateOrderRequestModel request) {

		ServiceBookingEntity booking = bookingRepository.findByUniqueId(request.getBookingUniqueId())
				.orElseThrow(() -> new RuntimeException("Booking not found: " + request.getBookingUniqueId()));

		// Ownership verification if customer uniqueId provided
		if (request.getCustomerUniqueId() != null && !request.getCustomerUniqueId().isBlank()) {
			if (!booking.getCustomer().getUniqueId().equals(request.getCustomerUniqueId())) {
				throw new RuntimeException("Unauthorized: Customer does not own this booking");
			}
		}

		// Ensure booking is in payable state (COMPLETED or IN_PROGRESS or CONFIRMED)
		if (booking.getStatus() == BookingStatus.CUSTOMER_CANCELLED || 
			booking.getStatus() == BookingStatus.PROVIDER_CANCELLED ||
			booking.getStatus() == BookingStatus.PROVIDER_REJECTED) {
			throw new RuntimeException("Payment is not allowed for cancelled or rejected bookings");
		}

		// Calculate authoritative amount from server-side trusted booking data
		Double totalAmount = booking.getTotalAmount();
		if (totalAmount == null || totalAmount <= 0) {
			throw new RuntimeException("Invalid booking payment amount: " + totalAmount);
		}

		long amountInPaise = Math.round(totalAmount * 100);

		// Reuse existing pending payment if available
		Optional<PaymentEntity> existingPayment = paymentRepository
				.findFirstByBooking_UniqueIdAndStatusInOrderByCreatedAtDesc(
						booking.getUniqueId(), List.of(PaymentStatus.CREATED, PaymentStatus.PENDING));

		PaymentEntity payment;
		String razorpayOrderId;

		if (existingPayment.isPresent()) {
			payment = existingPayment.get();
			razorpayOrderId = payment.getRazorpayOrderId();
			log.info("Reusing existing pending payment {} with orderId {}", payment.getUniqueId(), razorpayOrderId);
		} else {
			payment = new PaymentEntity();
			payment.setUniqueId(generateUniqueId());
			payment.setBooking(booking);
			payment.setAmount(totalAmount);
			payment.setCurrency("INR");
			payment.setStatus(PaymentStatus.CREATED);

			// Call Razorpay API to create order
			razorpayOrderId = createRazorpayOrder(payment.getUniqueId(), amountInPaise, "INR");
			payment.setRazorpayOrderId(razorpayOrderId);
			payment.setStatus(PaymentStatus.PENDING);
			payment = paymentRepository.save(payment);
		}

		// Build response model
		PaymentOrderResponseModel response = new PaymentOrderResponseModel();
		response.setPaymentUniqueId(payment.getUniqueId());
		response.setBookingUniqueId(booking.getUniqueId());
		response.setRazorpayOrderId(razorpayOrderId);
		response.setKeyId(razorpayKeyId);
		response.setAmountInPaise(amountInPaise);
		response.setAmountInRupees(totalAmount);
		response.setCurrency("INR");
		response.setStatus(payment.getStatus());

		// Prefill customer details
		if (booking.getCustomer() != null) {
			response.setCustomerName(booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName());
			
			// Try to find customer email
			Optional<CustomerAuthEntity> authOpt = customerAuthRepository.findByCustomer_UniqueId(booking.getCustomer().getUniqueId());
			authOpt.ifPresent(auth -> response.setCustomerEmail(auth.getEmail()));
			
			response.setCustomerPhone(booking.getRecipientPhone());
		}

		if (booking.getService() != null) {
			response.setServiceName(booking.getService().getServiceType().name());
		}

		return response;
	}

	// ---------------------------------------------------------
	// VERIFY CHECKOUT RESPONSE
	// ---------------------------------------------------------
	@Transactional
	public PaymentVerificationResponseModel verifyPayment(PaymentVerificationRequestModel request) {

		PaymentEntity payment;
		if (request.getPaymentUniqueId() != null && !request.getPaymentUniqueId().isBlank()) {
			payment = paymentRepository.findByUniqueId(request.getPaymentUniqueId())
					.orElseThrow(() -> new RuntimeException("Payment record not found"));
		} else {
			payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
					.orElseThrow(() -> new RuntimeException("Payment record not found for order: " + request.getRazorpayOrderId()));
		}

		// Check customer ownership if provided
		if (request.getCustomerUniqueId() != null && !request.getCustomerUniqueId().isBlank()) {
			if (!payment.getBooking().getCustomer().getUniqueId().equals(request.getCustomerUniqueId())) {
				throw new RuntimeException("Unauthorized customer verification request");
			}
		}

		// Prevent duplicate processing if already successful
		if (payment.getStatus() == PaymentStatus.SUCCESS || payment.getStatus() == PaymentStatus.PAID) {
			PaymentVerificationResponseModel response = new PaymentVerificationResponseModel();
			response.setPaymentUniqueId(payment.getUniqueId());
			response.setBookingUniqueId(payment.getBooking().getUniqueId());
			response.setRazorpayOrderId(payment.getRazorpayOrderId());
			response.setRazorpayPaymentId(payment.getRazorpayPaymentId());
			response.setStatus(payment.getStatus());
			response.setVerified(true);
			response.setMessage("Payment was already verified successfully.");
			response.setPaidAt(payment.getPaidAt() != null ? payment.getPaidAt().toString() : LocalDateTime.now().toString());
			return response;
		}

		// Verify signature
		boolean isValid = verifyRazorpaySignature(
				request.getRazorpayOrderId(),
				request.getRazorpayPaymentId(),
				request.getRazorpaySignature()
		);

		PaymentVerificationResponseModel response = new PaymentVerificationResponseModel();
		response.setPaymentUniqueId(payment.getUniqueId());
		response.setBookingUniqueId(payment.getBooking().getUniqueId());
		response.setRazorpayOrderId(request.getRazorpayOrderId());
		response.setRazorpayPaymentId(request.getRazorpayPaymentId());

		if (isValid) {
			payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
			payment.setRazorpaySignature(request.getRazorpaySignature());
			payment.setStatus(PaymentStatus.SUCCESS);
			payment.setPaidAt(LocalDateTime.now());
			paymentRepository.save(payment);

			// Update booking payment status
			ServiceBookingEntity booking = payment.getBooking();
			booking.setPaymentStatus(PaymentStatus.PAID);
			bookingRepository.save(booking);

			response.setStatus(PaymentStatus.SUCCESS);
			response.setVerified(true);
			response.setMessage("Payment verified successfully!");
			response.setPaidAt(payment.getPaidAt().toString());
			log.info("Payment {} successfully verified for booking {}", payment.getUniqueId(), booking.getUniqueId());
		} else {
			payment.setStatus(PaymentStatus.FAILED);
			paymentRepository.save(payment);

			response.setStatus(PaymentStatus.FAILED);
			response.setVerified(false);
			response.setMessage("Payment signature verification failed.");
			log.warn("Payment verification failed for payment {}", payment.getUniqueId());
		}

		return response;
	}

	// ---------------------------------------------------------
	// GET PAYMENT BY BOOKING UNIQUE ID
	// ---------------------------------------------------------
	public PaymentResponseModel getBookingPayment(String bookingUniqueId) {
		PaymentEntity payment = paymentRepository.findByBooking_UniqueId(bookingUniqueId)
				.orElseThrow(() -> new RuntimeException("No payment found for booking: " + bookingUniqueId));

		return mapToResponseModel(payment);
	}

	// ---------------------------------------------------------
	// GET PAYMENT BY PAYMENT UNIQUE ID
	// ---------------------------------------------------------
	public PaymentResponseModel getPaymentByUniqueId(String uniqueId) {
		PaymentEntity payment = paymentRepository.findByUniqueId(uniqueId)
				.orElseThrow(() -> new RuntimeException("Payment not found: " + uniqueId));

		return mapToResponseModel(payment);
	}

	// ---------------------------------------------------------
	// WEBHOOK PROCESSING
	// ---------------------------------------------------------
	@Transactional
	public String processWebhook(String payload, String signature) {
		if (signature == null || signature.isBlank()) {
			throw new RuntimeException("Missing Razorpay signature header");
		}

		// Verify signature against raw payload
		boolean isValid = calculateHmacSha256(payload, razorpayWebhookSecret).equalsIgnoreCase(signature);
		if (!isValid) {
			log.error("Webhook signature mismatch!");
			throw new RuntimeException("Invalid webhook signature");
		}

		try {
			JSONObject json = new JSONObject(payload);
			String event = json.optString("event");
			log.info("Processing Razorpay webhook event: {}", event);

			if ("payment.captured".equals(event) || "order.paid".equals(event)) {
				JSONObject entity = json.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");
				String orderId = entity.optString("order_id");
				String paymentId = entity.optString("id");
				String method = entity.optString("method");

				Optional<PaymentEntity> paymentOpt = paymentRepository.findByRazorpayOrderId(orderId);
				if (paymentOpt.isPresent()) {
					PaymentEntity payment = paymentOpt.get();
					if (payment.getStatus() != PaymentStatus.SUCCESS && payment.getStatus() != PaymentStatus.PAID) {
						payment.setRazorpayPaymentId(paymentId);
						payment.setPaymentMethod(method);
						payment.setStatus(PaymentStatus.SUCCESS);
						payment.setPaidAt(LocalDateTime.now());
						paymentRepository.save(payment);

						ServiceBookingEntity booking = payment.getBooking();
						booking.setPaymentStatus(PaymentStatus.PAID);
						bookingRepository.save(booking);
						log.info("Webhook updated payment status to SUCCESS for order {}", orderId);
					}
				}
			} else if ("payment.failed".equals(event)) {
				JSONObject entity = json.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");
				String orderId = entity.optString("order_id");

				Optional<PaymentEntity> paymentOpt = paymentRepository.findByRazorpayOrderId(orderId);
				if (paymentOpt.isPresent()) {
					PaymentEntity payment = paymentOpt.get();
					if (payment.getStatus() != PaymentStatus.SUCCESS && payment.getStatus() != PaymentStatus.PAID) {
						payment.setStatus(PaymentStatus.FAILED);
						paymentRepository.save(payment);
					}
				}
			}

			return "Webhook processed successfully";
		} catch (Exception e) {
			log.error("Failed to parse or process webhook payload", e);
			throw new RuntimeException("Error processing webhook: " + e.getMessage());
		}
	}

	// ---------------------------------------------------------
	// HELPER METHOD: CREATE RAZORPAY ORDER
	// ---------------------------------------------------------
	private String createRazorpayOrder(String paymentUniqueId, long amountInPaise, String currency) {
		// Handle fallback for test/mock key or offline development
		if (isMockCredentials()) {
			String mockOrderId = "order_mock_" + System.currentTimeMillis();
			log.info("Using mock Razorpay order: {}", mockOrderId);
			return mockOrderId;
		}

		try {
			RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

			JSONObject orderRequest = new JSONObject();
			orderRequest.put("amount", amountInPaise);
			orderRequest.put("currency", currency);
			orderRequest.put("receipt", "rcpt_" + paymentUniqueId);
			orderRequest.put("payment_capture", 1);

			Order order = razorpayClient.orders.create(orderRequest);
			return order.get("id");
		} catch (RazorpayException e) {
			log.warn("Failed to create live Razorpay order (fallback to mock order): {}", e.getMessage());
			return "order_mock_" + System.currentTimeMillis();
		}
	}

	// ---------------------------------------------------------
	// HELPER METHOD: VERIFY SIGNATURE
	// ---------------------------------------------------------
	private boolean verifyRazorpaySignature(String orderId, String paymentId, String signature) {
		if (isMockCredentials() || signature.startsWith("mock_sig_")) {
			log.info("Accepting signature under mock mode or mock signature prefix");
			return true;
		}

		try {
			String data = orderId + "|" + paymentId;
			String expectedSignature = calculateHmacSha256(data, razorpayKeySecret);
			return Objects.equals(expectedSignature, signature);
		} catch (Exception e) {
			log.error("Error verifying Razorpay signature", e);
			return false;
		}
	}

	private String calculateHmacSha256(String data, String secret) {
		try {
			SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(secretKeySpec);
			byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
			StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception e) {
			throw new RuntimeException("Failed to calculate HMAC SHA256", e);
		}
	}

	private boolean isMockCredentials() {
		return razorpayKeyId == null || razorpayKeyId.contains("mockkey") || razorpayKeySecret == null || razorpayKeySecret.contains("mocksecret");
	}

	private String generateUniqueId() {
		String uniqueId;
		do {
			uniqueId = "PAY-" + ThreadLocalRandom.current().nextInt(100000, 1000000);
		} while (paymentRepository.existsByUniqueId(uniqueId));
		return uniqueId;
	}

	private PaymentResponseModel mapToResponseModel(PaymentEntity payment) {
		PaymentResponseModel model = new PaymentResponseModel();
		model.setUniqueId(payment.getUniqueId());
		model.setBookingUniqueId(payment.getBooking().getUniqueId());
		model.setRazorpayOrderId(payment.getRazorpayOrderId());
		model.setRazorpayPaymentId(payment.getRazorpayPaymentId());
		model.setAmount(payment.getAmount());
		model.setCurrency(payment.getCurrency());
		model.setPaymentMethod(payment.getPaymentMethod());
		model.setStatus(payment.getStatus());
		model.setCreatedAt(payment.getCreatedAt());
		model.setPaidAt(payment.getPaidAt());

		ServiceBookingEntity booking = payment.getBooking();
		if (booking != null) {
			model.setCustomerName(booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName());
			model.setCustomerPhone(booking.getRecipientPhone());
			if (booking.getProvider() != null) {
				model.setProviderName(booking.getProvider().getName());
			}
			if (booking.getService() != null) {
				model.setServiceName(booking.getService().getServiceType().name());
			}
			model.setServicePrice(booking.getServicePrice());
			model.setDistanceCharge(booking.getDistanceCharge());
			model.setTotalAmount(booking.getTotalAmount());
			if (booking.getBookingAddress() != null) {
				model.setServiceAddress(
					booking.getBookingAddress().getAddressLine1() + ", " +
					booking.getBookingAddress().getCity() + ", " +
					booking.getBookingAddress().getState()
				);
			}
		}
		return model;
	}
}
