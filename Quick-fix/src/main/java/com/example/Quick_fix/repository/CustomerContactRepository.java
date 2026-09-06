package com.example.Quick_fix.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.CustomerContactEntity;
import com.example.Quick_fix.Entity.CustomerEntity;

@Repository
public interface CustomerContactRepository extends JpaRepository<CustomerContactEntity, Long> {

	List<CustomerContactEntity> findByCustomerId(Long customerId);

	Optional<CustomerContactEntity> findByIdAndCustomerId(Long id, Long customerId);

	Optional<CustomerContactEntity> findByUniqueId(String contactUniqueId);
}
