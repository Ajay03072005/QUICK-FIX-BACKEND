package com.example.Quick_fix.Entity;

import java.time.LocalDateTime;

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
@Table(name = "SUPPORT_TICKETS")
public class SupportTicketEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_id", nullable = false, unique = true)
    private String uniqueId;

    @Column(nullable = false)
    private String subject;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private ProviderEntity provider;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private ServiceBookingEntity booking;

    @Column(nullable = false)
    private String category = "GENERAL"; // BOOKING, PAYMENT, CONDUCT, TECHNICAL, GENERAL

    @Column(nullable = false)
    private String priority = "NORMAL"; // LOW, NORMAL, HIGH, URGENT

    @Column(nullable = false)
    private String status = "OPEN"; // OPEN, ASSIGNED, IN_PROGRESS, WAITING, RESOLVED, CLOSED

    private String assignedToAgent;

    @Column(columnDefinition = "TEXT")
    private String resolutionNotes;

    private LocalDateTime resolvedAt;
}
