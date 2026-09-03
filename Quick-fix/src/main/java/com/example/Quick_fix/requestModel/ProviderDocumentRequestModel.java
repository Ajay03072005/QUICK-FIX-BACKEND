package com.example.Quick_fix.requestModel;

import java.time.LocalDate;

import com.example.Quick_fix.Enums.DocumentType;

import lombok.Data;

@Data
public class ProviderDocumentRequestModel {
	 
	    private DocumentType documentType; 

	    private String documentNumber;

	    private String fileName;

	    private String filePath;

	    private String fileUrl;
	    
	    private LocalDate issueDate;

	    private LocalDate expiryDate;

	    private String status;
}
