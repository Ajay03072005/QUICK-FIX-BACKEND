package com.example.Quick_fix.Entity;

import java.time.LocalDateTime;

import com.example.Quick_fix.Enums.PaymentStatus;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "PAYMENT", indexes = {
		@Index(name = "idx_payment_booking", columnList = "booking_id"),
		@Index(name = "idx_payment_order", columnList = "razorpay_order_id"),
		@Index(name = "idx_payment_status", columnList = "status")
})
public class PaymentEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "unique_id", nullable = false, unique = true, length = 50)
	private String uniqueId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "booking_id", nullable = false)
	private ServiceBookingEntity booking;

	@Column(name = "razorpay_order_id", length = 100)
	private String razorpayOrderId;

	@Column(name = "razorpay_payment_id", length = 100)
	private String razorpayPaymentId;

	@Column(name = "razorpay_signature", length = 255)
	private String razorpaySignature;

	@Column(name = "amount", nullable = false)
	private Double amount;

	@Column(name = "currency", nullable = false, length = 3)
	private String currency = "INR";

	@Column(name = "payment_method", length = 30)
	private String paymentMethod;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 30)
	private PaymentStatus status;

	@Column(name = "paid_at")
	private LocalDateTime paidAt;
}
