package com.example.Quick_fix.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.BookingTrackingPointEntity;

@Repository
public interface BookingTrackingPointRepository extends JpaRepository<BookingTrackingPointEntity, Long> {
    List<BookingTrackingPointEntity> findByBooking_UniqueIdOrderByRecordedAtDesc(String bookingUniqueId);
    Optional<BookingTrackingPointEntity> findFirstByBooking_UniqueIdOrderByRecordedAtDesc(String bookingUniqueId);
}
