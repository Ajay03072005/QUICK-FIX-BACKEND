package com.example.Quick_fix.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.PaymentEntity;
import com.example.Quick_fix.Enums.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

	Optional<PaymentEntity> findByUniqueId(String uniqueId);

	Optional<PaymentEntity> findByBooking_UniqueId(String bookingUniqueId);

	List<PaymentEntity> findAllByBooking_UniqueId(String bookingUniqueId);

	Optional<PaymentEntity> findByRazorpayOrderId(String razorpayOrderId);

	boolean existsByUniqueId(String uniqueId);

	Optional<PaymentEntity> findFirstByBooking_UniqueIdAndStatusInOrderByCreatedAtDesc(
			String bookingUniqueId, List<PaymentStatus> statuses);
}
