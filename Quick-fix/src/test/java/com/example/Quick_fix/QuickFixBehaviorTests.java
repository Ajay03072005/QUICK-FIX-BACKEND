package com.example.Quick_fix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.Quick_fix.Entity.AdminAuditLogEntity;
import com.example.Quick_fix.Entity.BookingAddressEntity;
import com.example.Quick_fix.Entity.CustomerAddressEntity;
import com.example.Quick_fix.Entity.CustomerEntity;
import com.example.Quick_fix.Entity.LedgerEntryEntity;
import com.example.Quick_fix.Entity.PayoutEntity;
import com.example.Quick_fix.Entity.ProviderAuthEntity;
import com.example.Quick_fix.Entity.ProviderEarningEntity;
import com.example.Quick_fix.Entity.ProviderEntity;
import com.example.Quick_fix.Entity.ReviewEntity;
import com.example.Quick_fix.Entity.ServiceBookingEntity;
import com.example.Quick_fix.Entity.ServiceEntity;
import com.example.Quick_fix.Entity.SupportTicketEntity;
import com.example.Quick_fix.Enums.BookingStatus;
import com.example.Quick_fix.Enums.ServiceType;
import com.example.Quick_fix.ResponseModel.BookingResponseModel;
import com.example.Quick_fix.ResponseModel.ProviderAuthResponseModel;
import com.example.Quick_fix.ResponseModel.ReviewResponseModel;
import com.example.Quick_fix.commons.Common;
import com.example.Quick_fix.config.JwtTokenProvider;
import com.example.Quick_fix.repository.AdminAuditLogRepository;
import com.example.Quick_fix.repository.BookingAddressRepository;
import com.example.Quick_fix.repository.BookingRepository;
import com.example.Quick_fix.repository.CommissionRuleRepository;
import com.example.Quick_fix.repository.CustomerAddressRepository;
import com.example.Quick_fix.repository.CustomerAuthRepository;
import com.example.Quick_fix.repository.CustomerRepository;
import com.example.Quick_fix.repository.LedgerEntryRepository;
import com.example.Quick_fix.repository.PayoutRepository;
import com.example.Quick_fix.repository.ProviderAddressRepository;
import com.example.Quick_fix.repository.ProviderAuthRepository;
import com.example.Quick_fix.repository.ProviderEarningRepository;
import com.example.Quick_fix.repository.ProviderRepository;
import com.example.Quick_fix.repository.ProviderServiceHistoryRepository;
import com.example.Quick_fix.repository.ReviewRepository;
import com.example.Quick_fix.repository.ServiceRepository;
import com.example.Quick_fix.repository.SupportTicketMessageRepository;
import com.example.Quick_fix.repository.SupportTicketRepository;
import com.example.Quick_fix.requestModel.BookingRequestModel;
import com.example.Quick_fix.requestModel.ProviderAuthRequestModel;
import com.example.Quick_fix.requestModel.ReviewRequestModel;
import com.example.Quick_fix.service.AdminService;
import com.example.Quick_fix.service.BookingService;
import com.example.Quick_fix.service.CustomerService;
import com.example.Quick_fix.service.FinanceService;
import com.example.Quick_fix.service.ProviderService;
import com.example.Quick_fix.service.ReviewService;
import com.example.Quick_fix.service.SupportService;

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
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private Common common;
    @Mock private FinanceService financeService;
    @Mock private CommissionRuleRepository commissionRuleRepository;
    @Mock private LedgerEntryRepository ledgerEntryRepository;
    @Mock private ProviderEarningRepository providerEarningRepository;
    @Mock private PayoutRepository payoutRepository;
    @Mock private SupportTicketRepository supportTicketRepository;
    @Mock private SupportTicketMessageRepository supportTicketMessageRepository;
    @Mock private AdminAuditLogRepository adminAuditLogRepository;
    @Mock private ReviewRepository reviewRepository;

    @InjectMocks private BookingService bookingService;
    @InjectMocks private CustomerService customerService;
    @InjectMocks private ProviderService providerService;
    @InjectMocks private FinanceService financeServiceInjectable;
    @InjectMocks private SupportService supportService;
    @InjectMocks private AdminService adminService;
    @InjectMocks private ReviewService reviewService;

    private CustomerEntity createDummyCustomer() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(10L);
        customer.setUniqueId("CUST-10");
        customer.setFirstName("Ajay");
        return customer;
    }

    private ProviderEntity createDummyProvider() {
        ProviderEntity provider = new ProviderEntity();
        provider.setId(1);
        provider.setUniqueId("PRO-1");
        provider.setName("Test Provider");
        provider.setEmail("provider@test.com");
        return provider;
    }

    private ServiceEntity createDummyService() {
        ServiceEntity service = new ServiceEntity();
        service.setId(100);
        service.setUniqueId("SER-100");
        service.setServiceType(ServiceType.PLUMBING);
        service.setServiceCharge(200.0);
        return service;
    }

    private BookingAddressEntity createDummyBookingAddress() {
        BookingAddressEntity address = new BookingAddressEntity();
        address.setAddressLine1("123 Main St");
        address.setCity("Bengaluru");
        address.setState("KA");
        address.setPostalCode("560001");
        return address;
    }

    // --- TEST CASE 1: Customer Address Ownership Validation ---
    @Test
    void createBooking_shouldRejectAddressNotOwnedByCustomer() {
        CustomerEntity customer = createDummyCustomer();

        ServiceEntity service = createDummyService();

        CustomerAddressEntity otherCustomerAddress = new CustomerAddressEntity();
        otherCustomerAddress.setId(99L);
        otherCustomerAddress.setCustomer(new CustomerEntity());
        otherCustomerAddress.getCustomer().setId(77L);
        otherCustomerAddress.setUniqueId("ADDR-999");

        BookingRequestModel request = new BookingRequestModel();
        request.setServiceUniqueId("SER-100");
        request.setAddressUniqueId("ADDR-999");
        request.setBookingDate(LocalDate.now());
        request.setBookingTime(LocalTime.of(10, 30));

        when(customerRepository.findByUniqueId("CUST-10")).thenReturn(Optional.of(customer));
        when(serviceRepository.findByUniqueId("SER-100")).thenReturn(Optional.of(service));
        when(customerAddressRepository.findByUniqueId("ADDR-999")).thenReturn(Optional.of(otherCustomerAddress));

        assertThrows(RuntimeException.class, () -> bookingService.createBooking("CUST-10", request));
    }

    // --- TEST CASE 2: Provider Auth & JWT Generation ---
    @Test
    void providerRegisterAndLogin_shouldHashPasswordAndAuthenticate() {
        ProviderEntity provider = createDummyProvider();

        ProviderAuthEntity auth = new ProviderAuthEntity();
        auth.setId(1L);
        auth.setProvider(provider);
        auth.setEmail("provider@test.com");
        auth.setPassword("hashed-secret");

        ProviderAuthRequestModel loginRequest = new ProviderAuthRequestModel();
        loginRequest.setEmail("provider@test.com");
        loginRequest.setPassword("secret123");

        when(providerAuthRepository.findByEmail("provider@test.com")).thenReturn(Optional.of(auth));
        when(passwordEncoder.matches("secret123", "hashed-secret")).thenReturn(true);
        when(jwtTokenProvider.generateToken("provider@test.com", "PROVIDER", "PRO-1", "1")).thenReturn("mock-jwt-token");

        ProviderAuthResponseModel response = providerService.loginProvider(loginRequest);
        assertEquals("PRO-1", response.getProviderUniqueId());
        assertEquals("provider@test.com", response.getEmail());
        assertEquals("mock-jwt-token", response.getToken());
    }

    // --- TEST CASE 3: Job Execution Lifecycle & En-Route Transition ---
    @Test
    void markEnRoute_shouldUpdateStatusToProviderEnRoute() {
        ServiceBookingEntity booking = new ServiceBookingEntity();
        booking.setId(100L);
        booking.setUniqueId("BOOK-100");
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCustomer(createDummyCustomer());
        booking.setProvider(createDummyProvider());
        booking.setService(createDummyService());
        booking.setBookingAddress(createDummyBookingAddress());

        when(bookingRepository.findByUniqueId("BOOK-100")).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(ServiceBookingEntity.class))).thenAnswer(i -> i.getArgument(0));

        BookingResponseModel updated = bookingService.markEnRoute("BOOK-100");
        assertEquals(BookingStatus.PROVIDER_EN_ROUTE, updated.getStatus());
    }

    // --- TEST CASE 4: Complete Job & Financial Commission Calculation ---
    @Test
    void completeBooking_shouldCalculatePlatformCommissionAndPostLedger() {
        ServiceBookingEntity booking = new ServiceBookingEntity();
        booking.setId(200L);
        booking.setUniqueId("BOOK-200");
        booking.setTotalAmount(200.0);
        booking.setStatus(BookingStatus.IN_PROGRESS);
        booking.setCustomer(createDummyCustomer());
        booking.setProvider(createDummyProvider());
        booking.setService(createDummyService());
        booking.setBookingAddress(createDummyBookingAddress());

        when(bookingRepository.findByUniqueId("BOOK-200")).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(ServiceBookingEntity.class))).thenAnswer(i -> i.getArgument(0));

        BookingResponseModel completed = bookingService.completeBooking("BOOK-200");
        assertEquals(BookingStatus.COMPLETED, completed.getStatus());
        verify(financeService).calculateAndRecordEarning(booking);
    }

    // --- TEST CASE 5: Finance Engine 15% Platform Commission & Double-Entry Ledger ---
    @Test
    void calculateAndRecordEarning_shouldSplit15PercentCommissionAndRecordEntries() {
        ServiceBookingEntity booking = new ServiceBookingEntity();
        booking.setId(300L);
        booking.setUniqueId("BOOK-300");
        booking.setTotalAmount(100.0);
        booking.setCustomer(createDummyCustomer());
        booking.setProvider(createDummyProvider());
        booking.setService(createDummyService());
        booking.setBookingAddress(createDummyBookingAddress());

        when(providerEarningRepository.save(any(ProviderEarningEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(commissionRuleRepository.findFirstByActiveTrueOrderByIdDesc()).thenReturn(Optional.empty());

        ProviderEarningEntity earning = financeServiceInjectable.calculateAndRecordEarning(booking);
        assertNotNull(earning);
        assertEquals(100.0, earning.getGrossAmount());
        assertEquals(15.0, earning.getCommissionAmount()); // Default 15% of 100
        assertEquals(85.0, earning.getNetAmount());

        // Verify 3 double-entry ledger records (Customer Payment, Platform Revenue & Provider Payable)
        verify(ledgerEntryRepository, org.mockito.Mockito.times(3)).save(any(LedgerEntryEntity.class));
    }

    // --- TEST CASE 6: Provider Payout Creation ---
    @Test
    void createPayout_shouldDeductEarningsAndCreatePayoutRecord() {
        ProviderEntity provider = createDummyProvider();

        when(providerRepository.findByUniqueId("PRO-1")).thenReturn(Optional.of(provider));
        when(payoutRepository.save(any(PayoutEntity.class))).thenAnswer(i -> i.getArgument(0));

        PayoutEntity payout = financeServiceInjectable.createPayout("PRO-1", 150.0);
        assertNotNull(payout);
        assertEquals("PRO-1", payout.getProvider().getUniqueId());
        assertEquals(150.0, payout.getAmount());
        assertEquals("PAID", payout.getStatus());
    }

    // --- TEST CASE 7: Support Ticket Lifecycle ---
    @Test
    void createTicket_shouldTrackSupportThread() {
        CustomerEntity customer = createDummyCustomer();

        when(customerRepository.findByUniqueId("CUST-10")).thenReturn(Optional.of(customer));
        when(common.generateUniqueId()).thenReturn("TICK-101");
        when(supportTicketRepository.save(any(SupportTicketEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(supportTicketRepository.findByUniqueId("TICK-101")).thenAnswer(i -> {
            SupportTicketEntity t = new SupportTicketEntity();
            t.setUniqueId("TICK-101");
            t.setSubject("Issue with billing");
            t.setStatus("OPEN");
            return Optional.of(t);
        });

        SupportTicketEntity created = supportService.createTicket("Issue with billing", "PAYMENT", "HIGH", "CUST-10", null, null, "Overcharged for service");
        assertNotNull(created);
        assertEquals("TICK-101", created.getUniqueId());
        assertEquals("OPEN", created.getStatus());
        assertEquals("Issue with billing", created.getSubject());
    }

    // --- TEST CASE 8: Admin Audit Logging ---
    @Test
    void logAction_shouldPersistAdminAuditTrail() {
        when(adminAuditLogRepository.save(any(AdminAuditLogEntity.class))).thenAnswer(i -> i.getArgument(0));

        AdminAuditLogEntity log = adminService.logAction("ADMIN_1", "APPROVE_PROVIDER", "PROVIDER", "PRO-1", "Verification passed");
        assertNotNull(log);
        assertEquals("ADMIN_1", log.getActor());
        assertEquals("APPROVE_PROVIDER", log.getAction());
        assertEquals("Verification passed", log.getReason());
    }

    // --- TEST CASE 9: Service Reviews ---
    @Test
    void createReview_shouldSaveReviewForCompletedBooking() {
        CustomerEntity customer = createDummyCustomer();
        ProviderEntity provider = createDummyProvider();

        ServiceBookingEntity booking = new ServiceBookingEntity();
        booking.setId(500L);
        booking.setUniqueId("BOOK-500");
        booking.setStatus(BookingStatus.COMPLETED);
        booking.setCustomer(customer);
        booking.setProvider(provider);

        ReviewRequestModel request = new ReviewRequestModel();
        request.setBookingUniqueId("BOOK-500");
        request.setRating(5);
        request.setComment("Excellent plumbing work!");

        when(customerRepository.findByUniqueId("CUST-10")).thenReturn(Optional.of(customer));
        when(bookingRepository.findByUniqueId("BOOK-500")).thenReturn(Optional.of(booking));
        when(reviewRepository.existsByBooking_UniqueId("BOOK-500")).thenReturn(false);
        when(common.generateUniqueId()).thenReturn("REV-100");
        when(reviewRepository.save(any(ReviewEntity.class))).thenAnswer(i -> i.getArgument(0));

        ReviewResponseModel res = reviewService.createReview("CUST-10", request);
        assertNotNull(res);
        assertEquals("REV-100", res.getUniqueId());
        assertEquals(5, res.getRating());
        assertEquals("Excellent plumbing work!", res.getComment());
    }
}
