package com.example.Quick_fix.Entity;

import com.example.Quick_fix.Enums.ServiceType;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "SERVICE")
@Data
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UNIQUE_ID", nullable = false, unique = true, length = 6)
    private String uniqueId;

    @Enumerated(EnumType.STRING)
    @Column(name = "SERVICE_TYPE", nullable = false, unique = true)
    private ServiceType serviceType;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    @Column(name = "SERVICE_CHARGE", nullable = false)
    private Double serviceCharge;

    @Column(name = "ACTIVE", nullable = false)
    private Boolean active;
}