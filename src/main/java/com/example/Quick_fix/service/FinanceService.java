package com.example.Quick_fix.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Quick_fix.Entity.CommissionRuleEntity;
import com.example.Quick_fix.Entity.LedgerEntryEntity;
import com.example.Quick_fix.Entity.PayoutEntity;
import com.example.Quick_fix.Entity.ProviderEarningEntity;
import com.example.Quick_fix.Entity.ProviderEntity;
import com.example.Quick_fix.Entity.ServiceBookingEntity;
import com.example.Quick_fix.commons.Common;
import com.example.Quick_fix.repository.CommissionRuleRepository;
import com.example.Quick_fix.repository.LedgerEntryRepository;
import com.example.Quick_fix.repository.PayoutRepository;
import com.example.Quick_fix.repository.ProviderEarningRepository;
import com.example.Quick_fix.repository.ProviderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final ProviderEarningRepository providerEarningRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final PayoutRepository payoutRepository;
    private final CommissionRuleRepository commissionRuleRepository;
    private final ProviderRepository providerRepository;
    private final Common commons;

    @Transactional
    public ProviderEarningEntity calculateAndRecordEarning(ServiceBookingEntity booking) {
        if (booking == null || booking.getProvider() == null) {
            return null;
        }

        // Check if already calculated
        if (providerEarningRepository.findByBooking_UniqueId(booking.getUniqueId()).isPresent()) {
            return providerEarningRepository.findByBooking_UniqueId(booking.getUniqueId()).get();
        }

        Double grossAmount = booking.getTotalAmount() != null ? booking.getTotalAmount() : 0.0;
        Double commissionRate = 15.0;

        CommissionRuleEntity rule = commissionRuleRepository.findFirstByActiveTrueOrderByIdDesc().orElse(null);
        if (rule != null && rule.getValue() != null) {
            commissionRate = rule.getValue();
        }

        Double commissionAmount = Math.round((grossAmount * (commissionRate / 100.0)) * 100.0) / 100.0;
        Double netAmount = Math.round((grossAmount - commissionAmount) * 100.0) / 100.0;

        ProviderEarningEntity earning = new ProviderEarningEntity();
        earning.setUniqueId(commons.generateUniqueId());
        earning.setBooking(booking);
        earning.setProvider(booking.getProvider());
        earning.setGrossAmount(grossAmount);
        earning.setCommissionAmount(commissionAmount);
        earning.setNetAmount(netAmount);
        earning.setStatus("AVAILABLE");

        earning = providerEarningRepository.save(earning);

        // Record Ledger Entries
        LedgerEntryEntity captureEntry = new LedgerEntryEntity();
        captureEntry.setUniqueId(commons.generateUniqueId());
        captureEntry.setProvider(booking.getProvider());
        captureEntry.setBooking(booking);
        captureEntry.setEntryType("CUSTOMER_CAPTURE");
        captureEntry.setDirection("CREDIT");
        captureEntry.setAmount(grossAmount);
        captureEntry.setDescription("Customer booking capture for " + booking.getUniqueId());
        ledgerEntryRepository.save(captureEntry);

        LedgerEntryEntity earningEntry = new LedgerEntryEntity();
        earningEntry.setUniqueId(commons.generateUniqueId());
        earningEntry.setProvider(booking.getProvider());
        earningEntry.setBooking(booking);
        earningEntry.setEntryType("PROVIDER_EARNING");
        earningEntry.setDirection("CREDIT");
        earningEntry.setAmount(netAmount);
        earningEntry.setDescription("Provider net payable for booking " + booking.getUniqueId());
        ledgerEntryRepository.save(earningEntry);

        LedgerEntryEntity commissionEntry = new LedgerEntryEntity();
        commissionEntry.setUniqueId(commons.generateUniqueId());
        commissionEntry.setProvider(booking.getProvider());
        commissionEntry.setBooking(booking);
        commissionEntry.setEntryType("COMMISSION");
        commissionEntry.setDirection("CREDIT");
        commissionEntry.setAmount(commissionAmount);
        commissionEntry.setDescription("Platform commission fee for booking " + booking.getUniqueId());
        ledgerEntryRepository.save(commissionEntry);

        return earning;
    }

    public List<ProviderEarningEntity> getProviderEarnings(String providerUniqueId) {
        return providerEarningRepository.findByProvider_UniqueId(providerUniqueId);
    }

    public List<LedgerEntryEntity> getProviderLedger(String providerUniqueId) {
        return ledgerEntryRepository.findByProvider_UniqueId(providerUniqueId);
    }

    public List<LedgerEntryEntity> getAllLedgerEntries() {
        return ledgerEntryRepository.findAll();
    }

    @Transactional
    public PayoutEntity createPayout(String providerUniqueId, Double amount) {
        ProviderEntity provider = providerRepository.findByUniqueId(providerUniqueId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        PayoutEntity payout = new PayoutEntity();
        payout.setUniqueId(commons.generateUniqueId());
        payout.setProvider(provider);
        payout.setAmount(amount);
        payout.setStatus("PAID");
        payout.setPaidAt(LocalDateTime.now());
        payout.setProviderPayoutRef("PAYOUT-" + System.currentTimeMillis());

        payout = payoutRepository.save(payout);

        LedgerEntryEntity payoutEntry = new LedgerEntryEntity();
        payoutEntry.setUniqueId(commons.generateUniqueId());
        payoutEntry.setProvider(provider);
        payoutEntry.setEntryType("PAYOUT");
        payoutEntry.setDirection("DEBIT");
        payoutEntry.setAmount(amount);
        payoutEntry.setDescription("Bank payout settlement ref: " + payout.getProviderPayoutRef());
        ledgerEntryRepository.save(payoutEntry);

        return payout;
    }

    public List<PayoutEntity> getProviderPayouts(String providerUniqueId) {
        return payoutRepository.findByProvider_UniqueId(providerUniqueId);
    }
}
