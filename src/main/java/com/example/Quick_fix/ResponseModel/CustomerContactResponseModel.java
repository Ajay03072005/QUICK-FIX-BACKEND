package com.example.Quick_fix.ResponseModel;

import java.time.LocalDateTime;

import com.example.Quick_fix.Enums.ContactType;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CustomerContactResponseModel {

    private Long id;

    private Long customerId;

    private ContactType contactType;

    private String contactValue;

    private boolean primaryContact;

    private boolean verified;
    
    private String uniqueId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}