package com.example.Quick_fix.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Quick_fix.ResponseModel.ProviderServiceApplicationResponseModel;
import com.example.Quick_fix.requestModel.ProviderServiceApplicationRequestModel;
import com.example.Quick_fix.service.ProviderServiceApplicationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/provider/service-applications")
@RequiredArgsConstructor
public class ProviderServiceApplicationController {

    private final ProviderServiceApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ProviderServiceApplicationResponseModel> submitApplication(
            @RequestParam String providerUniqueId,
            @RequestBody ProviderServiceApplicationRequestModel request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.applyForService(providerUniqueId, request));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ProviderServiceApplicationResponseModel>> getMyApplications(
            @RequestParam String providerUniqueId) {
        return ResponseEntity.ok(applicationService.getApplicationsByProvider(providerUniqueId));
    }

    @GetMapping("/{uniqueId}")
    public ResponseEntity<ProviderServiceApplicationResponseModel> getApplication(
            @PathVariable String uniqueId) {
        return ResponseEntity.ok(applicationService.getApplicationByUniqueId(uniqueId));
    }
}
