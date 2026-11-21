package com.example.Quick_fix.requestModel;

import java.util.List;

import lombok.Data;

@Data
public class ProviderRequestModel {
	private String name;

    private String phoneNumber;

    private String email;

    private List<ProviderServiceHistoryRequestModel> providerServices;

    private List<ProviderAddressRequestModel> addresses;

    private List<ProviderDocumentRequestModel> documents;

    
    private List<ProviderContactRequestModel> contacts;
}
