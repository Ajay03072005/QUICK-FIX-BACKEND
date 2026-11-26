package com.example.Quick_fix.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "SERVICE_GUIDANCE_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class ServiceGuidanceItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GUIDANCE_ID", nullable = false)
    @JsonIgnore
    private ServiceGuidanceEntity guidance;

    @Column(name = "ITEM_TYPE", length = 30, nullable = false)
    private String itemType; // SKILL, TOOL, MATERIAL

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "IS_MANDATORY")
    private Boolean mandatory = true;

    @Column(name = "NOTES", length = 255)
    private String notes;
}
