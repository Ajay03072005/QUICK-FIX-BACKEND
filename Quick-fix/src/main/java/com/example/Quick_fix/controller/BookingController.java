package com.example.Quick_fix.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Quick_fix.service.BookingService;
import com.example.Quick_fix.requestModel.BookingRequestModel;
import com.example.Quick_fix.requestModel.ProviderSuggestionRequestModel;
import com.example.Quick_fix.ResponseModel.BookingResponseModel;
import com.example.Quick_fix.ResponseModel.ProviderSuggestionResponseModel;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

	private final BookingService bookingService;

	// CREATE BOOKING REQUEST
	@PostMapping
	public ResponseEntity<BookingResponseModel> createBooking(@RequestParam String customerUniqueId,
			@Valid @RequestBody BookingRequestModel request) {

		return ResponseEntity.ok(bookingService.createBooking(customerUniqueId, request));
	}

	// FIND NEARBY PROVIDERS
	@GetMapping("/{bookingUniqueId}/providers")
	public ResponseEntity<List<ProviderSuggestionResponseModel>> findProviders(@PathVariable String bookingUniqueId) {

		return ResponseEntity.ok(bookingService.findProviders(bookingUniqueId));
	}

	// SELECT PROVIDER
	@PutMapping("/{bookingUniqueId}/provider")
	public ResponseEntity<BookingResponseModel> selectProvider(@PathVariable String bookingUniqueId,
			@Valid @RequestBody ProviderSuggestionRequestModel request) {

		return ResponseEntity.ok(bookingService.selectProvider(bookingUniqueId, request));
	}

	// PROVIDER ACCEPT
	@PutMapping("/{bookingUniqueId}/accept")
	public ResponseEntity<BookingResponseModel> acceptBooking(@PathVariable String bookingUniqueId) {

		return ResponseEntity.ok(bookingService.acceptBooking(bookingUniqueId));
	}

	// PROVIDER REJECT
	@PutMapping("/{bookingUniqueId}/reject")
	public ResponseEntity<BookingResponseModel> rejectBooking(@PathVariable String bookingUniqueId) {

		return ResponseEntity.ok(bookingService.rejectBooking(bookingUniqueId));
	}

	// CUSTOMER CANCEL
	@PutMapping("/{bookingUniqueId}/cancel")
	public ResponseEntity<BookingResponseModel> cancelBooking(@PathVariable String bookingUniqueId) {

		return ResponseEntity.ok(bookingService.cancelBooking(bookingUniqueId));
	}

	// GET BOOKING
	@GetMapping("/{bookingUniqueId}")
	public ResponseEntity<BookingResponseModel> getBooking(@PathVariable String bookingUniqueId) {

		return ResponseEntity.ok(bookingService.getBookingResponse(bookingUniqueId));
	}

	// DELETE BOOKING
	@DeleteMapping("/{bookingUniqueId}")
	public ResponseEntity<Void> deleteBooking(@PathVariable String bookingUniqueId) {

		bookingService.deleteBooking(bookingUniqueId);

		return ResponseEntity.noContent().build();
	}

	// START SERVICE
	@PutMapping("/{bookingUniqueId}/start")
	public ResponseEntity<BookingResponseModel> startBooking(@PathVariable String bookingUniqueId) {

		return ResponseEntity.ok(bookingService.startBooking(bookingUniqueId));
	}

	// COMPLETE SERVICE
	@PutMapping("/{bookingUniqueId}/complete")
	public ResponseEntity<BookingResponseModel> completeBooking(@PathVariable String bookingUniqueId) {

		return ResponseEntity.ok(bookingService.completeBooking(bookingUniqueId));
	}

	// PROVIDER CANCEL
	@PutMapping("/{bookingUniqueId}/provider-cancel")
	public ResponseEntity<BookingResponseModel> providerCancelBooking(@PathVariable String bookingUniqueId) {

		return ResponseEntity.ok(bookingService.providerCancelBooking(bookingUniqueId));
	}
}