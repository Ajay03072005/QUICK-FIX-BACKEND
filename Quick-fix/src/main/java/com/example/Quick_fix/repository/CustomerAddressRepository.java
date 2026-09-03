package com.example.Quick_fix.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.CustomerAddressEntity;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddressEntity, Long> {

	List<CustomerAddressEntity> findByCustomerId(Long customerId);

	Optional<CustomerAddressEntity> findByIdAndCustomerId(Long id, Long customerId);
}
