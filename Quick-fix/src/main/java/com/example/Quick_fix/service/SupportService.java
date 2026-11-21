package com.example.Quick_fix.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Quick_fix.Entity.CustomerEntity;
import com.example.Quick_fix.Entity.ProviderEntity;
import com.example.Quick_fix.Entity.ServiceBookingEntity;
import com.example.Quick_fix.Entity.SupportTicketEntity;
import com.example.Quick_fix.Entity.SupportTicketMessageEntity;
import com.example.Quick_fix.commons.Common;
import com.example.Quick_fix.repository.BookingRepository;
import com.example.Quick_fix.repository.CustomerRepository;
import com.example.Quick_fix.repository.ProviderRepository;
import com.example.Quick_fix.repository.SupportTicketMessageRepository;
import com.example.Quick_fix.repository.SupportTicketRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SupportTicketRepository supportTicketRepository;
    private final SupportTicketMessageRepository messageRepository;
    private final CustomerRepository customerRepository;
    private final ProviderRepository providerRepository;
    private final BookingRepository bookingRepository;
    private final Common commons;

    @Transactional
    public SupportTicketEntity createTicket(String subject, String category, String priority, String customerUniqueId, String providerUniqueId, String bookingUniqueId, String initialMessage) {
        SupportTicketEntity ticket = new SupportTicketEntity();
        ticket.setUniqueId(commons.generateUniqueId());
        ticket.setSubject(subject);
        ticket.setCategory(category != null ? category : "GENERAL");
        ticket.setPriority(priority != null ? priority : "NORMAL");
        ticket.setStatus("OPEN");

        if (customerUniqueId != null && !customerUniqueId.isBlank()) {
            customerRepository.findByUniqueId(customerUniqueId).ifPresent(ticket::setCustomer);
        }
        if (providerUniqueId != null && !providerUniqueId.isBlank()) {
            providerRepository.findByUniqueId(providerUniqueId).ifPresent(ticket::setProvider);
        }
        if (bookingUniqueId != null && !bookingUniqueId.isBlank()) {
            bookingRepository.findByUniqueId(bookingUniqueId).ifPresent(ticket::setBooking);
        }

        ticket = supportTicketRepository.save(ticket);

        if (initialMessage != null && !initialMessage.isBlank()) {
            addMessage(ticket.getUniqueId(), "Requester", "CUSTOMER", "PUBLIC", initialMessage);
        }

        return ticket;
    }

    public List<SupportTicketEntity> getAllTickets() {
        return supportTicketRepository.findAll();
    }

    public SupportTicketEntity getTicketByUniqueId(String uniqueId) {
        return supportTicketRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    public List<SupportTicketMessageEntity> getTicketMessages(String ticketUniqueId) {
        return messageRepository.findByTicket_UniqueIdOrderByIdAsc(ticketUniqueId);
    }

    @Transactional
    public SupportTicketMessageEntity addMessage(String ticketUniqueId, String senderName, String senderRole, String visibility, String messageBody) {
        SupportTicketEntity ticket = getTicketByUniqueId(ticketUniqueId);

        SupportTicketMessageEntity msg = new SupportTicketMessageEntity();
        msg.setTicket(ticket);
        msg.setSenderName(senderName);
        msg.setSenderRole(senderRole);
        msg.setVisibility(visibility != null ? visibility : "PUBLIC");
        msg.setMessageBody(messageBody);

        return messageRepository.save(msg);
    }

    @Transactional
    public SupportTicketEntity updateTicketStatus(String ticketUniqueId, String status, String resolutionNotes) {
        SupportTicketEntity ticket = getTicketByUniqueId(ticketUniqueId);
        ticket.setStatus(status);
        if (resolutionNotes != null) {
            ticket.setResolutionNotes(resolutionNotes);
        }
        if ("RESOLVED".equalsIgnoreCase(status) || "CLOSED".equalsIgnoreCase(status)) {
            ticket.setResolvedAt(LocalDateTime.now());
        }
        return supportTicketRepository.save(ticket);
    }
}
