package com.example.Quick_fix.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.BookingPricingDetailEntity;

@Repository
public interface BookingPricingDetailRepository extends JpaRepository<BookingPricingDetailEntity, Long> {
    Optional<BookingPricingDetailEntity> findByBooking_Id(Long bookingId);
    Optional<BookingPricingDetailEntity> findByBooking_UniqueId(String bookingUniqueId);
}
