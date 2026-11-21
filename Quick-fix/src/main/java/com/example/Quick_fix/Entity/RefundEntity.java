package com.example.Quick_fix.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "REFUNDS")
public class RefundEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_id", nullable = false, unique = true)
    private String uniqueId;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentEntity payment;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private ServiceBookingEntity booking;

    @Column(nullable = false)
    private Double amount;

    private String reason;

    @Column(nullable = false)
    private String status = "REQUESTED"; // REQUESTED, PROCESSING, SUCCEEDED, FAILED

    private String gatewayRefundRef;
}
