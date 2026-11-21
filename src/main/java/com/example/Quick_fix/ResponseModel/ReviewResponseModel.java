package com.example.Quick_fix.ResponseModel;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseModel {
    private String uniqueId;
    private String bookingUniqueId;
    private String customerUniqueId;
    private String customerName;
    private String providerUniqueId;
    private String providerName;
    private Integer rating;
    private String comment;
    private String status;
    private LocalDateTime createdAt;
}
