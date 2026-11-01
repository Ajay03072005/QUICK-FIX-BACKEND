package com.example.Quick_fix.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.Quick_fix.Entity.BookingTrackingPointEntity;
import com.example.Quick_fix.Entity.CustomerEntity;
import com.example.Quick_fix.Entity.ProviderEntity;
import com.example.Quick_fix.Entity.ServiceBookingEntity;
import com.example.Quick_fix.Enums.BookingStatus;
import com.example.Quick_fix.ResponseModel.TrackingLocationResponseModel;
import com.example.Quick_fix.repository.BookingRepository;
import com.example.Quick_fix.repository.BookingTrackingPointRepository;
import com.example.Quick_fix.repository.CustomerAuthRepository;
import com.example.Quick_fix.repository.ProviderAuthRepository;
import com.example.Quick_fix.requestModel.TrackingLocationRequestModel;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrackingService {

    private static final Duration MAX_RECORDED_AGE = Duration.ofMinutes(2);
    private static final Duration STALE_AFTER = Duration.ofSeconds(60);
    private static final Duration MIN_UPDATE_INTERVAL = Duration.ofSeconds(1);

    private final BookingRepository bookingRepository;
    private final BookingTrackingPointRepository trackingPointRepository;
    private final CustomerAuthRepository customerAuthRepository;
    private final ProviderAuthRepository providerAuthRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private final ConcurrentMap<String, LocalDateTime> lastAcceptedUpdate = new ConcurrentHashMap<>();

    @Transactional
    public TrackingLocationResponseModel recordLocation(
            String bookingUniqueId,
            TrackingLocationRequestModel request,
            Authentication authentication) {
        if (request == null || request.getLatitude() == null || request.getLongitude() == null
                || request.getRecordedAt() == null) {
            throw new IllegalArgumentException("Latitude, longitude, and recordedAt are required");
        }
        ServiceBookingEntity booking = getBooking(bookingUniqueId);
        ProviderEntity provider = requireAssignedProvider(booking, authentication);
        validateTravelState(booking);

        LocalDateTime now = LocalDateTime.now();
        if (request.getRecordedAt().isAfter(now.plusSeconds(5))
                || Duration.between(request.getRecordedAt(), now).compareTo(MAX_RECORDED_AGE) > 0) {
            throw new IllegalArgumentException("Location timestamp is outside the accepted window");
        }

        BookingTrackingPointEntity latest = trackingPointRepository
                .findFirstByBooking_UniqueIdOrderByRecordedAtDesc(bookingUniqueId)
                .orElse(null);
        if (latest != null && !request.getRecordedAt().isAfter(latest.getRecordedAt())) {
            throw new IllegalArgumentException("Location timestamp must be newer than the latest update");
        }

        String updateKey = bookingUniqueId + ":" + provider.getUniqueId();
        LocalDateTime previousAccepted = lastAcceptedUpdate.get(updateKey);
        if (previousAccepted != null
                && Duration.between(previousAccepted, now).compareTo(MIN_UPDATE_INTERVAL) < 0) {
            throw new IllegalArgumentException("Location updates are arriving too frequently");
        }

        BookingTrackingPointEntity point = new BookingTrackingPointEntity();
        point.setBooking(booking);
        point.setProvider(provider);
        point.setLatitude(request.getLatitude());
        point.setLongitude(request.getLongitude());
        point.setHeading(request.getHeading());
        point.setSpeed(request.getSpeed());
        point.setRecordedAt(request.getRecordedAt());
        point.setReceivedAt(now);
        point = trackingPointRepository.save(point);
        lastAcceptedUpdate.put(updateKey, now);

        TrackingLocationResponseModel response = toResponse(point, now);
        messagingTemplate.convertAndSend("/topic/bookings/" + bookingUniqueId + "/tracking", response);
        return response;
    }

    @Transactional
    public TrackingLocationResponseModel getLatestLocation(
            String bookingUniqueId,
            Authentication authentication) {
        ServiceBookingEntity booking = getBooking(bookingUniqueId);
        requireBookingAccess(booking, authentication);
        BookingTrackingPointEntity point = trackingPointRepository
                .findFirstByBooking_UniqueIdOrderByRecordedAtDesc(bookingUniqueId)
                .orElse(null);
        return point == null ? null : toResponse(point, LocalDateTime.now());
    }

    private ProviderEntity requireAssignedProvider(
            ServiceBookingEntity booking,
            Authentication authentication) {
        if (booking.getProvider() == null) {
            throw new IllegalStateException("Booking has no assigned provider");
        }
        String email = requireEmail(authentication);
        ProviderEntity provider = providerAuthRepository.findByEmail(email)
                .map(auth -> auth.getProvider())
                .orElseThrow(() -> new SecurityException("Authenticated user is not a provider"));
        if (!provider.getId().equals(booking.getProvider().getId())) {
            throw new SecurityException("Provider is not assigned to this booking");
        }
        return provider;
    }

    private void requireBookingAccess(ServiceBookingEntity booking, Authentication authentication) {
        if (hasRole(authentication, "ROLE_ADMIN")) {
            return;
        }
        String email = requireEmail(authentication);
        boolean customerAccess = customerAuthRepository.findByEmail(email)
                .map(auth -> sameId(auth.getCustomer(), booking.getCustomer()))
                .orElse(false);
        boolean providerAccess = providerAuthRepository.findByEmail(email)
                .map(auth -> booking.getProvider() != null
                        && sameId(auth.getProvider(), booking.getProvider()))
                .orElse(false);
        if (!customerAccess && !providerAccess) {
            throw new SecurityException("User is not authorized for this booking");
        }
    }

    private void validateTravelState(ServiceBookingEntity booking) {
        if (booking.getStatus() != BookingStatus.PROVIDER_EN_ROUTE) {
            throw new IllegalStateException("Location updates require an en-route booking");
        }
    }

    private ServiceBookingEntity getBooking(String bookingUniqueId) {
        return bookingRepository.findByUniqueId(bookingUniqueId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }

    private TrackingLocationResponseModel toResponse(
            BookingTrackingPointEntity point,
            LocalDateTime now) {
        boolean stale = Duration.between(point.getReceivedAt(), now).compareTo(STALE_AFTER) > 0;
        return TrackingLocationResponseModel.builder()
                .bookingUniqueId(point.getBooking().getUniqueId())
                .providerUniqueId(point.getProvider().getUniqueId())
                .latitude(point.getLatitude())
                .longitude(point.getLongitude())
                .heading(point.getHeading())
                .speed(point.getSpeed())
                .recordedAt(point.getRecordedAt())
                .receivedAt(point.getReceivedAt())
                .stale(stale)
                .build();
    }

    private String requireEmail(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new SecurityException("Authentication is required");
        }
        return authentication.getName();
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> role.equals(authority.getAuthority()));
    }

    private boolean sameId(Object first, Object second) {
        if (first instanceof CustomerEntity firstCustomer && second instanceof CustomerEntity secondCustomer) {
            return firstCustomer.getId().equals(secondCustomer.getId());
        }
        if (first instanceof ProviderEntity firstProvider && second instanceof ProviderEntity secondProvider) {
            return firstProvider.getId().equals(secondProvider.getId());
        }
        return false;
    }
}
