package com.example.Quick_fix.Entity;

import java.time.LocalDate;

import com.example.Quick_fix.Enums.DocumentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "PROVIDER_DOCUMENT")
public class ProviderDocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROVIDER_ID", nullable = false)
    private ProviderEntity provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "DOCUMENT_TYPE", nullable = false)
    private DocumentType documentType; 

    @Column(name = "DOCUMENT_NUMBER")
    private String documentNumber;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "FILE_PATH", columnDefinition = "TEXT")
    private String filePath;

    @Column(name = "FILE_URL", columnDefinition = "TEXT")
    private String fileUrl;

    @Column(name = "ISSUE_DATE")
    private LocalDate issueDate;

    @Column(name = "EXPIRY_DATE")
    private LocalDate expiryDate;

    @Column(name = "STATUS")
    private String status;


}