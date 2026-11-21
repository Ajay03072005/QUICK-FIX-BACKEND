package com.example.Quick_fix.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.CommissionRuleEntity;

@Repository
public interface CommissionRuleRepository extends JpaRepository<CommissionRuleEntity, Long> {
    Optional<CommissionRuleEntity> findFirstByActiveTrueOrderByIdDesc();
}
