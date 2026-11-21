package com.example.Quick_fix.ResponseModel;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TrackingLocationResponseModel {
    String bookingUniqueId;
    String providerUniqueId;
    Double latitude;
    Double longitude;
    Double heading;
    Double speed;
    LocalDateTime recordedAt;
    LocalDateTime receivedAt;
    boolean stale;
}
