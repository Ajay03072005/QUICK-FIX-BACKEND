package com.example.Quick_fix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Quick_fix.Entity.SupportTicketMessageEntity;

@Repository
public interface SupportTicketMessageRepository extends JpaRepository<SupportTicketMessageEntity, Long> {
    List<SupportTicketMessageEntity> findByTicket_UniqueIdOrderByIdAsc(String ticketUniqueId);
}
