package com.example.Quick_fix.Entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "SERVICE_PACKAGE")
@Data
public class ServicePackageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERVICE_ID", nullable = false)
    private ServiceEntity service;

    @Column(name = "PACKAGE_NAME", nullable = false, length = 150)
    private String packageName;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "PACKAGE_PRICE", nullable = false, precision = 12, scale = 2)
    private BigDecimal packagePrice;

    @Column(name = "ACTIVE")
    private Boolean active;
}
