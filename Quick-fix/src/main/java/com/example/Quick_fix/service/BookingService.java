package com.example.Quick_fix.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.example.Quick_fix.Entity.CustomerAddressEntity;
import com.example.Quick_fix.Entity.CustomerEntity;
import com.example.Quick_fix.Entity.ProviderAddressEntity;
import com.example.Quick_fix.Entity.ProviderEntity;
import com.example.Quick_fix.Entity.ProviderServiceHistoryEntity;
import com.example.Quick_fix.Entity.ServiceBookingEntity;
import com.example.Quick_fix.Entity.ServiceEntity;
import com.example.Quick_fix.Enums.BookingStatus;
import com.example.Quick_fix.Enums.PaymentStatus;
import com.example.Quick_fix.ResponseModel.BookingResponseModel;
import com.example.Quick_fix.ResponseModel.ProviderSuggestionResponseModel;
import com.example.Quick_fix.repository.BookingRepository;
import com.example.Quick_fix.repository.CustomerAddressRepository;
import com.example.Quick_fix.repository.CustomerRepository;
import com.example.Quick_fix.repository.ProviderAddressRepository;
import com.example.Quick_fix.repository.ProviderRepository;
import com.example.Quick_fix.repository.ProviderServiceHistoryRepository;
import com.example.Quick_fix.repository.ServiceRepository;
import com.example.Quick_fix.requestModel.BookingRequestModel;
import com.example.Quick_fix.requestModel.ProviderSuggestionRequestModel;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BookingService {

	private final BookingRepository bookingRepository;
	private final CustomerRepository customerRepository;
	private final ServiceRepository serviceRepository;
	private final CustomerAddressRepository customerAddressRepository;
	private final ProviderRepository providerRepository;
	private final ProviderAddressRepository providerAddressRepository;
	private final ProviderServiceHistoryRepository providerServiceHistoryRepository;

	// ---------------------------------------------------------
	// CREATE BOOKING REQUEST
	// ---------------------------------------------------------

	@Transactional
	public BookingResponseModel createBooking(String customerUniqueId, BookingRequestModel request) {
		CustomerEntity customer = customerRepository.findByUniqueId(customerUniqueId)
				.orElseThrow(() -> new RuntimeException("Customer not found"));

		ServiceEntity service = serviceRepository.findByUniqueId(request.getServiceUniqueId())
				.orElseThrow(() -> new RuntimeException("Service not found"));

		CustomerAddressEntity address = customerAddressRepository.findByUniqueId(request.getAddressUniqueId())
				.orElseThrow(() -> new RuntimeException("Address not found"));

		ServiceBookingEntity booking = new ServiceBookingEntity();

		booking.setUniqueId(generateUniqueId());

		booking.setCustomer(customer);
		booking.setService(service);
		booking.setAddress(address);

		booking.setBookingDate(request.getBookingDate());
		booking.setBookingTime(request.getBookingTime());

		booking.setStatus(BookingStatus.PENDING);
		booking.setPaymentStatus(PaymentStatus.PENDING);

		booking.setServicePrice(service.getServiceCharge());
		booking.setDistance(0.0);
		booking.setDistanceCharge(0.0);
		booking.setTotalAmount(service.getServiceCharge());

		booking = bookingRepository.save(booking);

		return mapBookingResponse(booking);
	}

	// ---------------------------------------------------------
	// FIND NEARBY PROVIDERS
	// ---------------------------------------------------------

	public List<ProviderSuggestionResponseModel> findProviders(String bookingUniqueId) {

		ServiceBookingEntity booking = getBooking(bookingUniqueId);

		ServiceEntity service = booking.getService();

		List<ProviderServiceHistoryEntity> providers = providerServiceHistoryRepository
				.findByServiceAndStatus(service.getServiceType(), "ACTIVE");

		List<ProviderSuggestionResponseModel> result = new ArrayList<>();

		Double customerLatitude = booking.getAddress().getLatitude();

		Double customerLongitude = booking.getAddress().getLongitude();

		for (ProviderServiceHistoryEntity history : providers) {

			ProviderEntity provider = history.getProvider();

			ProviderAddressEntity providerAddress = providerAddressRepository
					.findByProvider_UniqueIdAndPrimary(provider.getUniqueId(), true).orElseThrow();

			if (providerAddress == null || providerAddress.getLatitude() == null
					|| providerAddress.getLongitude() == null) {
				continue;
			}

			double distance = calculateDistance(customerLatitude, customerLongitude, providerAddress.getLatitude(),
					providerAddress.getLongitude());

			double distanceCharge = calculateDistanceCharge(distance);

			double totalAmount = service.getServiceCharge() + distanceCharge;

			ProviderSuggestionResponseModel response = new ProviderSuggestionResponseModel();

			response.setProviderUniqueId(provider.getUniqueId());
			response.setProviderName(provider.getName());
			response.setDistance(distance);
			response.setServicePrice(service.getServiceCharge());
			response.setDistanceCharge(distanceCharge);
			response.setTotalAmount(totalAmount);
			response.setAvailable(true);

			result.add(response);
		}

		return result;
	}

	// ---------------------------------------------------------
	// SELECT PROVIDER
	// ---------------------------------------------------------

	@Transactional
	public BookingResponseModel selectProvider(String bookingUniqueId, ProviderSuggestionRequestModel request) {

		ServiceBookingEntity booking = getBooking(bookingUniqueId);

		ProviderEntity provider = providerRepository.findByUniqueId(request.getProviderUniqueId())
				.orElseThrow(() -> new RuntimeException("Provider not found"));

		ProviderAddressEntity providerAddress = providerAddressRepository
				.findByProvider_UniqueIdAndPrimary(provider.getUniqueId(), true)
				.orElseThrow(() -> new RuntimeException("Provider address not found"));

		double distance = calculateDistance(booking.getAddress().getLatitude(), booking.getAddress().getLongitude(),
				providerAddress.getLatitude(), providerAddress.getLongitude());

		double distanceCharge = calculateDistanceCharge(distance);

		double servicePrice = booking.getService().getServiceCharge();

		double totalAmount = servicePrice + distanceCharge;

		booking.setProvider(provider);
		booking.setDistance(distance);
		booking.setServicePrice(servicePrice);
		booking.setDistanceCharge(distanceCharge);
		booking.setTotalAmount(totalAmount);

		booking.setStatus(BookingStatus.WAITING_FOR_PROVIDER);

		booking = bookingRepository.save(booking);

		return mapBookingResponse(booking);
	}

	// ---------------------------------------------------------
	// PROVIDER ACCEPT
	// ---------------------------------------------------------

	@Transactional
	public BookingResponseModel acceptBooking(String bookingUniqueId) {

		ServiceBookingEntity booking = getBooking(bookingUniqueId);

		if (booking.getProvider() == null) {
			throw new RuntimeException("No provider selected for this booking");
		}

		booking.setStatus(BookingStatus.PROVIDER_ACCEPTED);

		booking.setStatus(BookingStatus.CONFIRMED);

		booking = bookingRepository.save(booking);

		return mapBookingResponse(booking);
	}

	// ---------------------------------------------------------
	// PROVIDER REJECT
	// ---------------------------------------------------------

	@Transactional
	public BookingResponseModel rejectBooking(String bookingUniqueId) {

		ServiceBookingEntity booking = getBooking(bookingUniqueId);

		booking.setStatus(BookingStatus.PROVIDER_REJECTED);

		booking = bookingRepository.save(booking);

		return mapBookingResponse(booking);
	}

	// ---------------------------------------------------------
	// CUSTOMER CANCEL
	// ---------------------------------------------------------

	@Transactional
	public BookingResponseModel cancelBooking(String bookingUniqueId) {

		ServiceBookingEntity booking = getBooking(bookingUniqueId);

		if (booking.getStatus() != BookingStatus.CONFIRMED) {
			throw new RuntimeException("Booking cannot be cancelled at this stage");
		}

		booking.setStatus(BookingStatus.CUSTOMER_CANCELLED);

		booking = bookingRepository.save(booking);

		return mapBookingResponse(booking);
	}

	// ---------------------------------------------------------
	// GET BOOKING
	// ---------------------------------------------------------

	public BookingResponseModel getBookingResponse(String bookingUniqueId) {

		return mapBookingResponse(getBooking(bookingUniqueId));
	}

	// ---------------------------------------------------------
	// DELETE BOOKING
	// ---------------------------------------------------------

	@Transactional
	public void deleteBooking(String bookingUniqueId) {

		ServiceBookingEntity booking = getBooking(bookingUniqueId);

		bookingRepository.delete(booking);
	}

	// ---------------------------------------------------------
	// GET ENTITY BY UNIQUE ID
	// ---------------------------------------------------------

	private ServiceBookingEntity getBooking(String bookingUniqueId) {

		return bookingRepository.findByUniqueId(bookingUniqueId)
				.orElseThrow(() -> new RuntimeException("Booking not found"));
	}

	// ---------------------------------------------------------
	// GENERATE 6 DIGIT UNIQUE ID
	// ---------------------------------------------------------

	private String generateUniqueId() {

		String uniqueId;

		do {
			uniqueId = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

		} while (bookingRepository.existsByUniqueId(uniqueId));

		return uniqueId;
	}

	// ---------------------------------------------------------
	// DISTANCE CALCULATION
	// ---------------------------------------------------------

	private double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {

		final int EARTH_RADIUS = 6371;

		double latDistance = Math.toRadians(latitude2 - latitude1);

		double lonDistance = Math.toRadians(longitude2 - longitude1);

		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(latitude1))
				* Math.cos(Math.toRadians(latitude2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return Math.round(EARTH_RADIUS * c * 100.0) / 100.0;
	}

	// ---------------------------------------------------------
	// DISTANCE PRICE
	// ---------------------------------------------------------

	private double calculateDistanceCharge(double distance) {

		/*
		 * Example pricing:
		 *
		 * 0 - 2 km -> ₹30 2 - 5 km -> ₹50 5 - 10 km -> ₹100 Above 10 km -> ₹150
		 *
		 * Change these values according to Quick-fix pricing rules.
		 */

		if (distance <= 2) {
			return 30.0;
		}

		if (distance <= 5) {
			return 50.0;
		}

		if (distance <= 10) {
			return 100.0;
		}

		return 150.0;
	}

	// ---------------------------------------------------------
	// RESPONSE MAPPING
	// ---------------------------------------------------------

	private BookingResponseModel mapBookingResponse(ServiceBookingEntity booking) {

		BookingResponseModel response = new BookingResponseModel();

		response.setUniqueId(booking.getUniqueId());

		response.setCustomerUniqueId(booking.getCustomer().getUniqueId());

		response.setServiceUniqueId(booking.getService().getUniqueId());

		if (booking.getProvider() != null) {
			response.setProviderUniqueId(booking.getProvider().getUniqueId());
		}

		response.setAddressUniqueId(booking.getAddress().getUniqueId());

		response.setRecipientName(booking.getRecipientName());

		response.setRecipientPhone(booking.getRecipientPhone());

		response.setBookingDate(booking.getBookingDate());

		response.setBookingTime(booking.getBookingTime());

		response.setDistance(booking.getDistance());

		response.setServicePrice(booking.getServicePrice());

		response.setDistanceCharge(booking.getDistanceCharge());

		response.setTotalAmount(booking.getTotalAmount());

		response.setStatus(booking.getStatus());

		response.setPaymentStatus(booking.getPaymentStatus());

		return response;
	}
}