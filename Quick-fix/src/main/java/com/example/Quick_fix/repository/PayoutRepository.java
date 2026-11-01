package com.example.Quick_fix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.PayoutEntity;

@Repository
public interface PayoutRepository extends JpaRepository<PayoutEntity, Long> {
    List<PayoutEntity> findByProvider_UniqueId(String providerUniqueId);
}
