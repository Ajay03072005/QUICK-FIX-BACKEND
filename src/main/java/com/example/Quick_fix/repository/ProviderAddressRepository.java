package com.example.Quick_fix.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.ProviderAddressEntity;
import com.example.Quick_fix.Entity.ServiceEntity;

@Repository
public interface ProviderAddressRepository extends JpaRepository<ProviderAddressEntity, Long> {

	Optional<ProviderAddressEntity> findByProvider_UniqueIdAndPrimary(String uniqueId, boolean b);

	@Query("select address from ProviderAddressEntity address join fetch address.provider provider where provider.uniqueId in :providerIds and address.primary = true")
	List<ProviderAddressEntity> findPrimaryAddressesByProviderIds(@Param("providerIds") List<String> providerIds);

}
