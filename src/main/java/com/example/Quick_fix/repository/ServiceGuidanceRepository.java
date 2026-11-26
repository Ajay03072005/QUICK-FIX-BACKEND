package com.example.Quick_fix.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.Quick_fix.Entity.ServiceGuidanceEntity;

@Repository
public interface ServiceGuidanceRepository extends JpaRepository<ServiceGuidanceEntity, Long> {
    Optional<ServiceGuidanceEntity> findByService_Id(Integer serviceId);
    Optional<ServiceGuidanceEntity> findByService_UniqueId(String uniqueId);
    Optional<ServiceGuidanceEntity> findByService_UniqueIdAndActiveTrue(String uniqueId);
    boolean existsByService_Id(Integer serviceId);
}
