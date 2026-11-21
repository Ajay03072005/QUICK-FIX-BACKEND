package com.example.Quick_fix.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "COMMISSION_RULES")
public class CommissionRuleEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_id", nullable = false, unique = true)
    private String uniqueId;

    @Column(nullable = false)
    private String ruleName;

    @Column(nullable = false)
    private String calculationType = "PERCENTAGE"; // PERCENTAGE or FIXED

    @Column(nullable = false)
    private Double value = 15.0; // 15% platform commission by default

    @Column(nullable = false)
    private boolean active = true;
}
