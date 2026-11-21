package com.example.Quick_fix.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Quick_fix.Entity.LedgerEntryEntity;
import com.example.Quick_fix.Entity.PayoutEntity;
import com.example.Quick_fix.Entity.ProviderEarningEntity;
import com.example.Quick_fix.service.FinanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    @GetMapping("/provider/{providerUniqueId}/earnings")
    public ResponseEntity<List<ProviderEarningEntity>> getProviderEarnings(@PathVariable String providerUniqueId) {
        return ResponseEntity.ok(financeService.getProviderEarnings(providerUniqueId));
    }

    @GetMapping("/provider/{providerUniqueId}/ledger")
    public ResponseEntity<List<LedgerEntryEntity>> getProviderLedger(@PathVariable String providerUniqueId) {
        return ResponseEntity.ok(financeService.getProviderLedger(providerUniqueId));
    }

    @GetMapping("/ledger")
    public ResponseEntity<List<LedgerEntryEntity>> getAllLedgerEntries() {
        return ResponseEntity.ok(financeService.getAllLedgerEntries());
    }

    @PostMapping("/provider/{providerUniqueId}/payouts")
    public ResponseEntity<PayoutEntity> createPayout(@PathVariable String providerUniqueId, @RequestParam Double amount) {
        return ResponseEntity.ok(financeService.createPayout(providerUniqueId, amount));
    }

    @GetMapping("/provider/{providerUniqueId}/payouts")
    public ResponseEntity<List<PayoutEntity>> getProviderPayouts(@PathVariable String providerUniqueId) {
        return ResponseEntity.ok(financeService.getProviderPayouts(providerUniqueId));
    }
}
