package com.example.Quick_fix.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Quick_fix.Entity.SupportTicketEntity;
import com.example.Quick_fix.Entity.SupportTicketMessageEntity;
import com.example.Quick_fix.service.SupportService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;

    @PostMapping("/tickets")
    public ResponseEntity<SupportTicketEntity> createTicket(@RequestBody CreateTicketRequest request) {
        SupportTicketEntity ticket = supportService.createTicket(
                request.getSubject(),
                request.getCategory(),
                request.getPriority(),
                request.getCustomerUniqueId(),
                request.getProviderUniqueId(),
                request.getBookingUniqueId(),
                request.getInitialMessage()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<SupportTicketEntity>> getAllTickets() {
        return ResponseEntity.ok(supportService.getAllTickets());
    }

    @GetMapping("/tickets/{uniqueId}")
    public ResponseEntity<SupportTicketEntity> getTicket(@PathVariable String uniqueId) {
        return ResponseEntity.ok(supportService.getTicketByUniqueId(uniqueId));
    }

    @GetMapping("/tickets/{uniqueId}/messages")
    public ResponseEntity<List<SupportTicketMessageEntity>> getMessages(@PathVariable String uniqueId) {
        return ResponseEntity.ok(supportService.getTicketMessages(uniqueId));
    }

    @PostMapping("/tickets/{uniqueId}/messages")
    public ResponseEntity<SupportTicketMessageEntity> addMessage(
            @PathVariable String uniqueId,
            @RequestBody AddMessageRequest request) {
        return ResponseEntity.ok(supportService.addMessage(
                uniqueId,
                request.getSenderName(),
                request.getSenderRole(),
                request.getVisibility(),
                request.getMessageBody()
        ));
    }

    @PutMapping("/tickets/{uniqueId}/status")
    public ResponseEntity<SupportTicketEntity> updateStatus(
            @PathVariable String uniqueId,
            @RequestParam String status,
            @RequestParam(required = false) String resolutionNotes) {
        return ResponseEntity.ok(supportService.updateTicketStatus(uniqueId, status, resolutionNotes));
    }

    @Data
    public static class CreateTicketRequest {
        private String subject;
        private String category;
        private String priority;
        private String customerUniqueId;
        private String providerUniqueId;
        private String bookingUniqueId;
        private String initialMessage;
    }

    @Data
    public static class AddMessageRequest {
        private String senderName;
        private String senderRole;
        private String visibility;
        private String messageBody;
    }
}
