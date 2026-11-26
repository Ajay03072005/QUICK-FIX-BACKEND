package com.example.Quick_fix.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Quick_fix.ResponseModel.ProviderServiceApplicationResponseModel;
import com.example.Quick_fix.ResponseModel.ServiceGuidanceResponseModel;
import com.example.Quick_fix.requestModel.ProviderServiceApplicationReviewRequestModel;
import com.example.Quick_fix.requestModel.ServiceGuidanceRequestModel;
import com.example.Quick_fix.service.ProviderServiceApplicationService;
import com.example.Quick_fix.service.ServiceGuidanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminServiceApplicationController {

    private final ProviderServiceApplicationService applicationService;
    private final ServiceGuidanceService guidanceService;

    @GetMapping("/service-applications")
    public ResponseEntity<List<ProviderServiceApplicationResponseModel>> listApplications(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(applicationService.getAllApplications(status));
    }

    @GetMapping("/service-applications/{uniqueId}")
    public ResponseEntity<ProviderServiceApplicationResponseModel> getApplication(
            @PathVariable String uniqueId) {
        return ResponseEntity.ok(applicationService.getApplicationByUniqueId(uniqueId));
    }

    @PutMapping("/service-applications/{uniqueId}/review")
    public ResponseEntity<ProviderServiceApplicationResponseModel> reviewApplication(
            @PathVariable String uniqueId,
            @RequestBody ProviderServiceApplicationReviewRequestModel request) {
        return ResponseEntity.ok(applicationService.reviewApplication(uniqueId, request));
    }

    @GetMapping("/guidance/{serviceUniqueId}")
    public ResponseEntity<ServiceGuidanceResponseModel> getGuidance(
            @PathVariable String serviceUniqueId) {
        return ResponseEntity.ok(guidanceService.getGuidanceByServiceUniqueId(serviceUniqueId, null));
    }

    @PostMapping("/guidance")
    public ResponseEntity<ServiceGuidanceResponseModel> saveGuidance(
            @RequestBody ServiceGuidanceRequestModel request) {
        return ResponseEntity.ok(guidanceService.saveOrUpdateGuidance(request));
    }
}
