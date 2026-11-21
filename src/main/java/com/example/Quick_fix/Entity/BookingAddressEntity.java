package com.example.Quick_fix.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "BOOKING_ADDRESS")
@Data
public class BookingAddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "UNIQUE_ID", nullable = false, unique = true, length = 6)
    private String uniqueId;

    @Column(name = "ADDRESS_LINE_1", nullable = false)
    private String addressLine1;

    @Column(name = "ADDRESS_LINE_2")
    private String addressLine2;

    @Column(name = "CITY", nullable = false)
    private String city;

    @Column(name = "STATE", nullable = false)
    private String state;

    @Column(name = "COUNTRY", nullable = false)
    private String country;

    @Column(name = "POSTAL_CODE", nullable = false)
    private String postalCode;

    @Column(name = "LATITUDE", nullable = false)
    private Double latitude;

    @Column(name = "LONGITUDE", nullable = false)
    private Double longitude;
}