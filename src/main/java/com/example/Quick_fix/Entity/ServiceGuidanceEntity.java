package com.example.Quick_fix.Entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "SERVICE_GUIDANCE")
@Data
@EqualsAndHashCode(callSuper = false)
public class ServiceGuidanceEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SERVICE_ID", nullable = false, unique = true)
    private ServiceEntity service;

    @Column(name = "OVERVIEW", columnDefinition = "TEXT")
    private String overview;

    @Column(name = "INCLUSIONS", columnDefinition = "TEXT")
    private String inclusions;

    @Column(name = "EXCLUSIONS", columnDefinition = "TEXT")
    private String exclusions;

    @Column(name = "RESPONSIBILITIES", columnDefinition = "TEXT")
    private String responsibilities;

    @Column(name = "MIN_EXPERIENCE_YEARS")
    private Integer minExperienceYears = 1;

    @Column(name = "ESTIMATED_DURATION", length = 100)
    private String estimatedDuration;

    @Column(name = "ESTIMATED_EARNING_MIN")
    private Double estimatedEarningMin;

    @Column(name = "ESTIMATED_EARNING_MAX")
    private Double estimatedEarningMax;

    @Column(name = "EARNING_BASIS", length = 100)
    private String earningBasis;

    @Column(name = "SAFETY_INSTRUCTIONS", columnDefinition = "TEXT")
    private String safetyInstructions;

    @Column(name = "ELIGIBILITY_CRITERIA", columnDefinition = "TEXT")
    private String eligibilityCriteria;

    @Column(name = "ACTIVE")
    private Boolean active = true;

    @OneToMany(mappedBy = "guidance", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ServiceGuidanceItemEntity> items = new ArrayList<>();
}
