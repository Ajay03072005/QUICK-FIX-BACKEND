
package com.example.Quick_fix.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Quick_fix.ResponseModel.ProviderAuthResponseModel;
import com.example.Quick_fix.ResponseModel.ProviderResponseModel;
import com.example.Quick_fix.requestModel.ProviderAuthRequestModel;
import com.example.Quick_fix.requestModel.ProviderRegisterRequestModel;
import com.example.Quick_fix.requestModel.ProviderRequestModel;
import com.example.Quick_fix.service.ProviderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
public class ProviderController {

	private final ProviderService providerService;

	@PostMapping
	public ResponseEntity<String> createProvider(@RequestBody ProviderRequestModel request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(providerService.createProvider(request));
	}

	@PostMapping("/auth/register")
	public ResponseEntity<ProviderAuthResponseModel> registerProvider(@RequestBody ProviderRegisterRequestModel request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(providerService.registerProvider(request));
	}

	@PostMapping("/auth/login")
	public ResponseEntity<ProviderAuthResponseModel> loginProvider(@RequestBody ProviderAuthRequestModel request) {
		return ResponseEntity.ok(providerService.loginProvider(request));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProviderResponseModel> getProviderById(@PathVariable Integer id) {

		ProviderResponseModel response = providerService.getProviderById(id);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/unique/{uniqueId}")
	public ResponseEntity<ProviderResponseModel> getProviderByUniqueId(@PathVariable String uniqueId) {
		return ResponseEntity.ok(providerService.getProviderByUniqueId(uniqueId));
	}

	@GetMapping
	public ResponseEntity<List<ProviderResponseModel>> getAllProviders() {
		List<ProviderResponseModel> response = providerService.getAllProviders();
		return ResponseEntity.ok(response);
	}

	@PutMapping("/unique/{uniqueId}/availability")
	public ResponseEntity<ProviderResponseModel> updateAvailability(@PathVariable String uniqueId,
			@RequestParam String status) {
		return ResponseEntity.ok(providerService.updateAvailability(uniqueId, status));
	}

	@PutMapping("/{id}/availability")
	public ResponseEntity<ProviderResponseModel> updateAvailability(@PathVariable Integer id,
			@RequestParam String status) {
		return ResponseEntity.ok(providerService.updateAvailability(id, status));
	}
	@PutMapping("/{id}")
	public ResponseEntity<ProviderResponseModel> updateProvider(@PathVariable Integer id,
			@RequestBody ProviderRequestModel request) {

		ProviderResponseModel response = providerService.updateProvider(id, request);

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteProvider(@PathVariable Integer id) {

		String response = providerService.deleteProvider(id);

		return ResponseEntity.ok(response);
	}
}
