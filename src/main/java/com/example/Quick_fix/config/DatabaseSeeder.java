package com.example.Quick_fix.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.Quick_fix.Entity.ProviderAddressEntity;
import com.example.Quick_fix.Entity.ProviderEntity;
import com.example.Quick_fix.Entity.ProviderServiceHistoryEntity;
import com.example.Quick_fix.Entity.ServiceEntity;
import com.example.Quick_fix.Enums.ServiceType;
import com.example.Quick_fix.repository.ProviderRepository;
import com.example.Quick_fix.repository.ServiceRepository;

@Configuration
public class DatabaseSeeder {
    @Bean
    CommandLineRunner seedDatabase(ServiceRepository serviceRepository, ProviderRepository providerRepository) {
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
        };
    }

    private ServiceEntity service(ServiceType type, String description, double charge) {
        ServiceEntity service = new ServiceEntity();
        service.setUniqueId(type.name().substring(0, 6));
        service.setServiceType(type);
        service.setDescription(description);
        service.setServiceCharge(charge);
        service.setActive(true);
        return service;
    }
}
