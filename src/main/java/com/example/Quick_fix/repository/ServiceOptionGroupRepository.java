package com.example.Quick_fix.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.ServiceOptionGroupEntity;

@Repository
public interface ServiceOptionGroupRepository extends JpaRepository<ServiceOptionGroupEntity, Long> {
    List<ServiceOptionGroupEntity> findByService_IdAndActiveTrue(Integer serviceId);
    List<ServiceOptionGroupEntity> findByService_UniqueIdAndActiveTrue(String serviceUniqueId);
    List<ServiceOptionGroupEntity> findByService_UniqueId(String serviceUniqueId);
}
