package com.example.Quick_fix.ResponseModel;

import lombok.Data;

@Data
public class ProviderSuggestionResponseModel {

    private String providerUniqueId;

    private String providerName;

    private String profileImage;

    private Double rating;

    private Double distance;

    private Double servicePrice;

    private Double distanceCharge;

    private Double totalAmount;

    private boolean available;
}