package com.example.Quick_fix.ResponseModel;

import java.time.LocalDate;

import com.example.Quick_fix.Enums.DocumentType;

import lombok.Data;

@Data
public class ProviderDocumentReponseModel {

	 private DocumentType documentType; 

	    private String documentNumber;

	    private String fileName;

	    private String filePath;

	    private String fileUrl;
	    
	    private LocalDate issueDate;

	    private LocalDate expiryDate;

	    private String status;
}
