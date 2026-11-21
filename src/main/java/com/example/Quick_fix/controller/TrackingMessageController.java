package com.example.Quick_fix.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import com.example.Quick_fix.requestModel.TrackingLocationMessageModel;
import com.example.Quick_fix.service.TrackingService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Controller
@Validated
@RequiredArgsConstructor
public class TrackingMessageController {

    private final TrackingService trackingService;
    private final Validator validator;

    @MessageMapping("/tracking/location")
    public void recordLocation(TrackingLocationMessageModel message,
            org.springframework.security.core.Authentication authentication) {
        if (message.getBookingUniqueId() == null || message.getBookingUniqueId().isBlank()) {
            throw new IllegalArgumentException("bookingUniqueId is required");
        }
        for (ConstraintViolation<TrackingLocationMessageModel> violation : validator.validate(message)) {
            throw new IllegalArgumentException(violation.getMessage());
        }
        trackingService.recordLocation(
                message.getBookingUniqueId(),
                message.toLocationRequest(),
                authentication);
    }
}
