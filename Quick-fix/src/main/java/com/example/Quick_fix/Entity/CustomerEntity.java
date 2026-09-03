package com.example.Quick_fix.Entity;

import java.time.LocalDate;

import com.example.Quick_fix.Enums.CustomerStatus;
import com.example.Quick_fix.Enums.Gender;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class CustomerEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String firstName;

	private String lastName;

	@Column(unique = true)
	private String phoneNumber;

	private String profileImage;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Enumerated(EnumType.STRING)
	private CustomerStatus status;

	private LocalDate dateOfBirth;
}
