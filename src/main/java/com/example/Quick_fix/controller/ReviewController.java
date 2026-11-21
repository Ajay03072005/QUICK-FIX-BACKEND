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

import com.example.Quick_fix.ResponseModel.ReviewResponseModel;
import com.example.Quick_fix.requestModel.ReviewRequestModel;
import com.example.Quick_fix.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponseModel> createReview(
            @RequestParam String customerUniqueId,
            @Valid @RequestBody ReviewRequestModel request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(customerUniqueId, request));
    }

    @GetMapping("/provider/{providerUniqueId}")
    public ResponseEntity<List<ReviewResponseModel>> getProviderReviews(@PathVariable String providerUniqueId) {
        return ResponseEntity.ok(reviewService.getProviderReviews(providerUniqueId));
    }

    @GetMapping("/customer/{customerUniqueId}")
    public ResponseEntity<List<ReviewResponseModel>> getCustomerReviews(@PathVariable String customerUniqueId) {
        return ResponseEntity.ok(reviewService.getCustomerReviews(customerUniqueId));
    }
}
