package com.example.Quick_fix.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.CustomerAuthEntity;

@Repository
public interface CustomerAuthRepository extends JpaRepository<CustomerAuthEntity, Long> {

	Optional<CustomerAuthEntity> findByEmail(String email);

	boolean existsByEmail(String email);
}