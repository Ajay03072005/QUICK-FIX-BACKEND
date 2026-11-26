package com.example.Quick_fix.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.Quick_fix.Entity.ProviderServiceApplicationEntity;

@Repository
public interface ProviderServiceApplicationRepository extends JpaRepository<ProviderServiceApplicationEntity, Long> {
    Optional<ProviderServiceApplicationEntity> findByUniqueId(String uniqueId);
    List<ProviderServiceApplicationEntity> findByProvider_Id(Integer providerId);
    List<ProviderServiceApplicationEntity> findByProvider_UniqueId(String providerUniqueId);
    List<ProviderServiceApplicationEntity> findByProvider_UniqueIdAndStatus(String providerUniqueId, String status);
    List<ProviderServiceApplicationEntity> findByStatus(String status);
    Optional<ProviderServiceApplicationEntity> findByProvider_IdAndService_Id(Integer providerId, Integer serviceId);
    boolean existsByProvider_IdAndService_IdAndStatusIn(Integer providerId, Integer serviceId, List<String> statuses);
    Optional<ProviderServiceApplicationEntity> findByProvider_UniqueIdAndService_UniqueId(String providerUniqueId, String serviceUniqueId);
}
