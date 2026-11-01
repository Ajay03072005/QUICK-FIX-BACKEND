package com.example.Quick_fix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.RefundEntity;

@Repository
public interface RefundRepository extends JpaRepository<RefundEntity, Long> {
    List<RefundEntity> findByBooking_UniqueId(String bookingUniqueId);
    List<RefundEntity> findByPayment_UniqueId(String paymentUniqueId);
}
