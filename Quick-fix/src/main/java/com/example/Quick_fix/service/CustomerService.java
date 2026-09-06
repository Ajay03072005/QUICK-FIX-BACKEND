package com.example.Quick_fix.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Quick_fix.Entity.CustomerAddressEntity;
import com.example.Quick_fix.Entity.CustomerAuthEntity;
import com.example.Quick_fix.Entity.CustomerContactEntity;
import com.example.Quick_fix.Entity.CustomerEntity;
import com.example.Quick_fix.Enums.CustomerStatus;
import com.example.Quick_fix.ResponseModel.CustomerAddressResponseModel;
import com.example.Quick_fix.ResponseModel.CustomerAuthResponseModel;
import com.example.Quick_fix.ResponseModel.CustomerContactResponseModel;
import com.example.Quick_fix.ResponseModel.CustomerResponseModel;
import com.example.Quick_fix.repository.CustomerAddressRepository;
import com.example.Quick_fix.repository.CustomerAuthRepository;
import com.example.Quick_fix.repository.CustomerContactRepository;
import com.example.Quick_fix.repository.CustomerRepository;
import com.example.Quick_fix.requestModel.CustomerAddressRequestModel;
import com.example.Quick_fix.requestModel.CustomerAuthRequestModel;
import com.example.Quick_fix.requestModel.CustomerContactRequestModel;
import com.example.Quick_fix.requestModel.CustomerRegisterRequestModel;
import com.example.Quick_fix.requestModel.CustomerRequestModel;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class CustomerService {

	private final CustomerRepository customerRepository;
	private final CustomerAuthRepository customerAuthRepository;
	private final CustomerAddressRepository customerAddressRepository;
	private final CustomerContactRepository customerContactRepository;
	private final PasswordEncoder passwordEncoder;

	// =========================================================
	// CUSTOMER
	// =========================================================

	public CustomerResponseModel createCustomer(CustomerRequestModel request) {

		CustomerEntity customer = new CustomerEntity();

		customer.setFirstName(request.getFirstName());
		customer.setLastName(request.getLastName());
		customer.setPhoneNumber(request.getPhoneNumber());
		customer.setProfileImage(request.getProfileImage());
		customer.setGender(request.getGender());
		customer.setDateOfBirth(request.getDateOfBirth());
		customer.setStatus(CustomerStatus.ACTIVE);
		customer = customerRepository.save(customer);
		return mapCustomerResponse(customer);
	}

	public CustomerResponseModel getCustomer(Long customerId) {

		CustomerEntity customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new RuntimeException("Customer not found"));

		return mapCustomerResponse(customer);
	}

	public CustomerResponseModel updateCustomer(Long customerId, CustomerRequestModel request) {

		CustomerEntity customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new RuntimeException("Customer not found"));

		customer.setFirstName(request.getFirstName());
		customer.setLastName(request.getLastName());
		customer.setPhoneNumber(request.getPhoneNumber());
		customer.setProfileImage(request.getProfileImage());
		customer.setGender(request.getGender());
		customer.setStatus(request.getStatus());
		customer.setDateOfBirth(request.getDateOfBirth());

		customer = customerRepository.save(customer);

		return mapCustomerResponse(customer);
	}

	public void deleteCustomer(Long customerId) {

		CustomerEntity customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new RuntimeException("Customer not found"));
		// If using CustomerStatus for soft delete
		// customer.setStatus(CustomerStatus.INACTIVE);
		customerRepository.delete(customer);
	}

	// =========================================================
	// AUTH
	// =========================================================

	public String register(CustomerRegisterRequestModel request) {

		if (customerAuthRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email already registered");
		}
		CustomerEntity customer = new CustomerEntity();
		customer.setStatus(CustomerStatus.ACTIVE);
		customer.setFirstName(request.getFirstName());
		customer.setDateOfBirth(request.getDateOfBirth());
		customer.setGender(request.getGender());
		customer.setLastName(request.getLastName());
		customer = customerRepository.save(customer);
		CustomerAuthEntity auth = new CustomerAuthEntity();
		auth.setCustomer(customer);
		auth.setEmail(request.getEmail());
		auth.setPassword(passwordEncoder.encode(request.getPassword()));
		auth.setEmailVerified(false);

		auth = customerAuthRepository.save(auth);

		return "Registered SuccessFully please login";
	}

	public CustomerAuthResponseModel login(CustomerAuthRequestModel request) {
		CustomerAuthEntity auth = customerAuthRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("Invalid email or password"));
		if (!passwordEncoder.matches(request.getPassword(), auth.getPassword())) {
			throw new RuntimeException("Invalid email or password");
		}
		auth.setLastLoginAt(LocalDateTime.now());
		auth = customerAuthRepository.save(auth);
		return mapAuthResponse(auth);
	}

	// =========================================================
	// ADDRESS
	// =========================================================

	public CustomerAddressResponseModel addAddress(Long customerId, CustomerAddressRequestModel request) {

		CustomerEntity customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new RuntimeException("Customer not found"));

		CustomerAddressEntity address = new CustomerAddressEntity();

		address.setCustomer(customer);
		address.setAddressType(request.getAddressType());
		address.setAddressLine1(request.getAddressLine1());
		address.setAddressLine2(request.getAddressLine2());
		address.setCity(request.getCity());
		address.setState(request.getState());
		address.setCountry(request.getCountry());
		address.setPostalCode(request.getPostalCode());
		address.setLatitude(request.getLatitude());
		address.setLongitude(request.getLongitude());
		address.setDefaultAddress(request.isDefaultAddress());

		address = customerAddressRepository.save(address);

		return mapAddressResponse(address);
	}

	public List<CustomerAddressResponseModel> getAddresses(Long customerId) {

		if (!customerRepository.existsById(customerId)) {
			throw new RuntimeException("Customer not found");
		}

		return customerAddressRepository.findByCustomerId(customerId).stream().map(this::mapAddressResponse).toList();
	}

	public CustomerAddressResponseModel updateAddress(Long customerId, Long addressId,
			CustomerAddressRequestModel request) {

		CustomerAddressEntity address = customerAddressRepository.findById(addressId)
				.orElseThrow(() -> new RuntimeException("Address not found"));

		if (!address.getCustomer().getId().equals(customerId)) {
			throw new RuntimeException("Address does not belong to this customer");
		}

		address.setAddressType(request.getAddressType());
		address.setAddressLine1(request.getAddressLine1());
		address.setAddressLine2(request.getAddressLine2());
		address.setCity(request.getCity());
		address.setState(request.getState());
		address.setCountry(request.getCountry());
		address.setPostalCode(request.getPostalCode());
		address.setLatitude(request.getLatitude());
		address.setLongitude(request.getLongitude());
		address.setDefaultAddress(request.isDefaultAddress());

		address = customerAddressRepository.save(address);

		return mapAddressResponse(address);
	}

	public void deleteAddress(Long customerId, Long addressId) {

		CustomerAddressEntity address = customerAddressRepository.findById(addressId)
				.orElseThrow(() -> new RuntimeException("Address not found"));

		if (!address.getCustomer().getId().equals(customerId)) {
			throw new RuntimeException("Address does not belong to this customer");
		}

		customerAddressRepository.delete(address);
	}

	// =========================================================
	// CONTACT
	// =========================================================

	public CustomerContactResponseModel addContact(Long customerId, CustomerContactRequestModel request) {

		CustomerEntity customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new RuntimeException("Customer not found"));

		CustomerContactEntity contact = new CustomerContactEntity();

		contact.setCustomer(customer);
		contact.setContactType(request.getContactType());
		contact.setContactValue(request.getContactValue());
		contact.setPrimaryContact(request.isPrimaryContact());
		contact.setVerified(request.isVerified());

		contact = customerContactRepository.save(contact);

		return mapContactResponse(contact);
	}

	public List<CustomerContactResponseModel> getContacts(Long customerId) {

		if (!customerRepository.existsById(customerId)) {
			throw new RuntimeException("Customer not found");
		}

		return customerContactRepository.findByCustomerId(customerId).stream().map(this::mapContactResponse).toList();
	}

	public CustomerContactResponseModel updateContact(Long customerId, Long contactId,
			CustomerContactRequestModel request) {

		CustomerContactEntity contact = customerContactRepository.findById(contactId)
				.orElseThrow(() -> new RuntimeException("Contact not found"));

		if (!contact.getCustomer().getId().equals(customerId)) {
			throw new RuntimeException("Contact does not belong to this customer");
		}

		contact.setContactType(request.getContactType());
		contact.setContactValue(request.getContactValue());
		contact.setPrimaryContact(request.isPrimaryContact());
		contact.setVerified(request.isVerified());

		contact = customerContactRepository.save(contact);

		return mapContactResponse(contact);
	}

	public void deleteContact(Long customerId, Long contactId) {

		CustomerContactEntity contact = customerContactRepository.findById(contactId)
				.orElseThrow(() -> new RuntimeException("Contact not found"));

		if (!contact.getCustomer().getId().equals(customerId)) {
			throw new RuntimeException("Contact does not belong to this customer");
		}

		customerContactRepository.delete(contact);
	}

	// =========================================================
	// MAPPERS
	// =========================================================

	private CustomerResponseModel mapCustomerResponse(CustomerEntity customer) {

		CustomerResponseModel response = new CustomerResponseModel();

		response.setId(customer.getId());
		response.setFirstName(customer.getFirstName());
		response.setLastName(customer.getLastName());
		response.setPhoneNumber(customer.getPhoneNumber());
		response.setProfileImage(customer.getProfileImage());
		response.setGender(customer.getGender());
		response.setStatus(customer.getStatus());
		response.setDateOfBirth(customer.getDateOfBirth());

		return response;
	}

	private CustomerAuthResponseModel mapAuthResponse(CustomerAuthEntity auth) {

		CustomerAuthResponseModel response = new CustomerAuthResponseModel();

		response.setId(auth.getId());
		response.setCustomerId(auth.getCustomer().getId());
		response.setEmail(auth.getEmail());
		response.setEmailVerified(auth.isEmailVerified());
		response.setLastLoginAt(auth.getLastLoginAt());

		return response;
	}

	private CustomerAddressResponseModel mapAddressResponse(CustomerAddressEntity address) {

		CustomerAddressResponseModel response = new CustomerAddressResponseModel();

		response.setId(address.getId());
		response.setCustomerId(address.getCustomer().getId());
		response.setAddressType(address.getAddressType());
		response.setAddressLine1(address.getAddressLine1());
		response.setAddressLine2(address.getAddressLine2());
		response.setCity(address.getCity());
		response.setState(address.getState());
		response.setCountry(address.getCountry());
		response.setPostalCode(address.getPostalCode());
		response.setLatitude(address.getLatitude());
		response.setLongitude(address.getLongitude());
		response.setDefaultAddress(address.isDefaultAddress());

		return response;
	}

	private CustomerContactResponseModel mapContactResponse(CustomerContactEntity contact) {

		CustomerContactResponseModel response = new CustomerContactResponseModel();

		response.setId(contact.getId());
		response.setCustomerId(contact.getCustomer().getId());
		response.setContactType(contact.getContactType());
		response.setContactValue(contact.getContactValue());
		response.setPrimaryContact(contact.isPrimaryContact());
		response.setVerified(contact.isVerified());

		return response;
	}
}