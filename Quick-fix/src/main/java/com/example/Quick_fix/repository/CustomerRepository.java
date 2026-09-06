package com.example.Quick_fix.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.CustomerEntity;
import com.example.Quick_fix.Entity.ServiceEntity;

@Repository
public interface CustomerRepository
        extends JpaRepository<CustomerEntity, Long> {

	Optional<CustomerEntity> findByUniqueId(String customerUniqueId);

	boolean existsByuniqueId(String customerUniqueId);

}