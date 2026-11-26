package com.example.Quick_fix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.ProviderServiceHistoryEntity;

@Repository
public interface ProviderServiceHistoryRepository extends JpaRepository<ProviderServiceHistoryEntity, Integer> {

	@EntityGraph(attributePaths = "provider")
	List<ProviderServiceHistoryEntity> findByServiceAndStatus(String service, String status);

	List<ProviderServiceHistoryEntity> findByProvider_UniqueIdAndService(String providerUniqueId, String service);

	List<ProviderServiceHistoryEntity> findByProvider_UniqueId(String providerUniqueId);
}
