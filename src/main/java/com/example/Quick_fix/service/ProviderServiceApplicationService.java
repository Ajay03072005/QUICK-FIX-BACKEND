package com.example.Quick_fix.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Quick_fix.Entity.ProviderEntity;
import com.example.Quick_fix.Entity.ProviderServiceApplicationEntity;
import com.example.Quick_fix.Entity.ProviderServiceHistoryEntity;
import com.example.Quick_fix.Entity.ServiceEntity;
import com.example.Quick_fix.ResponseModel.ProviderServiceApplicationResponseModel;
import com.example.Quick_fix.commons.Common;
import com.example.Quick_fix.repository.ProviderRepository;
import com.example.Quick_fix.repository.ProviderServiceApplicationRepository;
import com.example.Quick_fix.repository.ProviderServiceHistoryRepository;
import com.example.Quick_fix.repository.ServiceRepository;
import com.example.Quick_fix.requestModel.ProviderServiceApplicationRequestModel;
import com.example.Quick_fix.requestModel.ProviderServiceApplicationReviewRequestModel;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProviderServiceApplicationService {

    private final ProviderServiceApplicationRepository applicationRepository;
    private final ProviderRepository providerRepository;
    private final ServiceRepository serviceRepository;
    private final ProviderServiceHistoryRepository historyRepository;
    private final Common common;

    @Transactional
    public ProviderServiceApplicationResponseModel applyForService(String providerUniqueId, ProviderServiceApplicationRequestModel request) {
        ProviderEntity provider = providerRepository.findByUniqueId(providerUniqueId)
                .orElseThrow(() -> new RuntimeException("Provider not found: " + providerUniqueId));

        ServiceEntity service = serviceRepository.findByUniqueId(request.getServiceUniqueId())
                .orElseThrow(() -> new RuntimeException("Service not found: " + request.getServiceUniqueId()));

        // Check if there is an existing application
        var existingOpt = applicationRepository.findByProvider_IdAndService_Id(provider.getId(), service.getId());
        ProviderServiceApplicationEntity app;

        if (existingOpt.isPresent()) {
            app = existingOpt.get();
            if ("PENDING".equalsIgnoreCase(app.getStatus())) {
                throw new IllegalStateException("You already have an application under review for this service.");
            }
            if ("APPROVED".equalsIgnoreCase(app.getStatus())) {
                throw new IllegalStateException("You are already approved to offer this service.");
            }
            // For REJECTED or MORE_INFO_REQUESTED: re-apply
            app.setStatus("PENDING");
            app.setRejectionReason(null);
            app.setAdminNotes(null);
        } else {
            app = new ProviderServiceApplicationEntity();
            app.setUniqueId(common.generateUniqueId());
            app.setProvider(provider);
            app.setService(service);
            app.setStatus("PENDING");
        }

        app.setExperienceYears(request.getExperienceYears() != null ? request.getExperienceYears() : 1);
        app.setCertifications(request.getCertifications());
        app.setServiceArea(request.getServiceArea());
        app.setNotes(request.getNotes());

        app = applicationRepository.save(app);
        return mapToResponse(app);
    }

    @Transactional(readOnly = true)
    public List<ProviderServiceApplicationResponseModel> getApplicationsByProvider(String providerUniqueId) {
        return applicationRepository.findByProvider_UniqueId(providerUniqueId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProviderServiceApplicationResponseModel> getAllApplications(String status) {
        List<ProviderServiceApplicationEntity> list;
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            list = applicationRepository.findByStatus(status.toUpperCase());
        } else {
            list = applicationRepository.findAll();
        }
        return list.stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProviderServiceApplicationResponseModel getApplicationByUniqueId(String uniqueId) {
        ProviderServiceApplicationEntity app = applicationRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + uniqueId));
        return mapToResponse(app);
    }

    @Transactional
    public ProviderServiceApplicationResponseModel reviewApplication(String uniqueId, ProviderServiceApplicationReviewRequestModel reviewRequest) {
        ProviderServiceApplicationEntity app = applicationRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + uniqueId));

        String targetStatus = reviewRequest.getStatus() != null ? reviewRequest.getStatus().toUpperCase() : "PENDING";
        app.setStatus(targetStatus);
        app.setAdminNotes(reviewRequest.getAdminNotes());

        if ("APPROVED".equalsIgnoreCase(targetStatus)) {
            app.setRejectionReason(null);

            // Automatically activate service in provider's service history so they are eligible for customer bookings
            String serviceTypeStr = app.getService().getServiceType();
            List<ProviderServiceHistoryEntity> existingHistory = historyRepository
                    .findByProvider_UniqueIdAndService(app.getProvider().getUniqueId(), serviceTypeStr);

            ProviderServiceHistoryEntity history;
            if (!existingHistory.isEmpty()) {
                history = existingHistory.get(0);
                history.setStatus("ACTIVE");
                history.setUpdatedDate(LocalDateTime.now());
            } else {
                history = new ProviderServiceHistoryEntity();
                history.setUniqueId(common.generateUniqueId());
                history.setProvider(app.getProvider());
                history.setService(serviceTypeStr);
                history.setStatus("ACTIVE");
                history.setStartDate(LocalDate.now());
                history.setNotes("Approved via Service Application #" + app.getUniqueId());
                history.setCreatedDate(LocalDateTime.now());
                history.setUpdatedDate(LocalDateTime.now());
            }
            historyRepository.save(history);

        } else if ("REJECTED".equalsIgnoreCase(targetStatus)) {
            app.setRejectionReason(reviewRequest.getRejectionReason() != null ? reviewRequest.getRejectionReason() : "Application criteria not met.");
        }

        app = applicationRepository.save(app);
        return mapToResponse(app);
    }

    private ProviderServiceApplicationResponseModel mapToResponse(ProviderServiceApplicationEntity app) {
        ProviderServiceApplicationResponseModel model = new ProviderServiceApplicationResponseModel();
        model.setId(app.getId());
        model.setUniqueId(app.getUniqueId());

        if (app.getProvider() != null) {
            model.setProviderUniqueId(app.getProvider().getUniqueId());
            model.setProviderName(app.getProvider().getName());
            model.setProviderEmail(app.getProvider().getEmail());
            model.setProviderPhone(app.getProvider().getPhoneNumber());
        }

        if (app.getService() != null) {
            model.setServiceUniqueId(app.getService().getUniqueId());
            model.setServiceName(formatServiceName(app.getService().getServiceType()));
            model.setServiceType(app.getService().getServiceType());
        }

        model.setStatus(app.getStatus());
        model.setExperienceYears(app.getExperienceYears());
        model.setCertifications(app.getCertifications());
        model.setServiceArea(app.getServiceArea());
        model.setNotes(app.getNotes());
        model.setRejectionReason(app.getRejectionReason());
        model.setAdminNotes(app.getAdminNotes());
        model.setCreatedAt(app.getCreatedAt());
        model.setUpdatedAt(app.getUpdatedAt());
        return model;
    }

    private String formatServiceName(String raw) {
        if (raw == null) return "Service";
        String s = raw.replaceAll("_", " ").toLowerCase();
        StringBuilder sb = new StringBuilder();
        for (String word : s.split(" ")) {
            if (!word.isBlank()) {
                sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
