package com.example.Quick_fix.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Quick_fix.ResponseModel.CustomerAddressResponseModel;
import com.example.Quick_fix.ResponseModel.CustomerAuthResponseModel;
import com.example.Quick_fix.ResponseModel.CustomerContactResponseModel;
import com.example.Quick_fix.ResponseModel.CustomerResponseModel;
import com.example.Quick_fix.requestModel.CustomerAddressRequestModel;
import com.example.Quick_fix.requestModel.CustomerAuthRequestModel;
import com.example.Quick_fix.requestModel.CustomerContactRequestModel;
import com.example.Quick_fix.requestModel.CustomerRegisterRequestModel;
import com.example.Quick_fix.requestModel.CustomerRequestModel;
import com.example.Quick_fix.service.CustomerService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/customers")
@AllArgsConstructor
public class CustomerController {

	private final CustomerService customerService;

	// =========================
	// Customer
	// =========================

	@PostMapping
	public ResponseEntity<CustomerResponseModel> createCustomer(@RequestBody CustomerRequestModel request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(request));
	}

	@GetMapping("/{customerId}")
	public ResponseEntity<CustomerResponseModel> getCustomer(@PathVariable Long customerId) {

		return ResponseEntity.ok(customerService.getCustomer(customerId));
	}

	@PutMapping("/{customerId}")
	public ResponseEntity<CustomerResponseModel> updateCustomer(@RequestParam String CustomerUniqueId,
			@RequestBody CustomerRequestModel request) {

		return ResponseEntity.ok(customerService.updateCustomer(CustomerUniqueId, request));
	}

	@DeleteMapping("/{customerId}")
	public ResponseEntity<Void> deleteCustomer(@RequestParam String CustomerUniqueId) {

		customerService.deleteCustomer(CustomerUniqueId);

		return ResponseEntity.noContent().build();
	}

	// =========================
	// Authentication
	// =========================

	@PostMapping("/auth/register")
	public ResponseEntity<String> register(@RequestBody CustomerRegisterRequestModel request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(customerService.register(request));
	}

	@PostMapping("/auth/login")
	public ResponseEntity<CustomerAuthResponseModel> login(@RequestBody CustomerAuthRequestModel request) {

		return ResponseEntity.ok(customerService.login(request));
	}

	// =========================
	// Address
	// =========================

	@PostMapping("/{customerId}/addresses")
	public ResponseEntity<CustomerAddressResponseModel> addAddress(@PathVariable Long customerId,
			@RequestBody CustomerAddressRequestModel request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(customerService.addAddress(customerId, request));
	}

	@GetMapping("/{customerId}/addresses")
	public ResponseEntity<List<CustomerAddressResponseModel>> getAddresses(@RequestParam String CustomerUniqueId) {

		return ResponseEntity.ok(customerService.getAddresses(CustomerUniqueId));
	}

	@PutMapping("/{customerId}/addresses/{addressId}")
	public ResponseEntity<CustomerAddressResponseModel> updateAddress(@RequestParam String CustomerUniqueId,
			@RequestParam String addressUniqueId, @RequestBody CustomerAddressRequestModel request) {

		return ResponseEntity.ok(customerService.updateAddress(CustomerUniqueId, addressUniqueId, request));
	}

	@DeleteMapping("/{customerId}/addresses/{addressId}")
	public ResponseEntity<Void> deleteAddress(@PathVariable Long customerId, @PathVariable Long addressId) {

		customerService.deleteAddress(customerId, addressId);

		return ResponseEntity.noContent().build();
	}

	// =========================
	// Contact
	// =========================

	@PostMapping("/{customerId}/contacts")
	public ResponseEntity<CustomerContactResponseModel> addContact(@PathVariable Long customerId,
			@RequestBody CustomerContactRequestModel request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(customerService.addContact(customerId, request));
	}

	@GetMapping("/{customerId}/contacts")
	public ResponseEntity<List<CustomerContactResponseModel>> getContacts(@PathVariable Long customerId) {

		return ResponseEntity.ok(customerService.getContacts(customerId));
	}

	@PutMapping("/{customerId}/contacts/{contactId}")
	public ResponseEntity<CustomerContactResponseModel> updateContact(@PathVariable Long customerId,
			@PathVariable Long contactId, @RequestBody CustomerContactRequestModel request) {

		return ResponseEntity.ok(customerService.updateContact(customerId, contactId, request));
	}

	@DeleteMapping("/{customerId}/contacts/{contactId}")
	public ResponseEntity<Void> deleteContact(@PathVariable Long customerId, @PathVariable Long contactId) {

		customerService.deleteContact(customerId, contactId);

		return ResponseEntity.noContent().build();
	}
}