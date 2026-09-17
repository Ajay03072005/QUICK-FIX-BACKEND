package com.example.Quick_fix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.Quick_fix.Entity.CustomerAddressEntity;
import com.example.Quick_fix.Entity.CustomerEntity;
import com.example.Quick_fix.Entity.ProviderAuthEntity;
import com.example.Quick_fix.Entity.ProviderEntity;
import com.example.Quick_fix.Entity.ServiceEntity;
import com.example.Quick_fix.Enums.ServiceType;
import com.example.Quick_fix.ResponseModel.CustomerAuthResponseModel;
import com.example.Quick_fix.ResponseModel.ProviderAuthResponseModel;
import com.example.Quick_fix.commons.Common;
import com.example.Quick_fix.repository.BookingAddressRepository;
import com.example.Quick_fix.repository.BookingRepository;
import com.example.Quick_fix.repository.CustomerAddressRepository;
import com.example.Quick_fix.repository.CustomerAuthRepository;
import com.example.Quick_fix.repository.CustomerRepository;
import com.example.Quick_fix.repository.ProviderAddressRepository;
import com.example.Quick_fix.repository.ProviderAuthRepository;
import com.example.Quick_fix.repository.ProviderRepository;
import com.example.Quick_fix.repository.ProviderServiceHistoryRepository;
import com.example.Quick_fix.repository.ServiceRepository;
import com.example.Quick_fix.requestModel.BookingRequestModel;
import com.example.Quick_fix.requestModel.CustomerAuthRequestModel;
import com.example.Quick_fix.requestModel.ProviderRegisterRequestModel;
import com.example.Quick_fix.service.BookingService;
import com.example.Quick_fix.service.CustomerService;
import com.example.Quick_fix.service.ProviderService;

@ExtendWith(MockitoExtension.class)
class QuickFixBehaviorTests {

    @Mock private BookingRepository bookingRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private ServiceRepository serviceRepository;
    @Mock private CustomerAddressRepository customerAddressRepository;
    @Mock private ProviderRepository providerRepository;
    @Mock private ProviderAddressRepository providerAddressRepository;
    @Mock private ProviderServiceHistoryRepository providerServiceHistoryRepository;
    @Mock private BookingAddressRepository bookingAddressRepository;
    @Mock private CustomerAuthRepository customerAuthRepository;
    @Mock private ProviderAuthRepository providerAuthRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private Common common;

    @InjectMocks private BookingService bookingService;
    @InjectMocks private CustomerService customerService;
    @InjectMocks private ProviderService providerService;

    @Test
    void createBooking_shouldRejectAddressNotOwnedByCustomer() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(10L);
        customer.setUniqueId("CUST-1");
        customer.setFirstName("Ajay");
        customer.setLastName("R");

        ServiceEntity service = new ServiceEntity();
        service.setServiceType(ServiceType.PLUMBING);
        service.setServiceCharge(300.0);
        service.setUniqueId("SER-1");

        CustomerAddressEntity otherCustomerAddress = new CustomerAddressEntity();
        otherCustomerAddress.setId(99L);
        otherCustomerAddress.setCustomer(new CustomerEntity());
        otherCustomerAddress.getCustomer().setId(77L);
        otherCustomerAddress.setUniqueId("ADDR-999");
        otherCustomerAddress.setAddressLine1("Other place");
        otherCustomerAddress.setCity("Bengaluru");
        otherCustomerAddress.setState("KA");
        otherCustomerAddress.setCountry("India");
        otherCustomerAddress.setPostalCode("560001");

        BookingRequestModel request = new BookingRequestModel();
        request.setServiceUniqueId("SER-1");
        request.setAddressUniqueId("ADDR-999");
        request.setRecipientName("Test");
        request.setRecipientPhone("9999999999");
        request.setBookingDate(LocalDate.now());
        request.setBookingTime(java.time.LocalTime.of(10, 30));

        when(customerRepository.findByUniqueId("CUST-1")).thenReturn(Optional.of(customer));
        when(serviceRepository.findByUniqueId("SER-1")).thenReturn(Optional.of(service));
        when(customerAddressRepository.findByUniqueId("ADDR-999")).thenReturn(Optional.of(otherCustomerAddress));

        assertThrows(RuntimeException.class, () -> bookingService.createBooking("CUST-1", request));
    }

    @Test
    void providerRegisterAndLogin_shouldHashPasswordAndAuthenticate() {
        ProviderEntity provider = new ProviderEntity();
        provider.setId(1);
        provider.setUniqueId("PRO-1");
        provider.setName("Test Provider");
        provider.setEmail("provider@test.com");

        ProviderAuthEntity auth = new ProviderAuthEntity();
        auth.setId(10L);
        auth.setProvider(provider);
        auth.setEmail("provider@test.com");
        auth.setPassword("hashed-secret");

        com.example.Quick_fix.requestModel.ProviderAuthRequestModel loginRequest = new com.example.Quick_fix.requestModel.ProviderAuthRequestModel();
        loginRequest.setEmail("provider@test.com");
        loginRequest.setPassword("secret123");

        when(providerAuthRepository.findByEmail("provider@test.com")).thenReturn(Optional.of(auth));
        when(passwordEncoder.matches("secret123", "hashed-secret")).thenReturn(true);

        ProviderAuthResponseModel response = providerService.loginProvider(loginRequest);
        assertEquals("PRO-1", response.getProviderUniqueId());
        assertEquals("provider@test.com", response.getEmail());
    }
}
