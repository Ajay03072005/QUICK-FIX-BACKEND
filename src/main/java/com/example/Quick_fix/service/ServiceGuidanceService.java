package com.example.Quick_fix.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Quick_fix.Entity.ProviderEntity;
import com.example.Quick_fix.Entity.ProviderServiceApplicationEntity;
import com.example.Quick_fix.Entity.ServiceEntity;
import com.example.Quick_fix.Entity.ServiceGuidanceEntity;
import com.example.Quick_fix.Entity.ServiceGuidanceItemEntity;
import com.example.Quick_fix.ResponseModel.ServiceGuidanceItemResponseModel;
import com.example.Quick_fix.ResponseModel.ServiceGuidanceResponseModel;
import com.example.Quick_fix.repository.ProviderRepository;
import com.example.Quick_fix.repository.ProviderServiceApplicationRepository;
import com.example.Quick_fix.repository.ServiceGuidanceItemRepository;
import com.example.Quick_fix.repository.ServiceGuidanceRepository;
import com.example.Quick_fix.repository.ServiceRepository;
import com.example.Quick_fix.requestModel.ServiceGuidanceItemRequestModel;
import com.example.Quick_fix.requestModel.ServiceGuidanceRequestModel;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceGuidanceService {

    private final ServiceGuidanceRepository guidanceRepository;
    private final ServiceGuidanceItemRepository guidanceItemRepository;
    private final ServiceRepository serviceRepository;
    private final ProviderRepository providerRepository;
    private final ProviderServiceApplicationRepository applicationRepository;

    @Transactional(readOnly = true)
    public ServiceGuidanceResponseModel getGuidanceByServiceUniqueId(String serviceUniqueId, String providerUniqueId) {
        ServiceEntity service = serviceRepository.findByUniqueId(serviceUniqueId)
                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + serviceUniqueId));

        ServiceGuidanceEntity guidance = guidanceRepository.findByService_UniqueIdAndActiveTrue(serviceUniqueId)
                .orElseGet(() -> buildDefaultGuidance(service));

        ServiceGuidanceResponseModel response = mapToResponseModel(guidance, service);

        // Check provider application status if provider is specified
        if (providerUniqueId != null && !providerUniqueId.isBlank()) {
            Optional<ProviderServiceApplicationEntity> appOpt = applicationRepository
                    .findByProvider_UniqueIdAndService_UniqueId(providerUniqueId, serviceUniqueId);
            if (appOpt.isPresent()) {
                ProviderServiceApplicationEntity app = appOpt.get();
                response.setCurrentApplicationStatus(app.getStatus());
                response.setCurrentRejectionReason(app.getRejectionReason());
            }
        }

        return response;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCatalogueGuidanceSummary(String providerUniqueId) {
        List<ServiceEntity> allServices = serviceRepository.findAll().stream()
                .filter(s -> Boolean.TRUE.equals(s.getActive()))
                .toList();

        Map<String, ProviderServiceApplicationEntity> providerApps = new HashMap<>();
        if (providerUniqueId != null && !providerUniqueId.isBlank()) {
            List<ProviderServiceApplicationEntity> apps = applicationRepository.findByProvider_UniqueId(providerUniqueId);
            for (ProviderServiceApplicationEntity a : apps) {
                if (a.getService() != null) {
                    providerApps.put(a.getService().getUniqueId(), a);
                }
            }
        }

        List<Map<String, Object>> results = new ArrayList<>();
        for (ServiceEntity service : allServices) {
            Map<String, Object> item = new HashMap<>();
            item.put("serviceId", service.getId());
            item.put("serviceUniqueId", service.getUniqueId());
            item.put("serviceName", formatServiceName(service.getServiceType()));
            item.put("serviceType", service.getServiceType());
            item.put("categoryName", formatServiceName(service.getServiceType()));
            item.put("description", service.getDescription());
            item.put("pricingModel", service.getPricingModel() != null ? service.getPricingModel() : "FIXED");

            Optional<ServiceGuidanceEntity> gOpt = guidanceRepository.findByService_UniqueIdAndActiveTrue(service.getUniqueId());
            if (gOpt.isPresent()) {
                ServiceGuidanceEntity g = gOpt.get();
                item.put("minExperienceYears", g.getMinExperienceYears());
                item.put("estimatedDuration", g.getEstimatedDuration());
                item.put("estimatedEarningMin", g.getEstimatedEarningMin());
                item.put("estimatedEarningMax", g.getEstimatedEarningMax());
                item.put("earningBasis", g.getEarningBasis());
            } else {
                item.put("minExperienceYears", 1);
                item.put("estimatedDuration", "45 - 90 mins");
                item.put("estimatedEarningMin", 350.0);
                item.put("estimatedEarningMax", 1200.0);
                item.put("earningBasis", "Per completed service booking");
            }

            ProviderServiceApplicationEntity userApp = providerApps.get(service.getUniqueId());
            if (userApp != null) {
                item.put("applicationStatus", userApp.getStatus());
                item.put("applicationUniqueId", userApp.getUniqueId());
                item.put("rejectionReason", userApp.getRejectionReason());
            } else {
                item.put("applicationStatus", "NOT_APPLIED");
            }

            results.add(item);
        }
        return results;
    }

    @Transactional
    public ServiceGuidanceResponseModel saveOrUpdateGuidance(ServiceGuidanceRequestModel request) {
        ServiceEntity service = serviceRepository.findByUniqueId(request.getServiceUniqueId())
                .orElseThrow(() -> new RuntimeException("Service not found: " + request.getServiceUniqueId()));

        ServiceGuidanceEntity guidance = guidanceRepository.findByService_Id(service.getId())
                .orElseGet(() -> {
                    ServiceGuidanceEntity g = new ServiceGuidanceEntity();
                    g.setService(service);
                    return g;
                });

        guidance.setOverview(request.getOverview());
        guidance.setInclusions(request.getInclusions());
        guidance.setExclusions(request.getExclusions());
        guidance.setResponsibilities(request.getResponsibilities());
        guidance.setMinExperienceYears(request.getMinExperienceYears() != null ? request.getMinExperienceYears() : 1);
        guidance.setEstimatedDuration(request.getEstimatedDuration());
        guidance.setEstimatedEarningMin(request.getEstimatedEarningMin());
        guidance.setEstimatedEarningMax(request.getEstimatedEarningMax());
        guidance.setEarningBasis(request.getEarningBasis());
        guidance.setSafetyInstructions(request.getSafetyInstructions());
        guidance.setEligibilityCriteria(request.getEligibilityCriteria());
        guidance.setActive(request.getActive() != null ? request.getActive() : true);

        if (guidance.getId() != null) {
            guidance.getItems().clear();
        }

        if (request.getItems() != null) {
            for (ServiceGuidanceItemRequestModel itemReq : request.getItems()) {
                ServiceGuidanceItemEntity item = new ServiceGuidanceItemEntity();
                item.setGuidance(guidance);
                item.setItemType(itemReq.getItemType());
                item.setName(itemReq.getName());
                item.setMandatory(itemReq.getMandatory() != null ? itemReq.getMandatory() : true);
                item.setNotes(itemReq.getNotes());
                guidance.getItems().add(item);
            }
        }

        guidance = guidanceRepository.save(guidance);
        return mapToResponseModel(guidance, service);
    }

    private ServiceGuidanceResponseModel mapToResponseModel(ServiceGuidanceEntity guidance, ServiceEntity service) {
        ServiceGuidanceResponseModel model = new ServiceGuidanceResponseModel();
        model.setId(guidance.getId());
        model.setServiceId(service.getId());
        model.setServiceUniqueId(service.getUniqueId());
        model.setServiceName(formatServiceName(service.getServiceType()));
        model.setServiceType(service.getServiceType());
        model.setPricingModel(service.getPricingModel() != null ? service.getPricingModel() : "FIXED");
        model.setOverview(guidance.getOverview());
        model.setInclusions(guidance.getInclusions());
        model.setExclusions(guidance.getExclusions());
        model.setResponsibilities(guidance.getResponsibilities());
        model.setMinExperienceYears(guidance.getMinExperienceYears());
        model.setEstimatedDuration(guidance.getEstimatedDuration());
        model.setEstimatedEarningMin(guidance.getEstimatedEarningMin());
        model.setEstimatedEarningMax(guidance.getEstimatedEarningMax());
        model.setEarningBasis(guidance.getEarningBasis());
        model.setSafetyInstructions(guidance.getSafetyInstructions());
        model.setEligibilityCriteria(guidance.getEligibilityCriteria());
        model.setActive(guidance.getActive());

        if (guidance.getItems() != null) {
            for (ServiceGuidanceItemEntity item : guidance.getItems()) {
                ServiceGuidanceItemResponseModel im = new ServiceGuidanceItemResponseModel();
                im.setId(item.getId());
                im.setItemType(item.getItemType());
                im.setName(item.getName());
                im.setMandatory(item.getMandatory());
                im.setNotes(item.getNotes());

                if ("SKILL".equalsIgnoreCase(item.getItemType())) {
                    model.getSkills().add(im);
                } else if ("TOOL".equalsIgnoreCase(item.getItemType())) {
                    model.getTools().add(im);
                } else if ("MATERIAL".equalsIgnoreCase(item.getItemType())) {
                    model.getMaterials().add(im);
                }
            }
        }

        return model;
    }

    private ServiceGuidanceEntity buildDefaultGuidance(ServiceEntity service) {
        ServiceGuidanceEntity g = new ServiceGuidanceEntity();
        g.setService(service);
        g.setOverview(service.getDescription() != null ? service.getDescription() : "Standard certified home service.");
        g.setInclusions("Professional doorstep visit\nInitial diagnostics and inspection\nStandard labor for execution");
        g.setExclusions("Major structural overhauls\nProcurement of spare components unless billed separately");
        g.setResponsibilities("1. Arrive on schedule in proper QUIX FIX attire.\n2. Inspect customer site and explain work before starting.\n3. Execute the service safely using approved tools.\n4. Clean the workspace upon completion.\n5. Request customer review.");
        g.setMinExperienceYears(1);
        g.setEstimatedDuration("45 - 90 minutes");
        g.setEstimatedEarningMin(400.0);
        g.setEstimatedEarningMax(1200.0);
        g.setEarningBasis("Estimated provider payout per completed job (non-guaranteed)");
        g.setSafetyInstructions("Wear protective gear including gloves, mask, and safety goggles where applicable. Disconnect power supplies before handling electrical circuits.");
        g.setEligibilityCriteria("Government-issued identity proof, minimum 1 year practical experience, valid bank account for payouts.");
        g.setActive(true);

        ServiceGuidanceItemEntity skill1 = new ServiceGuidanceItemEntity();
        skill1.setGuidance(g);
        skill1.setItemType("SKILL");
        skill1.setName("Practical domain diagnostics and repair");
        skill1.setMandatory(true);

        ServiceGuidanceItemEntity tool1 = new ServiceGuidanceItemEntity();
        tool1.setGuidance(g);
        tool1.setItemType("TOOL");
        tool1.setName("Professional toolkit & inspection equipment");
        tool1.setMandatory(true);

        ServiceGuidanceItemEntity mat1 = new ServiceGuidanceItemEntity();
        mat1.setGuidance(g);
        mat1.setItemType("MATERIAL");
        mat1.setName("Standard cleaning & surface protection consumables");
        mat1.setMandatory(false);

        g.setItems(List.of(skill1, tool1, mat1));
        return g;
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
