package com.example.Quick_fix.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Quick_fix.Entity.ServiceEntity;
import com.example.Quick_fix.ResponseModel.ServiceResponseModel;
import com.example.Quick_fix.repository.ServiceRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {
    private final ServiceRepository serviceRepository;

    @GetMapping
    public ResponseEntity<List<ServiceResponseModel>> getAllServices() {
        return ResponseEntity.ok(serviceRepository.findAll().stream().filter(service -> Boolean.TRUE.equals(service.getActive()))
                .map(this::toResponse).toList());
    }

    @GetMapping("/{uniqueId}")
    public ResponseEntity<ServiceResponseModel> getService(@PathVariable String uniqueId) {
        return ResponseEntity.ok(toResponse(serviceRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new RuntimeException("Service not found"))));
    }

    private ServiceResponseModel toResponse(ServiceEntity service) {
        ServiceResponseModel response = new ServiceResponseModel();
        response.setId(service.getId());
        response.setUniqueId(service.getUniqueId());
        response.setServiceType(service.getServiceType());
        response.setDescription(service.getDescription());
        response.setServiceCharge(service.getServiceCharge());
        response.setActive(service.getActive());
        return response;
    }
}
