package com.example.Quick_fix.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "BOOKING_PRICING_DETAIL")
@Data
public class BookingPricingDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOKING_ID", nullable = false)
    private ServiceBookingEntity booking;

    @Column(name = "PRICING_MODEL", nullable = false, length = 30)
    private String pricingModel;

    @Column(name = "BASE_CHARGE", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseCharge;

    @Column(name = "OPTIONS_CHARGE", nullable = false, precision = 12, scale = 2)
    private BigDecimal optionsCharge;

    @Column(name = "PACKAGE_CHARGE", nullable = false, precision = 12, scale = 2)
    private BigDecimal packageCharge;

    @Column(name = "UNIT_RATE", precision = 12, scale = 2)
    private BigDecimal unitRate;

    @Column(name = "QUANTITY", precision = 12, scale = 2)
    private BigDecimal quantity;

    @Column(name = "UNIT_LABEL", length = 50)
    private String unitLabel;

    @Column(name = "PACKAGE_ID")
    private Long packageId;

    @Column(name = "PACKAGE_NAME", length = 150)
    private String packageName;

    @Column(name = "PRICING_SNAPSHOT_JSON", columnDefinition = "TEXT", nullable = false)
    private String pricingSnapshotJson;

    @Column(name = "CUSTOMER_NOTES", columnDefinition = "TEXT")
    private String customerNotes;

    @Column(name = "QUOTATION_STATUS", length = 30)
    private String quotationStatus; // PENDING_QUOTE, QUOTED, ACCEPTED, REJECTED

    @Column(name = "QUOTATION_NOTES", columnDefinition = "TEXT")
    private String quotationNotes;

    @Column(name = "QUOTED_PRICE", precision = 12, scale = 2)
    private BigDecimal quotedPrice;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
