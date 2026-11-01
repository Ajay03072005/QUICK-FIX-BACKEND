package com.example.Quick_fix.Entity;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "PROVIDER_AVAILABILITY")
public class ProviderAvailabilityEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderEntity provider;

    @Column(nullable = false)
    private Integer weekday; // 0=Sunday, 1=Monday, ... 6=Saturday

    private LocalTime startTime;

    private LocalTime endTime;

    @Column(nullable = false)
    private boolean isWorkingDay = true;
}
