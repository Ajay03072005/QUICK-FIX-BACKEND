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
@Table(name = "FINANCIAL_LEDGER_ENTRIES")
public class LedgerEntryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_id", nullable = false, unique = true)
    private String uniqueId;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private ProviderEntity provider;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private ServiceBookingEntity booking;

    @Column(nullable = false)
    private String entryType; // CUSTOMER_CAPTURE, COMMISSION, PROVIDER_EARNING, REFUND, PAYOUT

    @Column(nullable = false)
    private String direction; // DEBIT or CREDIT

    @Column(nullable = false)
    private Double amount;

    private String currency = "INR";

    private String description;
}
