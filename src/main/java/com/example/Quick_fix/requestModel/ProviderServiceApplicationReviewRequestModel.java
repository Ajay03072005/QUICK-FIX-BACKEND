package com.example.Quick_fix.requestModel;

import lombok.Data;

@Data
public class ProviderServiceApplicationReviewRequestModel {
    private String status; // APPROVED, REJECTED, MORE_INFO_REQUESTED
    private String rejectionReason;
    private String adminNotes;
}
