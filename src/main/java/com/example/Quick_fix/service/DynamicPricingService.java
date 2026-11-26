package com.example.Quick_fix.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Quick_fix.Entity.*;
import com.example.Quick_fix.Enums.PricingModel;
import com.example.Quick_fix.ResponseModel.*;
import com.example.Quick_fix.exception.ResourceNotFoundException;
import com.example.Quick_fix.repository.*;
import com.example.Quick_fix.requestModel.PricingCalculationRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicPricingService {

    private final ServiceRepository serviceRepository;
    private final ServiceOptionGroupRepository groupRepository;
    private final ServiceOptionRepository optionRepository;
    private final ServicePackageRepository packageRepository;
    private final BookingPricingDetailRepository pricingDetailRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional(readOnly = true)
    public ServicePricingConfigResponseModel getPricingConfig(String serviceUniqueId) {
        ServiceEntity service = getServiceByUniqueIdOrId(serviceUniqueId);

        ServicePricingConfigResponseModel config = new ServicePricingConfigResponseModel();
        config.setServiceId(service.getId());
        config.setServiceUniqueId(service.getUniqueId());
        config.setServiceName(formatServiceName(service.getServiceType()));
        config.setServiceType(service.getServiceType());
        config.setPricingModel(service.getPricingModel() != null ? service.getPricingModel() : "FIXED");
        config.setBaseServiceCharge(BigDecimal.valueOf(service.getServiceCharge() != null ? service.getServiceCharge() : 0.0));
        config.setActive(Boolean.TRUE.equals(service.getActive()));

        List<ServiceOptionGroupEntity> groups = groupRepository.findByService_IdAndActiveTrue(service.getId());
        List<ServiceOptionGroupResponseModel> groupModels = groups.stream().map(g -> {
            ServiceOptionGroupResponseModel gm = new ServiceOptionGroupResponseModel();
            gm.setId(g.getId());
            gm.setGroupName(g.getGroupName());
            gm.setSelectionType(g.getSelectionType());
            gm.setIsRequired(Boolean.TRUE.equals(g.getIsRequired()));
            gm.setMinSelections(g.getMinSelections());
            gm.setMaxSelections(g.getMaxSelections());
            gm.setActive(Boolean.TRUE.equals(g.getActive()));
            gm.setInputType(g.getInputType() != null ? g.getInputType() : "SELECT");
            gm.setUnitRate(g.getUnitRate());
            gm.setUnitLabel(g.getUnitLabel());
            gm.setMinValue(g.getMinValue());
            gm.setMaxValue(g.getMaxValue());

            List<ServiceOptionEntity> options = optionRepository.findByGroup_IdAndActiveTrue(g.getId());
            gm.setOptions(options.stream().map(o -> {
                ServiceOptionResponseModel om = new ServiceOptionResponseModel();
                om.setId(o.getId());
                om.setGroupId(g.getId());
                om.setOptionName(o.getOptionName());
                om.setAdditionalCharge(o.getAdditionalCharge());
                om.setActive(Boolean.TRUE.equals(o.getActive()));
                return om;
            }).toList());

            return gm;
        }).toList();
        config.setOptionGroups(groupModels);

        List<ServicePackageEntity> packages = packageRepository.findByService_IdAndActiveTrue(service.getId());
        config.setPackages(packages.stream().map(p -> {
            ServicePackageResponseModel pm = new ServicePackageResponseModel();
            pm.setId(p.getId());
            pm.setServiceId(service.getId());
            pm.setPackageName(p.getPackageName());
            pm.setDescription(p.getDescription());
            pm.setPackagePrice(p.getPackagePrice());
            pm.setActive(Boolean.TRUE.equals(p.getActive()));
            return pm;
        }).toList());

        return config;
    }

    @Transactional(readOnly = true)
    public List<ServiceOptionGroupResponseModel> getOptionGroups(String serviceUniqueId) {
        return getPricingConfig(serviceUniqueId).getOptionGroups();
    }

    @Transactional(readOnly = true)
    public List<ServicePackageResponseModel> getPackages(String serviceUniqueId) {
        return getPricingConfig(serviceUniqueId).getPackages();
    }

    @Transactional(readOnly = true)
    public PricingCalculationResponseModel calculatePrice(PricingCalculationRequestModel request) {
        String identifier = request.getServiceUniqueId();
        if (identifier == null && request.getServiceId() != null) {
            identifier = String.valueOf(request.getServiceId());
        }
        if (identifier == null) {
            throw new IllegalArgumentException("serviceUniqueId or serviceId is required for pricing calculation");
        }

        ServiceEntity service = getServiceByUniqueIdOrId(identifier);
        if (!Boolean.TRUE.equals(service.getActive())) {
            throw new IllegalStateException("Requested service is currently inactive.");
        }

        String rawModel = service.getPricingModel() != null ? service.getPricingModel().toUpperCase() : "FIXED";
        PricingModel model;
        try {
            model = PricingModel.valueOf(rawModel);
        } catch (IllegalArgumentException e) {
            model = PricingModel.FIXED;
        }

        PricingCalculationResponseModel response = new PricingCalculationResponseModel();
        response.setServiceUniqueId(service.getUniqueId());
        response.setServiceName(formatServiceName(service.getServiceType()));
        response.setPricingModel(model.name());
        response.setBreakdown(new ArrayList<>());

        BigDecimal baseCharge = BigDecimal.valueOf(service.getServiceCharge() != null ? service.getServiceCharge() : 0.0);
        response.setBaseCharge(baseCharge);
        response.setOptionsCharge(BigDecimal.ZERO);
        response.setPackageCharge(BigDecimal.ZERO);

        switch (model) {
            case FIXED -> calculateFixedPrice(service, response);
            case OPTIONS -> calculateOptionsPrice(service, request, response);
            case PER_UNIT -> calculateUnitOrHourlyPrice(service, request, response, false);
            case HOURLY -> calculateUnitOrHourlyPrice(service, request, response, true);
            case PACKAGE -> calculatePackagePrice(service, request, response);
            case QUOTATION -> calculateQuotationPrice(service, request, response);
        }

        return response;
    }

    private void calculateFixedPrice(ServiceEntity service, PricingCalculationResponseModel response) {
        BigDecimal base = BigDecimal.valueOf(service.getServiceCharge() != null ? service.getServiceCharge() : 0.0);
        response.setSubtotal(base);
        response.setIsQuotation(false);

        PricingCalculationResponseModel.BreakdownItem item = new PricingCalculationResponseModel.BreakdownItem();
        item.setLabel("Base Service Charge (" + formatServiceName(service.getServiceType()) + ")");
        item.setType("BASE");
        item.setAmount(base);
        item.setDetails("Standard fixed price");
        response.getBreakdown().add(item);
    }

    private void calculateOptionsPrice(ServiceEntity service, PricingCalculationRequestModel request, PricingCalculationResponseModel response) {
        List<ServiceOptionGroupEntity> groups = groupRepository.findByService_IdAndActiveTrue(service.getId());
        Map<Long, ServiceOptionGroupEntity> groupMap = groups.stream().collect(Collectors.toMap(ServiceOptionGroupEntity::getId, g -> g));

        Map<Long, List<Long>> selectedMap = new HashMap<>();
        if (request.getSelectedOptions() != null) {
            for (PricingCalculationRequestModel.SelectedOptionGroupRequest s : request.getSelectedOptions()) {
                if (s.getGroupId() != null && s.getOptionIds() != null) {
                    selectedMap.put(s.getGroupId(), s.getOptionIds());
                }
            }
        }

        BigDecimal optionsTotal = BigDecimal.ZERO;

        for (ServiceOptionGroupEntity group : groups) {
            List<Long> selectedOptionIds = selectedMap.getOrDefault(group.getId(), Collections.emptyList());

            if (Boolean.TRUE.equals(group.getIsRequired()) && selectedOptionIds.isEmpty()) {
                List<ServiceOptionEntity> activeOpts = optionRepository.findByGroup_IdAndActiveTrue(group.getId());
                if (!activeOpts.isEmpty()) {
                    selectedOptionIds = List.of(activeOpts.get(0).getId());
                } else {
                    throw new IllegalArgumentException("Selection required for option group: " + group.getGroupName());
                }
            }

            if ("SINGLE".equalsIgnoreCase(group.getSelectionType()) && selectedOptionIds.size() > 1) {
                throw new IllegalArgumentException("Only 1 selection allowed for group: " + group.getGroupName());
            }

            if (group.getMinSelections() != null && selectedOptionIds.size() < group.getMinSelections()) {
                throw new IllegalArgumentException("At least " + group.getMinSelections() + " selection(s) required for group: " + group.getGroupName());
            }

            if (group.getMaxSelections() != null && selectedOptionIds.size() > group.getMaxSelections()) {
                throw new IllegalArgumentException("At most " + group.getMaxSelections() + " selection(s) allowed for group: " + group.getGroupName());
            }

            for (Long optId : selectedOptionIds) {
                ServiceOptionEntity opt = optionRepository.findById(optId)
                        .orElseThrow(() -> new ResourceNotFoundException("Option not found with ID: " + optId));

                if (!opt.getGroup().getId().equals(group.getId())) {
                    throw new IllegalArgumentException("Option " + opt.getOptionName() + " does not belong to group " + group.getGroupName());
                }
                if (!Boolean.TRUE.equals(opt.getActive())) {
                    throw new IllegalArgumentException("Option " + opt.getOptionName() + " is currently inactive");
                }

                BigDecimal charge = opt.getAdditionalCharge() != null ? opt.getAdditionalCharge() : BigDecimal.ZERO;
                optionsTotal = optionsTotal.add(charge);

                PricingCalculationResponseModel.BreakdownItem item = new PricingCalculationResponseModel.BreakdownItem();
                item.setLabel(group.getGroupName() + " : " + opt.getOptionName());
                item.setType("OPTION");
                item.setAmount(charge);
                item.setDetails("Additional option selection");
                response.getBreakdown().add(item);
            }
        }

        BigDecimal base = BigDecimal.valueOf(service.getServiceCharge() != null ? service.getServiceCharge() : 0.0);
        BigDecimal subtotal = base.add(optionsTotal);
        response.setBaseCharge(base);
        response.setOptionsCharge(optionsTotal);
        response.setSubtotal(subtotal);
        response.setIsQuotation(false);
    }

    private void calculateUnitOrHourlyPrice(ServiceEntity service, PricingCalculationRequestModel request, PricingCalculationResponseModel response, boolean isHourly) {
        List<ServiceOptionGroupEntity> groups = groupRepository.findByService_IdAndActiveTrue(service.getId());
        ServiceOptionGroupEntity unitGroup = groups.stream()
                .filter(g -> isHourly ? "HOURLY".equalsIgnoreCase(g.getInputType()) || "Session Duration".equalsIgnoreCase(g.getGroupName()) || "hours".equalsIgnoreCase(g.getUnitLabel())
                        : "AREA".equalsIgnoreCase(g.getInputType()) || "NUMBER".equalsIgnoreCase(g.getInputType()) || g.getUnitRate() != null && g.getUnitRate().compareTo(BigDecimal.ZERO) > 0)
                .findFirst()
                .orElse(groups.isEmpty() ? null : groups.get(0));

        if (unitGroup == null || unitGroup.getUnitRate() == null) {
            BigDecimal base = BigDecimal.valueOf(service.getServiceCharge() != null ? service.getServiceCharge() : 0.0);
            response.setSubtotal(base);
            response.setIsQuotation(false);
            return;
        }

        BigDecimal qty = BigDecimal.ZERO;
        if (request.getQuantities() != null) {
            for (PricingCalculationRequestModel.QuantityInputRequest q : request.getQuantities()) {
                if (q.getGroupId() == null || q.getGroupId().equals(unitGroup.getId())) {
                    if (q.getValue() != null) {
                        qty = q.getValue();
                        break;
                    }
                }
            }
        }

        if (qty.compareTo(BigDecimal.ZERO) <= 0) {
            qty = unitGroup.getMinValue() != null ? unitGroup.getMinValue() : BigDecimal.ONE;
        }

        if (unitGroup.getMinValue() != null && qty.compareTo(unitGroup.getMinValue()) < 0) {
            throw new IllegalArgumentException("Minimum value for " + unitGroup.getGroupName() + " is " + unitGroup.getMinValue() + " " + (unitGroup.getUnitLabel() != null ? unitGroup.getUnitLabel() : ""));
        }
        if (unitGroup.getMaxValue() != null && qty.compareTo(unitGroup.getMaxValue()) > 0) {
            throw new IllegalArgumentException("Maximum value for " + unitGroup.getGroupName() + " is " + unitGroup.getMaxValue() + " " + (unitGroup.getUnitLabel() != null ? unitGroup.getUnitLabel() : ""));
        }

        BigDecimal unitRate = unitGroup.getUnitRate();
        BigDecimal total = unitRate.multiply(qty).setScale(2, RoundingMode.HALF_UP);

        response.setUnitRate(unitRate);
        response.setQuantity(qty);
        response.setUnitLabel(unitGroup.getUnitLabel());
        response.setSubtotal(total);
        response.setIsQuotation(false);

        PricingCalculationResponseModel.BreakdownItem item = new PricingCalculationResponseModel.BreakdownItem();
        item.setLabel(unitGroup.getGroupName() + " (" + qty + " " + (unitGroup.getUnitLabel() != null ? unitGroup.getUnitLabel() : "") + " × ₹" + unitRate + ")");
        item.setType(isHourly ? "HOURLY" : "UNIT");
        item.setAmount(total);
        item.setDetails("Quantity calculation: " + qty + " × " + unitRate);
        response.getBreakdown().add(item);
    }

    private void calculatePackagePrice(ServiceEntity service, PricingCalculationRequestModel request, PricingCalculationResponseModel response) {
        if (request.getPackageId() == null) {
            List<ServicePackageEntity> pkgs = packageRepository.findByService_IdAndActiveTrue(service.getId());
            if (pkgs.isEmpty()) {
                BigDecimal base = BigDecimal.valueOf(service.getServiceCharge() != null ? service.getServiceCharge() : 0.0);
                response.setSubtotal(base);
                response.setIsQuotation(false);
                return;
            }
            request.setPackageId(pkgs.get(0).getId());
        }

        ServicePackageEntity pkg = packageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new ResourceNotFoundException("Package not found with ID: " + request.getPackageId()));

        if (!pkg.getService().getId().equals(service.getId())) {
            throw new IllegalArgumentException("Selected package does not belong to service: " + service.getServiceType());
        }
        if (!Boolean.TRUE.equals(pkg.getActive())) {
            throw new IllegalArgumentException("Selected package is currently inactive");
        }

        response.setPackageCharge(pkg.getPackagePrice());
        response.setSubtotal(pkg.getPackagePrice());
        response.setIsQuotation(false);

        PricingCalculationResponseModel.BreakdownItem item = new PricingCalculationResponseModel.BreakdownItem();
        item.setLabel(pkg.getPackageName() + " Package");
        item.setType("PACKAGE");
        item.setAmount(pkg.getPackagePrice());
        item.setDetails(pkg.getDescription() != null ? pkg.getDescription() : "Standard package tier");
        response.getBreakdown().add(item);
    }

    private void calculateQuotationPrice(ServiceEntity service, PricingCalculationRequestModel request, PricingCalculationResponseModel response) {
        response.setSubtotal(BigDecimal.ZERO);
        response.setIsQuotation(true);
        response.setQuotationStatus("PENDING_PROVIDER_QUOTE");

        PricingCalculationResponseModel.BreakdownItem item = new PricingCalculationResponseModel.BreakdownItem();
        item.setLabel("Custom Requirement Inspection & Quotation");
        item.setType("QUOTATION");
        item.setAmount(BigDecimal.ZERO);
        item.setDetails("Final price will be provided by technician after inspecting work requirements.");
        response.getBreakdown().add(item);
    }

    @Transactional
    public BookingPricingDetailEntity savePricingSnapshot(ServiceBookingEntity booking, PricingCalculationRequestModel request, PricingCalculationResponseModel calculation) {
        BookingPricingDetailEntity detail = pricingDetailRepository.findByBooking_Id(booking.getId())
                .orElse(new BookingPricingDetailEntity());

        detail.setBooking(booking);
        detail.setPricingModel(calculation.getPricingModel());
        detail.setBaseCharge(calculation.getBaseCharge() != null ? calculation.getBaseCharge() : BigDecimal.ZERO);
        detail.setOptionsCharge(calculation.getOptionsCharge() != null ? calculation.getOptionsCharge() : BigDecimal.ZERO);
        detail.setPackageCharge(calculation.getPackageCharge() != null ? calculation.getPackageCharge() : BigDecimal.ZERO);
        detail.setUnitRate(calculation.getUnitRate());
        detail.setQuantity(calculation.getQuantity());
        detail.setUnitLabel(calculation.getUnitLabel());

        if (request != null && request.getPackageId() != null) {
            packageRepository.findById(request.getPackageId()).ifPresent(pkg -> {
                detail.setPackageId(pkg.getId());
                detail.setPackageName(pkg.getPackageName());
            });
        }

        try {
            detail.setPricingSnapshotJson(objectMapper.writeValueAsString(calculation));
        } catch (Exception e) {
            detail.setPricingSnapshotJson("{}");
        }

        if (request != null) {
            detail.setCustomerNotes(request.getCustomerNotes());
        }

        if (Boolean.TRUE.equals(calculation.getIsQuotation())) {
            detail.setQuotationStatus("PENDING_PROVIDER_QUOTE");
        }

        return pricingDetailRepository.save(detail);
    }

    private ServiceEntity getServiceByUniqueIdOrId(String identifier) {
        Optional<ServiceEntity> byUnique = serviceRepository.findByUniqueId(identifier);
        if (byUnique.isPresent()) return byUnique.get();

        try {
            Integer id = Integer.parseInt(identifier);
            return serviceRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID/uniqueId: " + identifier));
        } catch (NumberFormatException ignored) {
            throw new ResourceNotFoundException("Service not found with uniqueId: " + identifier);
        }
    }

    private String formatServiceName(String rawType) {
        if (rawType == null) return "Service";
        return Arrays.stream(rawType.toLowerCase().split("_"))
                .map(s -> s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1))
                .collect(Collectors.joining(" "));
    }
}
