package com.example.Quick_fix.Entity;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "SERVICE_OPTION_GROUP")
@Data
public class ServiceOptionGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERVICE_ID", nullable = false)
    private ServiceEntity service;

    @Column(name = "GROUP_NAME", nullable = false, length = 150)
    private String groupName;

    @Column(name = "SELECTION_TYPE", nullable = false, length = 20)
    private String selectionType; // SINGLE, MULTIPLE

    @Column(name = "IS_REQUIRED")
    private Boolean isRequired;

    @Column(name = "MIN_SELECTIONS")
    private Integer minSelections;

    @Column(name = "MAX_SELECTIONS")
    private Integer maxSelections;

    @Column(name = "ACTIVE")
    private Boolean active;

    @Column(name = "INPUT_TYPE", length = 30)
    private String inputType; // SELECT, MULTI_SELECT, NUMBER, AREA, TEXT

    @Column(name = "UNIT_RATE", precision = 12, scale = 2)
    private BigDecimal unitRate;

    @Column(name = "UNIT_LABEL", length = 50)
    private String unitLabel;

    @Column(name = "MIN_VALUE", precision = 12, scale = 2)
    private BigDecimal minValue;

    @Column(name = "MAX_VALUE", precision = 12, scale = 2)
    private BigDecimal maxValue;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ServiceOptionEntity> options;
}
