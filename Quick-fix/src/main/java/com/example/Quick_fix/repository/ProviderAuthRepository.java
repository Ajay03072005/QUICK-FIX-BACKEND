package com.example.Quick_fix.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.ProviderAuthEntity;

@Repository
public interface ProviderAuthRepository extends JpaRepository<ProviderAuthEntity, Long> {
    Optional<ProviderAuthEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
