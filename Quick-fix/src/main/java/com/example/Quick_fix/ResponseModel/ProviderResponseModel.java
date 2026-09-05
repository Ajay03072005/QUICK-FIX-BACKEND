package com.example.Quick_fix.ResponseModel;

import java.util.List;

import com.example.Quick_fix.requestModel.ProviderAddressRequestModel;
import com.example.Quick_fix.requestModel.ProviderContactRequestModel;
import com.example.Quick_fix.requestModel.ProviderDocumentRequestModel;
import com.example.Quick_fix.requestModel.ProviderServiceHistoryRequestModel;
import com.example.Quick_fix.requestModel.ProviderServiceHistoryResponseModel;

import lombok.Data;

@Data
public class ProviderResponseModel {
	private String name;

    private String phoneNumber;

    private String email;

    private List<ProviderServiceHistoryReponseModel> providerServices;

    private List<ProviderAddressReponseModel> addresses;

    private List<ProviderDocumentReponseModel> documents;

    
    private List<ProviderContactResponseModel> contacts;
}
