package com.example.Quick_fix.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Quick_fix.requestModel.CustomerAddressRequestModel;
import com.example.Quick_fix.requestModel.CustomerAuthRequestModel;
import com.example.Quick_fix.requestModel.CustomerContactRequestModel;
import com.example.Quick_fix.requestModel.CustomerRegisterRequestModel;
import com.example.Quick_fix.requestModel.CustomerRequestModel;
import com.example.Quick_fix.ResponseModel.CustomerAddressResponseModel;
import com.example.Quick_fix.ResponseModel.CustomerAuthResponseModel;
import com.example.Quick_fix.ResponseModel.CustomerContactResponseModel;
import com.example.Quick_fix.ResponseModel.CustomerResponseModel;
import com.example.Quick_fix.service.CustomerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

	private final CustomerService customerService;

	// =========================
	// CUSTOMER
	// =========================

	@PostMapping
	public ResponseEntity<CustomerResponseModel> createCustomer(@RequestBody CustomerRequestModel request) {

		return ResponseEntity.ok(customerService.createCustomer(request));
	}

	@GetMapping("/{customerUniqueId}")
	public ResponseEntity<CustomerResponseModel> getCustomer(@PathVariable String customerUniqueId) {

		return ResponseEntity.ok(customerService.getCustomer(customerUniqueId));
	}

	@PutMapping("/{customerUniqueId}")
	public ResponseEntity<CustomerResponseModel> updateCustomer(@PathVariable String customerUniqueId,
			@RequestBody CustomerRequestModel request) {

		return ResponseEntity.ok(customerService.updateCustomer(customerUniqueId, request));
	}

	@DeleteMapping("/{customerUniqueId}")
	public ResponseEntity<Void> deleteCustomer(@PathVariable String customerUniqueId) {

		customerService.deleteCustomer(customerUniqueId);

		return ResponseEntity.noContent().build();
	}

	// =========================
	// CUSTOMER AUTH
	// =========================

	@PostMapping("/auth/register")
	public ResponseEntity<String> register(@RequestBody CustomerRegisterRequestModel request) {

		return ResponseEntity.ok(customerService.register(request));
	}

	@PostMapping("/auth/login")
	public ResponseEntity<CustomerAuthResponseModel> login(@RequestBody CustomerAuthRequestModel request) {

		return ResponseEntity.ok(customerService.login(request));
	}

	// =========================
	// CUSTOMER ADDRESS
	// =========================

	@PostMapping("/{customerUniqueId}/addresses")
	public ResponseEntity<CustomerAddressResponseModel> addAddress(@PathVariable String customerUniqueId,
			@RequestBody CustomerAddressRequestModel request) {

		return ResponseEntity.ok(customerService.addAddress(customerUniqueId, request));
	}

	@GetMapping("/{customerUniqueId}/addresses")
	public ResponseEntity<List<CustomerAddressResponseModel>> getAddresses(@PathVariable String customerUniqueId) {

		return ResponseEntity.ok(customerService.getAddresses(customerUniqueId));
	}

	@PutMapping("/{customerUniqueId}/addresses/{addressUniqueId}")
	public ResponseEntity<CustomerAddressResponseModel> updateAddress(@PathVariable String customerUniqueId,
			@PathVariable String addressUniqueId, @RequestBody CustomerAddressRequestModel request) {

		return ResponseEntity.ok(customerService.updateAddress(customerUniqueId, addressUniqueId, request));
	}

	@DeleteMapping("/{customerUniqueId}/addresses/{addressUniqueId}")
	public ResponseEntity<Void> deleteAddress(@PathVariable String customerUniqueId,
			@PathVariable String addressUniqueId) {

		customerService.deleteAddress(customerUniqueId, addressUniqueId);

		return ResponseEntity.noContent().build();
	}

	// =========================
	// CUSTOMER CONTACT
	// =========================

	@PostMapping("/{customerUniqueId}/contacts")
	public ResponseEntity<CustomerContactResponseModel> addContact(@PathVariable String customerUniqueId,
			@RequestBody CustomerContactRequestModel request) {

		return ResponseEntity.ok(customerService.addContact(customerUniqueId, request));
	}

	@GetMapping("/{customerUniqueId}/contacts")
	public ResponseEntity<List<CustomerContactResponseModel>> getContacts(@PathVariable String customerUniqueId) {

		return ResponseEntity.ok(customerService.getContacts(customerUniqueId));
	}

	@PutMapping("/{customerUniqueId}/contacts/{contactUniqueId}")
	public ResponseEntity<CustomerContactResponseModel> updateContact(@PathVariable String customerUniqueId,
			@PathVariable String contactUniqueId, @RequestBody CustomerContactRequestModel request) {

		return ResponseEntity.ok(customerService.updateContact(customerUniqueId, contactUniqueId, request));
	}

	@DeleteMapping("/{customerUniqueId}/contacts/{contactUniqueId}")
	public ResponseEntity<Void> deleteContact(@PathVariable String customerUniqueId,
			@PathVariable String contactUniqueId) {

		customerService.deleteContact(customerUniqueId, contactUniqueId);

		return ResponseEntity.noContent().build();
	}
}