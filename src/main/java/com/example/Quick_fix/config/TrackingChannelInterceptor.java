package com.example.Quick_fix.config;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.Quick_fix.Entity.ServiceBookingEntity;
import com.example.Quick_fix.repository.BookingRepository;
import com.example.Quick_fix.repository.CustomerAuthRepository;
import com.example.Quick_fix.repository.ProviderAuthRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TrackingChannelInterceptor implements ChannelInterceptor {

    private static final Pattern TRACKING_TOPIC =
            Pattern.compile("^/topic/bookings/([^/]+)/tracking$");

    private final JwtTokenProvider tokenProvider;
    private final BookingRepository bookingRepository;
    private final CustomerAuthRepository customerAuthRepository;
    private final ProviderAuthRepository providerAuthRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            authenticate(accessor);
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            authorizeSubscription(accessor);
        } else if (StompCommand.SEND.equals(accessor.getCommand())
                && "/app/tracking/location".equals(accessor.getDestination())) {
            if (accessor.getUser() == null) {
                throw new SecurityException("Authentication is required");
            }
        }
        return message;
    }

    private void authenticate(StompHeaderAccessor accessor) {
        String header = accessor.getFirstNativeHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            throw new SecurityException("Bearer token is required");
        }
        String token = header.substring(7);
        if (!tokenProvider.validateToken(token)) {
            throw new SecurityException("Invalid bearer token");
        }
        String email = tokenProvider.getEmailFromToken(token);
        String role = tokenProvider.getRoleFromToken(token);
        if (StringUtils.hasText(role) && !role.startsWith("ROLE_")) {
            role = "ROLE_" + role.toUpperCase();
        }
        accessor.setUser(new UsernamePasswordAuthenticationToken(
                email,
                null,
                List.of(new SimpleGrantedAuthority(StringUtils.hasText(role) ? role : "ROLE_USER"))));
    }

    private void authorizeSubscription(StompHeaderAccessor accessor) {
        Matcher matcher = TRACKING_TOPIC.matcher(accessor.getDestination() == null ? "" : accessor.getDestination());
        if (!matcher.matches() || accessor.getUser() == null) {
            throw new SecurityException("Only authorized booking tracking subscriptions are allowed");
        }

        ServiceBookingEntity booking = bookingRepository.findByUniqueId(matcher.group(1))
                .orElseThrow(() -> new SecurityException("Booking not found"));
        String email = accessor.getUser().getName();
        boolean customerAccess = customerAuthRepository.findByEmail(email)
                .map(auth -> auth.getCustomer().getId().equals(booking.getCustomer().getId()))
                .orElse(false);
        boolean providerAccess = providerAuthRepository.findByEmail(email)
                .map(auth -> booking.getProvider() != null
                        && auth.getProvider().getId().equals(booking.getProvider().getId()))
                .orElse(false);
        boolean adminAccess = accessor.getUser() instanceof Authentication authentication
                && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        if (!customerAccess && !providerAccess && !adminAccess) {
            throw new SecurityException("User is not authorized for this booking");
        }
    }
}
