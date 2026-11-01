package com.example.Quick_fix.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.ReviewEntity;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByProvider_UniqueId(String providerUniqueId);
    List<ReviewEntity> findByCustomer_UniqueId(String customerUniqueId);
    Optional<ReviewEntity> findByBooking_UniqueId(String bookingUniqueId);
    boolean existsByBooking_UniqueId(String bookingUniqueId);
}
