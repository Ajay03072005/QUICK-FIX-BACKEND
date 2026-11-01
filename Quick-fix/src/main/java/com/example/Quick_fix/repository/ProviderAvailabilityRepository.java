package com.example.Quick_fix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Quick_fix.Entity.ProviderAvailabilityEntity;

public interface ProviderAvailabilityRepository extends JpaRepository<ProviderAvailabilityEntity, Long> {
    List<ProviderAvailabilityEntity> findByProvider_UniqueId(String providerUniqueId);
    List<ProviderAvailabilityEntity> findByProviderId(Integer providerId);
    void deleteByProviderId(Integer providerId);
}
