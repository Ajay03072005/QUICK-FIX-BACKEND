package com.example.Quick_fix.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Quick_fix.Entity.AdminAuditLogEntity;
import com.example.Quick_fix.repository.AdminAuditLogRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminAuditLogRepository auditLogRepository;

    @Transactional
    public AdminAuditLogEntity logAction(String actor, String action, String targetType, String targetId, String reason) {
        AdminAuditLogEntity log = new AdminAuditLogEntity();
        log.setActor(actor != null ? actor : "ADMIN");
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setReason(reason);
        return auditLogRepository.save(log);
    }

    public List<AdminAuditLogEntity> getAuditLogs() {
        return auditLogRepository.findAllByOrderByIdDesc();
    }
}
