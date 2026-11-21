package com.example.Quick_fix.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "PROVIDER_EARNINGS")
public class ProviderEarningEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_id", nullable = false, unique = true)
    private String uniqueId;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private ServiceBookingEntity booking;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderEntity provider;

    @Column(nullable = false)
    private Double grossAmount;

    @Column(nullable = false)
    private Double commissionAmount;

    @Column(nullable = false)
    private Double netAmount; // Payable to provider

    @Column(nullable = false)
    private String status = "AVAILABLE"; // PENDING, AVAILABLE, ON_HOLD, PAID
}
