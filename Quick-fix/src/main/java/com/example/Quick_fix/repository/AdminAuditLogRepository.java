package com.example.Quick_fix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.AdminAuditLogEntity;

@Repository
public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLogEntity, Long> {
    List<AdminAuditLogEntity> findAllByOrderByIdDesc();
}
