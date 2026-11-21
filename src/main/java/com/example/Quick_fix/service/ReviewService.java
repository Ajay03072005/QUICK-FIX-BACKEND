package com.example.Quick_fix.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Quick_fix.Entity.CustomerEntity;
import com.example.Quick_fix.Entity.ReviewEntity;
import com.example.Quick_fix.Entity.ServiceBookingEntity;
import com.example.Quick_fix.Enums.BookingStatus;
import com.example.Quick_fix.ResponseModel.ReviewResponseModel;
import com.example.Quick_fix.commons.Common;
import com.example.Quick_fix.repository.BookingRepository;
import com.example.Quick_fix.repository.CustomerRepository;
import com.example.Quick_fix.repository.ReviewRepository;
import com.example.Quick_fix.requestModel.ReviewRequestModel;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final Common commons;

    @Transactional
    public ReviewResponseModel createReview(String customerUniqueId, ReviewRequestModel request) {
        CustomerEntity customer = customerRepository.findByUniqueId(customerUniqueId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        ServiceBookingEntity booking = bookingRepository.findByUniqueId(request.getBookingUniqueId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("Booking does not belong to this customer");
        }

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new RuntimeException("Only completed bookings can be reviewed");
        }

        if (reviewRepository.existsByBooking_UniqueId(booking.getUniqueId())) {
            throw new RuntimeException("A review has already been submitted for this booking");
        }

        if (booking.getProvider() == null) {
            throw new RuntimeException("No provider assigned to this booking");
        }

        ReviewEntity review = new ReviewEntity();
        review.setUniqueId(commons.generateUniqueId());
        review.setBooking(booking);
        review.setCustomer(customer);
        review.setProvider(booking.getProvider());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setStatus("PUBLISHED");

        review = reviewRepository.save(review);
        return mapReviewResponse(review);
    }

    public List<ReviewResponseModel> getProviderReviews(String providerUniqueId) {
        return reviewRepository.findByProvider_UniqueId(providerUniqueId)
                .stream().map(this::mapReviewResponse).toList();
    }

    public List<ReviewResponseModel> getCustomerReviews(String customerUniqueId) {
        return reviewRepository.findByCustomer_UniqueId(customerUniqueId)
                .stream().map(this::mapReviewResponse).toList();
    }

    private ReviewResponseModel mapReviewResponse(ReviewEntity review) {
        ReviewResponseModel response = new ReviewResponseModel();
        response.setUniqueId(review.getUniqueId());
        response.setBookingUniqueId(review.getBooking().getUniqueId());
        response.setCustomerUniqueId(review.getCustomer().getUniqueId());
        response.setCustomerName(review.getCustomer().getFirstName() + " " + (review.getCustomer().getLastName() != null ? review.getCustomer().getLastName() : ""));
        response.setProviderUniqueId(review.getProvider().getUniqueId());
        response.setProviderName(review.getProvider().getName());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setStatus(review.getStatus());
        response.setCreatedAt(review.getCreatedAt() != null ? review.getCreatedAt() : java.time.LocalDateTime.now());
        return response;
    }
}
