package com.example.Quick_fix.Entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.Quick_fix.Enums.BookingStatus;
import com.example.Quick_fix.Enums.PaymentStatus;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "BOOKING")
public class ServiceBookingEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "unique_id", nullable = false, unique = true, length = 6)
	private String uniqueId;

	@ManyToOne
	@JoinColumn(name = "customer_id", nullable = false)
	private CustomerEntity customer;

	@ManyToOne
	@JoinColumn(name = "service_id", nullable = false)
	private ServiceEntity service;

	@ManyToOne
	@JoinColumn(name = "provider_id")
	private ProviderEntity provider;

	@ManyToOne
	@JoinColumn(name = "address_id", nullable = false)
	private CustomerAddressEntity address;

	@Column(nullable = false)
	private LocalDate bookingDate;

	@Column(nullable = false)
	private LocalTime bookingTime;

	@Column(nullable = false)
	private Double distance;

	@Column(nullable = false)
	private Double servicePrice;

	@Column(nullable = false)
	private Double distanceCharge;

	@Column(nullable = false)
	private Double totalAmount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BookingStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus paymentStatus;
}