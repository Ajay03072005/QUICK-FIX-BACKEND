package com.example.Quick_fix.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "PROVIDER_SERVICE_APPLICATION")
@Data
@EqualsAndHashCode(callSuper = false)
public class ProviderServiceApplicationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "UNIQUE_ID", nullable = false, unique = true, length = 32)
    private String uniqueId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PROVIDER_ID", nullable = false)
    private ProviderEntity provider;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SERVICE_ID", nullable = false)
    private ServiceEntity service;

    @Column(name = "STATUS", length = 50, nullable = false)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED, MORE_INFO_REQUESTED

    @Column(name = "EXPERIENCE_YEARS")
    private Integer experienceYears;

    @Column(name = "CERTIFICATIONS", columnDefinition = "TEXT")
    private String certifications;

    @Column(name = "SERVICE_AREA", length = 255)
    private String serviceArea;

    @Column(name = "NOTES", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "REJECTION_REASON", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "ADMIN_NOTES", columnDefinition = "TEXT")
    private String adminNotes;
}
