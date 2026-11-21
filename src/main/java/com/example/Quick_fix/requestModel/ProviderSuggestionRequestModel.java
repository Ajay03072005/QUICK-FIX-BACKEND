package com.example.Quick_fix.requestModel;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProviderSuggestionRequestModel {

    @NotBlank
    private String providerUniqueId;
}