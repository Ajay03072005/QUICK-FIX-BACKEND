package com.example.Quick_fix.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.ProviderAddressEntity;
import com.example.Quick_fix.Entity.ServiceEntity;

@Repository
public interface ProviderAddressRepository extends JpaRepository<ProviderAddressEntity,Integer > {

	Optional<ProviderAddressEntity> findByProvider_UniqueIdAndPrimary(String uniqueId, boolean b);

}
