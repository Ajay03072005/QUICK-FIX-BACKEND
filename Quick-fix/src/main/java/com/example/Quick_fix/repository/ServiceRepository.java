package com.example.Quick_fix.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.ServiceEntity;
import com.example.Quick_fix.Enums.ServiceType;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Integer> {

    Optional<ServiceEntity> findByUniqueId(String uniqueId);

    boolean existsByUniqueId(String uniqueId);

    Optional<ServiceEntity> findByServiceType(ServiceType serviceType);
}