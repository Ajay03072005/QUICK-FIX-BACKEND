package com.example.Quick_fix.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Quick_fix.ResponseModel.ServiceGuidanceResponseModel;
import com.example.Quick_fix.service.ServiceGuidanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/guidance")
@RequiredArgsConstructor
public class ServiceGuidanceController {

    private final ServiceGuidanceService guidanceService;

    @GetMapping("/catalogue")
    public ResponseEntity<List<Map<String, Object>>> getCatalogueGuidance(
            @RequestParam(required = false) String providerUniqueId) {
        return ResponseEntity.ok(guidanceService.getCatalogueGuidanceSummary(providerUniqueId));
    }

    @GetMapping("/{serviceUniqueId}")
    public ResponseEntity<ServiceGuidanceResponseModel> getGuidance(
            @PathVariable String serviceUniqueId,
            @RequestParam(required = false) String providerUniqueId) {
        return ResponseEntity.ok(guidanceService.getGuidanceByServiceUniqueId(serviceUniqueId, providerUniqueId));
    }
}
