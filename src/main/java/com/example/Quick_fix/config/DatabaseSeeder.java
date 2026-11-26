package com.example.Quick_fix.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.Quick_fix.Entity.ProviderAddressEntity;
import com.example.Quick_fix.Entity.ProviderEntity;
import com.example.Quick_fix.Entity.ProviderServiceHistoryEntity;
import com.example.Quick_fix.Entity.ServiceEntity;
import com.example.Quick_fix.Entity.ServiceGuidanceEntity;
import com.example.Quick_fix.Entity.ServiceGuidanceItemEntity;
import com.example.Quick_fix.Enums.ServiceType;
import com.example.Quick_fix.repository.ProviderRepository;
import com.example.Quick_fix.repository.ServiceGuidanceRepository;
import com.example.Quick_fix.repository.ServiceRepository;

@Configuration
public class DatabaseSeeder {
    @Bean
    CommandLineRunner seedDatabase(ServiceRepository serviceRepository, ProviderRepository providerRepository, ServiceGuidanceRepository guidanceRepository) {
        return args -> {
            if (serviceRepository.count() == 0) {
                serviceRepository.saveAll(List.of(
                        service(ServiceType.AC_REPAIR, "AC inspection, cleaning and repair", 399.0),
                        service(ServiceType.ELECTRICAL, "Switchboard, wiring and electrical repairs", 299.0),
                        service(ServiceType.PLUMBING, "Tap leakage, pipe and drain repairs", 249.0),
                        service(ServiceType.CLEANING, "Bathroom and home deep cleaning", 499.0)));
            }

            if (providerRepository.count() == 0) {
                ProviderEntity provider = new ProviderEntity();
                provider.setUniqueId("PRO001");
                provider.setName("Quick-Fix Service Partner");
                provider.setPhoneNumber("+91 98765 43210");
                provider.setEmail("partner@quickfix.local");

                ProviderAddressEntity address = new ProviderAddressEntity();
                address.setProvider(provider);
                address.setUniqueId("ADDR01");
                address.setAddressType("HOME");
                address.setAddressLine1("R.S. Puram");
                address.setCity("Coimbatore");
                address.setState("Tamil Nadu");
                address.setCountry("India");
                address.setPostalCode("641002");
                address.setPrimary(true);
                address.setLatitude(11.0168);
                address.setLongitude(76.9558);
                provider.setAddresses(List.of(address));

                provider.setProviderServices(serviceRepository.findAll().stream().map(service -> {
                    ProviderServiceHistoryEntity history = new ProviderServiceHistoryEntity();
                    history.setProvider(provider);
                    history.setService(service.getServiceType());
                    history.setStatus("ACTIVE");
                    history.setUniqueId("SVC-" + service.getUniqueId());
                    return history;
                }).toList());
                provider.setContacts(List.of());
                provider.setDocuments(List.of());
                providerRepository.save(provider);
            }

            // Seed guidance for all services if guidance count is 0
            if (guidanceRepository.count() == 0) {
                List<ServiceEntity> services = serviceRepository.findAll();
                List<ServiceGuidanceEntity> guidances = new ArrayList<>();

                for (ServiceEntity srv : services) {
                    ServiceGuidanceEntity g = new ServiceGuidanceEntity();
                    g.setService(srv);
                    g.setActive(true);

                    String type = srv.getServiceType() != null ? srv.getServiceType().toUpperCase() : "";
                    if (type.contains("CLEAN")) {
                        g.setOverview("Professional deep cleaning and sanitization service for residential rooms, bathrooms, and fixtures.");
                        g.setInclusions("Tile & floor scrubbing\nStain removal & sanitization\nMirror & glass polishing\nTap & fixture de-scaling");
                        g.setExclusions("Structural tile replacement\nWall painting or plaster repair\nExternal drainage excavation");
                        g.setResponsibilities("1. Arrive on schedule in verified attire with shoe covers.\n2. Walk through the space with the customer and note existing conditions.\n3. Apply eco-friendly cleaning agents and let them penetrate.\n4. Scrub surfaces thoroughly with rotary or hand scrubbers.\n5. Rinse, dry, and disinfect high-touch contact points.\n6. Request final customer inspection and sign-off.");
                        g.setMinExperienceYears(1);
                        g.setEstimatedDuration("45 - 90 mins");
                        g.setEstimatedEarningMin(250.0);
                        g.setEstimatedEarningMax(700.0);
                        g.setEarningBasis("Estimated provider payout per completed job (non-guaranteed)");
                        g.setSafetyInstructions("Wear safety goggles, waterproof gloves, and non-slip footwear. Ensure proper ventilation when working with descaling chemicals.");
                        g.setEligibilityCriteria("Minimum 1 year residential cleaning experience, background verification check, own transport.");

                        g.setItems(List.of(
                                item(g, "SKILL", "Stain identification & chemical dilution", true, "Knowledge of acid-free cleaning agents"),
                                item(g, "SKILL", "Machine & manual scrubbing techniques", true, "Safe operation of tile scrubbers"),
                                item(g, "TOOL", "Tile scrubbing brush & microfibre squeegee", true, "Must be clean and sanitized"),
                                item(g, "TOOL", "High-power wet vacuum cleaner", false, "Optional for rapid drying"),
                                item(g, "MATERIAL", "Eco-friendly disinfectant & descaling liquid", true, "Non-hazardous formula")
                        ));
                    } else if (type.contains("AC") || type.contains("APPLIANCE")) {
                        g.setOverview("Inspection, jet pump coil washing, filter disinfection, and electrical troubleshooting for air conditioning units.");
                        g.setInclusions("Indoor and outdoor unit inspection\nJet pump water wash for cooling coils\nAir filter sanitization\nRefrigerant pressure verification");
                        g.setExclusions("Copper piping installation beyond 3 meters\nFull compressor rewinding or replacement");
                        g.setResponsibilities("1. Test AC operations before starting work.\n2. Disconnect electrical supply safely from the MCB breaker.\n3. Wrap indoor unit with service jacket to catch water runoff.\n4. High-pressure wash coils and blower wheel.\n5. Measure operating refrigerant pressure and running amps.\n6. Clean workspace and confirm cooling with customer.");
                        g.setMinExperienceYears(2);
                        g.setEstimatedDuration("60 - 120 mins");
                        g.setEstimatedEarningMin(400.0);
                        g.setEstimatedEarningMax(1200.0);
                        g.setEarningBasis("Estimated provider payout per completed job (non-guaranteed)");
                        g.setSafetyInstructions("Always lockout electrical supply before opening AC cabinet. Use secure ladders and safety harness when servicing outdoor units at heights.");
                        g.setEligibilityCriteria("HVAC / Refrigeration diploma or 2+ years field apprenticeship. Safety certified.");

                        g.setItems(List.of(
                                item(g, "SKILL", "Refrigerant cycle diagnostics & leak detection", true, "R32/R410A pressure standards"),
                                item(g, "SKILL", "Safe jet-wash coil cleaning", true, "Avoid bending delicate cooling fins"),
                                item(g, "TOOL", "High-pressure washer with AC service jacket", true, "Prevents indoor water spillage"),
                                item(g, "TOOL", "Digital manifold gauge & clamp multimeter", true, "Accurate amp and pressure readings"),
                                item(g, "MATERIAL", "Coil cleaning detergent & foam cleaner", true, "Alkaline fin-safe formula")
                        ));
                    } else if (type.contains("ELECT")) {
                        g.setOverview("Safe diagnostics and repair of domestic electrical circuitry, switchboards, MCB breakers, and lighting fixtures.");
                        g.setInclusions("Fault tracing & short-circuit repair\nSwitch & socket replacement\nFan & light fixture installation\nEarthing and voltage check");
                        g.setExclusions("Major civil conduit cutting without prior quotation\nHigh-voltage line connection before meter box");
                        g.setResponsibilities("1. Test circuit polarity and line voltage using multimeter.\n2. Switch off the main MCB breaker and verify zero voltage.\n3. Replace damaged wiring with certified heat-resistant copper conductors.\n4. Properly secure switchboard faceplates and earthing wires.\n5. Test connected loads under full power.\n6. Explain work completed to customer.");
                        g.setMinExperienceYears(2);
                        g.setEstimatedDuration("30 - 60 mins");
                        g.setEstimatedEarningMin(250.0);
                        g.setEstimatedEarningMax(650.0);
                        g.setEarningBasis("Estimated provider payout per completed job (non-guaranteed)");
                        g.setSafetyInstructions("Never work on live circuits. Wear 1000V rated insulated boots and use certified insulated screwdrivers.");
                        g.setEligibilityCriteria("Certified wireman license or ITI in electrical trade. Minimum 2 years experience.");

                        g.setItems(List.of(
                                item(g, "SKILL", "Multimeter diagnostics & circuit continuity", true, "Quick fault isolation"),
                                item(g, "SKILL", "Load distribution & safe breaker sizing", true, "Prevents circuit overloads"),
                                item(g, "TOOL", "1000V VDE insulated hand tool set", true, "Screwdrivers, pliers, wire strippers"),
                                item(g, "TOOL", "Digital multimeter & neon voltage tester", true, "Safety verification"),
                                item(g, "MATERIAL", "Insulating PVC tape & heat shrink sleeves", true, "Fire retardant grade")
                        ));
                    } else if (type.contains("PLUMB")) {
                        g.setOverview("Rapid plumbing leak detection, fixture replacement, tap repair, and domestic drain unclogging.");
                        g.setInclusions("Tap, bibcock, and mixer replacement\nSink drain trap unblocking\nFlush tank mechanism repair\nMinor pipe leak sealing");
                        g.setExclusions("Excavation of underground sewer mains\nExternal overhead tank structural replacement");
                        g.setResponsibilities("1. Locate and isolate the water supply shut-off valve.\n2. Inspect fittings and diagnose leakage or blockage point.\n3. Disassemble old fitting carefully without scratching sanitaryware.\n4. Apply Teflon tape and install new washer or cartridge.\n5. Reconnect and inspect under full water pressure for 5 minutes.\n6. Ensure dry floor and hand over clean workspace.");
                        g.setMinExperienceYears(1);
                        g.setEstimatedDuration("30 - 75 mins");
                        g.setEstimatedEarningMin(220.0);
                        g.setEstimatedEarningMax(600.0);
                        g.setEarningBasis("Estimated provider payout per completed job (non-guaranteed)");
                        g.setSafetyInstructions("Wipe water puddles immediately to prevent slip hazards. Wear safety glasses when working beneath sinks.");
                        g.setEligibilityCriteria("Minimum 1 year practical residential plumbing experience.");

                        g.setItems(List.of(
                                item(g, "SKILL", "Thread sealing & gasket fitting", true, "Leak-free assembly"),
                                item(g, "SKILL", "Drain clearing with mechanical auger", true, "Non-destructive unblocking"),
                                item(g, "TOOL", "Adjustable pipe wrench & basin spanner", true, "Non-marring jaws for chrome fittings"),
                                item(g, "TOOL", "Drain snake spring auger", true, "Standard drain clearance"),
                                item(g, "MATERIAL", "PTFE Teflon tape & silicone sealant", true, "Plumbing grade water sealing")
                        ));
                    } else {
                        g.setOverview("Reliable doorstep professional service executed by verified QUIX FIX technicians.");
                        g.setInclusions("Technician visit & inspection\nStandard labor for execution\nWorkmanship guarantee");
                        g.setExclusions("Major civil alterations\nSpare parts cost unless billed separately");
                        g.setResponsibilities("1. Confirm arrival on time.\n2. Assess requirement and agree scope with customer.\n3. Execute service with approved tools and safety practices.\n4. Clean up workspace and solicit review.");
                        g.setMinExperienceYears(1);
                        g.setEstimatedDuration("45 - 90 mins");
                        g.setEstimatedEarningMin(300.0);
                        g.setEstimatedEarningMax(800.0);
                        g.setEarningBasis("Estimated provider payout per completed job (non-guaranteed)");
                        g.setSafetyInstructions("Follow standard occupational safety precautions. Wear appropriate personal protective equipment.");
                        g.setEligibilityCriteria("Valid government ID, clean background check, verified phone number.");

                        g.setItems(List.of(
                                item(g, "SKILL", "Standard trade skill execution", true, "Certified field practice"),
                                item(g, "TOOL", "Professional tool kit", true, "Complete hand & power tools"),
                                item(g, "MATERIAL", "Standard service consumables", false, "As required by specific job")
                        ));
                    }

                    guidances.add(g);
                }
                guidanceRepository.saveAll(guidances);
            }
        };
    }

    private ServiceGuidanceItemEntity item(ServiceGuidanceEntity g, String type, String name, boolean mandatory, String notes) {
        ServiceGuidanceItemEntity item = new ServiceGuidanceItemEntity();
        item.setGuidance(g);
        item.setItemType(type);
        item.setName(name);
        item.setMandatory(mandatory);
        item.setNotes(notes);
        return item;
    }

    private ServiceEntity service(ServiceType type, String description, double charge) {
        ServiceEntity service = new ServiceEntity();
        service.setUniqueId(type.name().substring(0, 6));
        service.setServiceType(type.name());
        service.setDescription(description);
        service.setServiceCharge(charge);
        service.setActive(true);
        service.setPricingModel("FIXED");
        return service;
    }
}
