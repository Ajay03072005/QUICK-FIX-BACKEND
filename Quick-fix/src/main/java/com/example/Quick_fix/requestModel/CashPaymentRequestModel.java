package com.example.Quick_fix.requestModel;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CashPaymentRequestModel {

	@NotBlank(message = "Booking unique ID is required")
	private String bookingUniqueId;

	private String customerUniqueId;
	private String providerUniqueId;
	private String notes;
}
