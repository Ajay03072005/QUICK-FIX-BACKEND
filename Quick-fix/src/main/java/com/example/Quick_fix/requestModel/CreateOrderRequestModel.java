package com.example.Quick_fix.requestModel;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrderRequestModel {

	@NotBlank(message = "Booking unique ID is required")
	private String bookingUniqueId;
	
	private String customerUniqueId;
}
