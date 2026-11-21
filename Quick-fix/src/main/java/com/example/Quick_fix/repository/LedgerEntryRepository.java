package com.example.Quick_fix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.LedgerEntryEntity;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntryEntity, Long> {
    List<LedgerEntryEntity> findByProvider_UniqueId(String providerUniqueId);
    List<LedgerEntryEntity> findByBooking_UniqueId(String bookingUniqueId);
}
