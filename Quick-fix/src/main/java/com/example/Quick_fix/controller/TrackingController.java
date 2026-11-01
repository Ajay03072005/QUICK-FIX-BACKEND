package com.example.Quick_fix.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Quick_fix.ResponseModel.TrackingLocationResponseModel;
import com.example.Quick_fix.requestModel.TrackingLocationRequestModel;
import com.example.Quick_fix.service.TrackingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/bookings/{bookingUniqueId}/tracking")
public class TrackingController {

    private final TrackingService trackingService;

    @GetMapping("/latest")
    public ResponseEntity<TrackingLocationResponseModel> getLatest(
            @PathVariable String bookingUniqueId,
            Authentication authentication) {
        TrackingLocationResponseModel response =
                trackingService.getLatestLocation(bookingUniqueId, authentication);
        return response == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @PostMapping("/location")
    public ResponseEntity<TrackingLocationResponseModel> recordLocation(
            @PathVariable String bookingUniqueId,
            @Valid @RequestBody TrackingLocationRequestModel request,
            Authentication authentication) {
        return ResponseEntity.ok(trackingService.recordLocation(bookingUniqueId, request, authentication));
    }
}
