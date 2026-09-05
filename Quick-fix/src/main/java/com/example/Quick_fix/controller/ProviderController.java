
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
import org.springframework.web.bind.annotation.RestController;

import com.example.Quick_fix.ResponseModel.ProviderResponseModel;
import com.example.Quick_fix.requestModel.ProviderRequestModel;
import com.example.Quick_fix.service.ProviderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    @PostMapping
    public ResponseEntity<String> createProvider(
            @RequestBody ProviderRequestModel request) {

            

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(providerService.createProvider(request));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProviderResponseModel> getProviderById(
            @PathVariable Integer id) {

        ProviderResponseModel response =
                providerService.getProviderById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProviderResponseModel>> getAllProviders() {

        List<ProviderResponseModel> response =
                providerService.getAllProviders();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProviderResponseModel> updateProvider(
            @PathVariable Integer id,
            @RequestBody ProviderRequestModel request) {

        ProviderResponseModel response =
                providerService.updateProvider(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProvider(
            @PathVariable Integer id) {

        String response =
                providerService.deleteProvider(id);

        return ResponseEntity.ok(response);
    }
}

