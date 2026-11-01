package com.example.Quick_fix.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "SUPPORT_TICKET_MESSAGES")
public class SupportTicketMessageEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private SupportTicketEntity ticket;

    @Column(nullable = false)
    private String senderName;

    @Column(nullable = false)
    private String senderRole; // CUSTOMER, PROVIDER, SUPPORT, ADMIN

    @Column(nullable = false)
    private String visibility = "PUBLIC"; // PUBLIC or INTERNAL_NOTE

    @Column(columnDefinition = "TEXT", nullable = false)
    private String messageBody;
}
