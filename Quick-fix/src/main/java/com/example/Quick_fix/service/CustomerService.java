package com.example.Quick_fix.service;

import java.util.List;

import org.springframework.stereotype.Service;

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
import com.example.Quick_fix.requestModel.CustomerRequestModel;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class CustomerService  {

	private final CustomerRepository customerRepository;

	private final CustomerAuthRepository customerAuthRepository;

	private final CustomerAddressRepository customerAddressRepository;

	private final CustomerContactRepository customerContactRepository;

	public CustomerResponseModel createCustomer(CustomerRequestModel request) {

		// create customer
		return null;
	}

	public CustomerResponseModel getCustomer(Long customerId) {

		// find customer
		return null;
	}

	public CustomerResponseModel updateCustomer(Long customerId, CustomerRequestModel request) {

		// update customer
		return null;
	}


	public void deleteCustomer(Long customerId) {

		// deactivate/delete customer
	}

	public CustomerAuthResponseModel register(CustomerAuthRequestModel request) {

		// registration logic
		return null;
	}

	public CustomerAuthResponseModel login(CustomerAuthRequestModel request) {

		// login logic
		return null;
	}

	public CustomerAddressResponseModel addAddress(Long customerId, CustomerAddressRequestModel request) {

		// add address
		return null;
	}

	public List<CustomerAddressResponseModel> getAddresses(Long customerId) {

		// get addresses
		return null;
	}

	public CustomerAddressResponseModel updateAddress(Long customerId, Long addressId, CustomerAddressRequestModel request) {

		// update address
		return null;
	}

	public void deleteAddress(Long customerId, Long addressId) {

		// delete address
	}

	public CustomerContactResponseModel addContact(Long customerId, CustomerContactRequestModel request) {

		// add contact
		return null;
	}

	public List<CustomerContactResponseModel> getContacts(Long customerId) {

		// get contacts
		return null;
	}

	public CustomerContactResponseModel updateContact(Long customerId, Long contactId, CustomerContactRequestModel request) {

		// update contact
		return null;
	}

	public void deleteContact(Long customerId, Long contactId) {

		// delete contact
	}
}
