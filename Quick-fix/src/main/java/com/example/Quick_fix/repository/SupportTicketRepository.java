package com.example.Quick_fix.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.SupportTicketEntity;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicketEntity, Long> {
    Optional<SupportTicketEntity> findByUniqueId(String uniqueId);
    List<SupportTicketEntity> findByCustomer_UniqueId(String customerUniqueId);
    List<SupportTicketEntity> findByProvider_UniqueId(String providerUniqueId);
}
