package com.example.Quick_fix.requestModel;

import java.time.LocalDate;

import org.hibernate.validator.constraints.UniqueElements;

import com.example.Quick_fix.Enums.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerRegisterRequestModel {

	@NotBlank
	private String email;

	@NotBlank
	private String password;

	@NotBlank
	private String firstName;

	private String lastName;

	@NotBlank
	@UniqueElements
	private String phoneNumber;

	private String profileImage;

	private Gender gender;

	private LocalDate dateOfBirth;
}