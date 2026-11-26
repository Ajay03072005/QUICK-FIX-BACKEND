package com.example.Quick_fix.Entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "SERVICE_OPTION")
@Data
public class ServiceOptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID", nullable = false)
    private ServiceOptionGroupEntity group;

    @Column(name = "OPTION_NAME", nullable = false, length = 150)
    private String optionName;

    @Column(name = "ADDITIONAL_CHARGE", precision = 12, scale = 2)
    private BigDecimal additionalCharge;

    @Column(name = "ACTIVE")
    private Boolean active;
}
