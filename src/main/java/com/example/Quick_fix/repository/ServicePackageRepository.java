package com.example.Quick_fix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.ServicePackageEntity;

@Repository
public interface ServicePackageRepository extends JpaRepository<ServicePackageEntity, Long> {
    List<ServicePackageEntity> findByService_IdAndActiveTrue(Integer serviceId);
    List<ServicePackageEntity> findByService_UniqueIdAndActiveTrue(String serviceUniqueId);
    List<ServicePackageEntity> findByService_UniqueId(String serviceUniqueId);
}
