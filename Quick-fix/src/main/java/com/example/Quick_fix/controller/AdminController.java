package com.example.Quick_fix.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Quick_fix.Entity.AdminAuditLogEntity;
import com.example.Quick_fix.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AdminAuditLogEntity>> getAuditLogs() {
        return ResponseEntity.ok(adminService.getAuditLogs());
    }

    @PostMapping("/audit-logs")
    public ResponseEntity<AdminAuditLogEntity> logAction(
            @RequestParam String actor,
            @RequestParam String action,
            @RequestParam String targetType,
            @RequestParam String targetId,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(adminService.logAction(actor, action, targetType, targetId, reason));
    }
}
