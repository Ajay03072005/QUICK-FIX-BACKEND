package com.example.Quick_fix.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Quick_fix.Entity.ServiceEntity;
import com.example.Quick_fix.ResponseModel.*;
import com.example.Quick_fix.repository.ServiceRepository;
import com.example.Quick_fix.requestModel.PricingCalculationRequestModel;
import com.example.Quick_fix.service.DynamicPricingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {
    private final ServiceRepository serviceRepository;
    private final DynamicPricingService dynamicPricingService;

    @GetMapping
    public ResponseEntity<List<ServiceResponseModel>> getAllServices() {
        return ResponseEntity.ok(serviceRepository.findAll().stream()
                .filter(service -> Boolean.TRUE.equals(service.getActive()))
                .map(this::toResponse).toList());
    }

    @GetMapping("/{uniqueId}")
    public ResponseEntity<ServiceResponseModel> getService(@PathVariable String uniqueId) {
        return ResponseEntity.ok(toResponse(serviceRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new RuntimeException("Service not found"))));
    }

    @GetMapping("/{uniqueId}/pricing")
    public ResponseEntity<ServicePricingConfigResponseModel> getServicePricingConfig(@PathVariable String uniqueId) {
        return ResponseEntity.ok(dynamicPricingService.getPricingConfig(uniqueId));
    }

    @GetMapping("/{uniqueId}/options")
    public ResponseEntity<List<ServiceOptionGroupResponseModel>> getServiceOptions(@PathVariable String uniqueId) {
        return ResponseEntity.ok(dynamicPricingService.getOptionGroups(uniqueId));
    }

    @GetMapping("/{uniqueId}/packages")
    public ResponseEntity<List<ServicePackageResponseModel>> getServicePackages(@PathVariable String uniqueId) {
        return ResponseEntity.ok(dynamicPricingService.getPackages(uniqueId));
    }

    @PostMapping("/calculate-price")
    public ResponseEntity<PricingCalculationResponseModel> calculatePrice(@RequestBody PricingCalculationRequestModel request) {
        return ResponseEntity.ok(dynamicPricingService.calculatePrice(request));
    }

    private ServiceResponseModel toResponse(ServiceEntity service) {
        ServiceResponseModel response = new ServiceResponseModel();
        response.setId(service.getId());
        response.setUniqueId(service.getUniqueId());
        response.setServiceType(service.getServiceType());
        response.setPricingModel(service.getPricingModel() != null ? service.getPricingModel() : "FIXED");
        response.setDescription(service.getDescription());
        response.setServiceCharge(service.getServiceCharge());
        response.setActive(service.getActive());
        return response;
    }
}
