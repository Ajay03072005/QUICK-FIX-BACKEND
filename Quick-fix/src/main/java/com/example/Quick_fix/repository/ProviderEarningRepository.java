package com.example.Quick_fix.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.ProviderEarningEntity;

@Repository
public interface ProviderEarningRepository extends JpaRepository<ProviderEarningEntity, Long> {
    List<ProviderEarningEntity> findByProvider_UniqueId(String providerUniqueId);
    Optional<ProviderEarningEntity> findByBooking_UniqueId(String bookingUniqueId);
}
