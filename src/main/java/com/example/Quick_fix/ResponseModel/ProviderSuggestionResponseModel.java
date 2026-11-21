package com.example.Quick_fix.ResponseModel;

import lombok.Data;

@Data
public class ProviderSuggestionResponseModel {

    private String providerUniqueId;

    private String providerName;

    private String profileImage;

    private Double rating;

    private Double latitude;

    private Double longitude;

    private String availabilityStatus;

    private Double distance;

    private Double servicePrice;

    private Double distanceCharge;

    private Double totalAmount;

    private boolean available;
    
    private String uniqueId;
}
