package com.example.Quick_fix.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.ServiceBookingEntity;

@Repository
public interface BookingRepository extends JpaRepository<ServiceBookingEntity, Long> {

    Optional<ServiceBookingEntity> findByUniqueId(String uniqueId);

    List<ServiceBookingEntity> findByCustomer_UniqueId(String customerUniqueId);

    List<ServiceBookingEntity> findByProvider_UniqueId(String providerUniqueId);

    boolean existsByUniqueId(String uniqueId);

    void deleteByUniqueId(String uniqueId);
}