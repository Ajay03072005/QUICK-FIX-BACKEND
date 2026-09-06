package com.example.Quick_fix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.ProviderServiceHistoryEntity;
import com.example.Quick_fix.Enums.ServiceType;

@Repository
public interface ProviderServiceHistoryRepository extends JpaRepository<ProviderServiceHistoryEntity, Integer> {

	List<ProviderServiceHistoryEntity> findByServiceAndStatus(ServiceType service, String status);
}
