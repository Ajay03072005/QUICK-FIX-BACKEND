package com.example.Quick_fix.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Quick_fix.Entity.ProviderAddressEntity;
import com.example.Quick_fix.Entity.ProviderAuthEntity;
import com.example.Quick_fix.Entity.ProviderContactEntity;
import com.example.Quick_fix.Entity.ProviderDocumentEntity;
import com.example.Quick_fix.Entity.ProviderEntity;
import com.example.Quick_fix.Entity.ProviderServiceHistoryEntity;

import com.example.Quick_fix.ResponseModel.ProviderAddressReponseModel;
import com.example.Quick_fix.ResponseModel.ProviderAuthResponseModel;
import com.example.Quick_fix.ResponseModel.ProviderContactResponseModel;
import com.example.Quick_fix.ResponseModel.ProviderDocumentReponseModel;
import com.example.Quick_fix.ResponseModel.ProviderResponseModel;
import com.example.Quick_fix.ResponseModel.ProviderServiceHistoryReponseModel;

import com.example.Quick_fix.repository.ProviderAuthRepository;
import com.example.Quick_fix.repository.ProviderRepository;

import com.example.Quick_fix.requestModel.ProviderAddressRequestModel;
import com.example.Quick_fix.requestModel.ProviderAuthRequestModel;
import com.example.Quick_fix.requestModel.ProviderContactRequestModel;
import com.example.Quick_fix.requestModel.ProviderDocumentRequestModel;
import com.example.Quick_fix.requestModel.ProviderRegisterRequestModel;
import com.example.Quick_fix.requestModel.ProviderRequestModel;
import com.example.Quick_fix.requestModel.ProviderServiceHistoryRequestModel;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProviderService {

	private final ProviderRepository providerRepository;
	private final ProviderAuthRepository providerAuthRepository;
	private final PasswordEncoder passwordEncoder;
	private final com.example.Quick_fix.commons.Common commons;

	@Transactional
	public String createProvider(ProviderRequestModel request) {

		ProviderEntity providerEntity = new ProviderEntity();
		providerEntity.setName(request.getName());
		providerEntity.setPhoneNumber(request.getPhoneNumber());
		providerEntity.setEmail(request.getEmail());
		providerEntity.setUniqueId(commons.generateUniqueId());

		List<ProviderAddressEntity> addressEntities = new ArrayList<>();

		if (request.getAddresses() != null) {

			for (ProviderAddressRequestModel addressRequest : request.getAddresses()) {

				ProviderAddressEntity addressEntity = new ProviderAddressEntity();

				addressEntity.setAddressType(addressRequest.getAddressType());

				addressEntity.setAddressLine1(addressRequest.getAddressLine1());

				addressEntity.setAddressLine2(addressRequest.getAddressLine2());

				addressEntity.setCity(addressRequest.getCity());

				addressEntity.setState(addressRequest.getState());

				addressEntity.setCountry(addressRequest.getCountry());

				addressEntity.setPostalCode(addressRequest.getPostalCode());

				addressEntity.setPrimary(addressRequest.getPrimary());

				// CHILD → PARENT
				addressEntity.setProvider(providerEntity);

				addressEntities.add(addressEntity);
			}
		}

		providerEntity.setAddresses(addressEntities);
		List<ProviderContactEntity> contactEntities = new ArrayList<>();

		if (request.getContacts() != null) {

			for (ProviderContactRequestModel contactRequest : request.getContacts()) {

				ProviderContactEntity contactEntity = new ProviderContactEntity();

				contactEntity.setFirstName(contactRequest.getFirstName());

				contactEntity.setLastName(contactRequest.getLastName());

				contactEntity.setPhoneNumber(contactRequest.getPhoneNumber());

				contactEntity.setEmail(contactRequest.getEmail());

				contactEntity.setPrimary(contactRequest.getPrimary());

				// CHILD → PARENT
				contactEntity.setProvider(providerEntity);

				contactEntities.add(contactEntity);
			}
		}

		providerEntity.setContacts(contactEntities);

		List<ProviderServiceHistoryEntity> serviceEntities = new ArrayList<>();

		if (request.getProviderServices() != null) {

			for (ProviderServiceHistoryRequestModel serviceRequest : request.getProviderServices()) {

				ProviderServiceHistoryEntity serviceEntity = new ProviderServiceHistoryEntity();

				serviceEntity.setService(serviceRequest.getService());

				serviceEntity.setStartDate(serviceRequest.getStartDate());

				serviceEntity.setEndDate(serviceRequest.getEndDate());

				serviceEntity.setStatus(serviceRequest.getStatus());

				serviceEntity.setNotes(serviceRequest.getNotes());

				serviceEntity.setCreatedDate(serviceRequest.getCreatedDate());

				serviceEntity.setUpdatedDate(serviceRequest.getUpdatedDate());

				// CHILD → PARENT
				serviceEntity.setProvider(providerEntity);

				serviceEntities.add(serviceEntity);
			}
		}

		providerEntity.setProviderServices(serviceEntities);

		List<ProviderDocumentEntity> documentEntities = new ArrayList<>();

		if (request.getDocuments() != null) {

			for (ProviderDocumentRequestModel documentRequest : request.getDocuments()) {

				ProviderDocumentEntity documentEntity = new ProviderDocumentEntity();

				documentEntity.setDocumentType(documentRequest.getDocumentType());

				documentEntity.setDocumentNumber(documentRequest.getDocumentNumber());

				documentEntity.setFileName(documentRequest.getFileName());

				documentEntity.setFilePath(documentRequest.getFilePath());

				documentEntity.setFileUrl(documentRequest.getFileUrl());

				documentEntity.setIssueDate(documentRequest.getIssueDate());

				documentEntity.setExpiryDate(documentRequest.getExpiryDate());

				documentEntity.setStatus(documentRequest.getStatus());

				documentEntity.setProvider(providerEntity);

				documentEntities.add(documentEntity);
			}
		}

		providerEntity.setDocuments(documentEntities);

		ProviderEntity savedProvider = providerRepository.save(providerEntity);
		return "Created Sucessfully:";
	}

	@Transactional
	public ProviderAuthResponseModel registerProvider(ProviderRegisterRequestModel request) {
		if (providerAuthRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email already registered");
		}

		ProviderEntity provider = new ProviderEntity();
		provider.setName(request.getName());
		provider.setPhoneNumber(request.getPhoneNumber());
		provider.setEmail(request.getEmail());
		provider.setUniqueId(commons.generateUniqueId());
		provider.setAvailabilityStatus("AVAILABLE_NOW");
		provider = providerRepository.save(provider);

		ProviderAuthEntity auth = new ProviderAuthEntity();
		auth.setProvider(provider);
		auth.setEmail(request.getEmail());
		auth.setPassword(passwordEncoder.encode(request.getPassword()));
		auth.setLastLoginAt(LocalDateTime.now());
		providerAuthRepository.save(auth);

		ProviderAuthResponseModel response = mapAuthResponse(auth);
		response.setToken(java.util.UUID.randomUUID().toString());
		return response;
	}

	@Transactional
	public ProviderAuthResponseModel loginProvider(ProviderAuthRequestModel request) {
		ProviderAuthEntity auth = providerAuthRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("Invalid email or password"));
		if (!passwordEncoder.matches(request.getPassword(), auth.getPassword())) {
			throw new RuntimeException("Invalid email or password");
		}
		auth.setLastLoginAt(LocalDateTime.now());
		providerAuthRepository.save(auth);
		ProviderAuthResponseModel response = mapAuthResponse(auth);
		response.setToken(java.util.UUID.randomUUID().toString());
		return response;
	}

	@Transactional
	public ProviderResponseModel getProviderById(Integer id) {

		ProviderEntity providerEntity = providerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Provider not found with id: " + id));
		ensureProviderIdentity(providerEntity);

		return convertToResponse(providerEntity);
	}

	@Transactional
	public ProviderResponseModel getProviderByUniqueId(String uniqueId) {
		return convertToResponse(providerRepository.findByUniqueId(uniqueId)
				.orElseThrow(() -> new RuntimeException("Provider not found")));
	}

	@Transactional
	public List<ProviderResponseModel> getAllProviders() {

		List<ProviderEntity> providers = providerRepository.findAll();

		List<ProviderResponseModel> responses = new ArrayList<>();

		for (ProviderEntity provider : providers) {
			ensureProviderIdentity(provider);

			responses.add(convertToResponse(provider));
		}

		return responses;
	}

	@Transactional
	public ProviderResponseModel updateAvailability(String uniqueId, String status) {
		ProviderEntity provider = providerRepository.findByUniqueId(uniqueId)
				.orElseThrow(() -> new RuntimeException("Provider not found"));
		provider.setAvailabilityStatus(status);
		return convertToResponse(providerRepository.save(provider));
	}

	@Transactional
	public ProviderResponseModel updateAvailability(Integer id, String status) {
		ProviderEntity provider = providerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Provider not found"));
		ensureProviderIdentity(provider);
		provider.setAvailabilityStatus(status);
		return convertToResponse(providerRepository.save(provider));
	}

	@Transactional
	public ProviderResponseModel updateProvider(Integer id, ProviderRequestModel request) {

		ProviderEntity providerEntity = providerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Provider not found with id: " + id));

		providerEntity.setName(request.getName());
		providerEntity.setPhoneNumber(request.getPhoneNumber());
		providerEntity.setEmail(request.getEmail());

		List<ProviderAddressEntity> addressEntities = new ArrayList<>();

		if (request.getAddresses() != null) {

			for (ProviderAddressRequestModel addressRequest : request.getAddresses()) {

				ProviderAddressEntity addressEntity = new ProviderAddressEntity();

				addressEntity.setAddressType(addressRequest.getAddressType());

				addressEntity.setAddressLine1(addressRequest.getAddressLine1());

				addressEntity.setAddressLine2(addressRequest.getAddressLine2());

				addressEntity.setCity(addressRequest.getCity());

				addressEntity.setState(addressRequest.getState());

				addressEntity.setCountry(addressRequest.getCountry());

				addressEntity.setPostalCode(addressRequest.getPostalCode());

				addressEntity.setPrimary(addressRequest.getPrimary());

				addressEntity.setProvider(providerEntity);

				addressEntities.add(addressEntity);
			}
		}

		providerEntity.setAddresses(addressEntities);

		List<ProviderContactEntity> contactEntities = new ArrayList<>();

		if (request.getContacts() != null) {

			for (ProviderContactRequestModel contactRequest : request.getContacts()) {

				ProviderContactEntity contactEntity = new ProviderContactEntity();

				contactEntity.setFirstName(contactRequest.getFirstName());

				contactEntity.setLastName(contactRequest.getLastName());

				contactEntity.setPhoneNumber(contactRequest.getPhoneNumber());

				contactEntity.setEmail(contactRequest.getEmail());

				contactEntity.setPrimary(contactRequest.getPrimary());

				contactEntity.setProvider(providerEntity);

				contactEntities.add(contactEntity);
			}
		}

		providerEntity.setContacts(contactEntities);

		List<ProviderServiceHistoryEntity> serviceEntities = new ArrayList<>();

		if (request.getProviderServices() != null) {

			for (ProviderServiceHistoryRequestModel serviceRequest : request.getProviderServices()) {

				ProviderServiceHistoryEntity serviceEntity = new ProviderServiceHistoryEntity();

				serviceEntity.setService(serviceRequest.getService());

				serviceEntity.setStartDate(serviceRequest.getStartDate());

				serviceEntity.setEndDate(serviceRequest.getEndDate());

				serviceEntity.setStatus(serviceRequest.getStatus());

				serviceEntity.setNotes(serviceRequest.getNotes());

				serviceEntity.setCreatedDate(serviceRequest.getCreatedDate());

				serviceEntity.setUpdatedDate(serviceRequest.getUpdatedDate());

				serviceEntity.setProvider(providerEntity);

				serviceEntities.add(serviceEntity);
			}
		}

		providerEntity.setProviderServices(serviceEntities);

		// -----------------------------------------------------
		// UPDATE DOCUMENTS
		// -----------------------------------------------------

		List<ProviderDocumentEntity> documentEntities = new ArrayList<>();

		if (request.getDocuments() != null) {

			for (ProviderDocumentRequestModel documentRequest : request.getDocuments()) {

				ProviderDocumentEntity documentEntity = new ProviderDocumentEntity();

				documentEntity.setDocumentType(documentRequest.getDocumentType());

				documentEntity.setDocumentNumber(documentRequest.getDocumentNumber());

				documentEntity.setFileName(documentRequest.getFileName());

				documentEntity.setFilePath(documentRequest.getFilePath());

				documentEntity.setFileUrl(documentRequest.getFileUrl());

				documentEntity.setIssueDate(documentRequest.getIssueDate());

				documentEntity.setExpiryDate(documentRequest.getExpiryDate());

				documentEntity.setStatus(documentRequest.getStatus());

				documentEntity.setProvider(providerEntity);

				documentEntities.add(documentEntity);
			}
		}

		providerEntity.setDocuments(documentEntities);

		ProviderEntity updatedProvider = providerRepository.save(providerEntity);

		return convertToResponse(updatedProvider);
	}

	@Transactional
	public String deleteProvider(Integer id) {

		ProviderEntity providerEntity = providerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Provider not found with id: " + id));

		providerRepository.delete(providerEntity);

		return "Provider deleted successfully with id: " + id;
	}

	private ProviderAuthResponseModel mapAuthResponse(ProviderAuthEntity auth) {
		ProviderAuthResponseModel response = new ProviderAuthResponseModel();
		response.setId(auth.getId());
		response.setProviderId(auth.getProvider().getId());
		response.setProviderUniqueId(auth.getProvider().getUniqueId());
		response.setEmail(auth.getEmail());
		response.setLastLoginAt(auth.getLastLoginAt());
		response.setToken(java.util.UUID.randomUUID().toString());
		return response;
	}

	private ProviderResponseModel convertToResponse(ProviderEntity providerEntity) {

		ProviderResponseModel response = new ProviderResponseModel();
		response.setId(providerEntity.getId());
		response.setUniqueId(providerEntity.getUniqueId());

		response.setName(providerEntity.getName());

		response.setPhoneNumber(providerEntity.getPhoneNumber());

		response.setEmail(providerEntity.getEmail());
		response.setAvailabilityStatus(providerEntity.getAvailabilityStatus());

		List<ProviderAddressReponseModel> addressResponses = new ArrayList<>();

		if (providerEntity.getAddresses() != null) {

			for (ProviderAddressEntity addressEntity : providerEntity.getAddresses()) {

				ProviderAddressReponseModel addressResponse = new ProviderAddressReponseModel();

				addressResponse.setAddressType(addressEntity.getAddressType());

				addressResponse.setAddressLine1(addressEntity.getAddressLine1());

				addressResponse.setAddressLine2(addressEntity.getAddressLine2());

				addressResponse.setCity(addressEntity.getCity());

				addressResponse.setState(addressEntity.getState());

				addressResponse.setCountry(addressEntity.getCountry());

				addressResponse.setPostalCode(addressEntity.getPostalCode());

				addressResponse.setPrimary(addressEntity.getPrimary());
				addressResponse.setUniqueId(addressEntity.getUniqueId());
				addressResponse.setLatitude(addressEntity.getLatitude());
				addressResponse.setLongitude(addressEntity.getLongitude());

				addressResponses.add(addressResponse);
			}
		}

		response.setAddresses(addressResponses);

		// =====================================================
		// CONTACTS
		// =====================================================

		List<ProviderContactResponseModel> contactResponses = new ArrayList<>();

		if (providerEntity.getContacts() != null) {

			for (ProviderContactEntity contactEntity : providerEntity.getContacts()) {

				ProviderContactResponseModel contactResponse = new ProviderContactResponseModel();

				contactResponse.setFirstName(contactEntity.getFirstName());

				contactResponse.setLastName(contactEntity.getLastName());

				contactResponse.setPhoneNumber(contactEntity.getPhoneNumber());

				contactResponse.setEmail(contactEntity.getEmail());

				contactResponse.setPrimary(contactEntity.getPrimary());

				contactResponses.add(contactResponse);
			}
		}

		response.setContacts(contactResponses);

		List<ProviderServiceHistoryReponseModel> serviceResponses = new ArrayList<>();

		if (providerEntity.getProviderServices() != null) {

			for (ProviderServiceHistoryEntity serviceEntity : providerEntity.getProviderServices()) {

				ProviderServiceHistoryReponseModel serviceResponse = new ProviderServiceHistoryReponseModel();

				serviceResponse.setService(serviceEntity.getService());

				serviceResponse.setStartDate(serviceEntity.getStartDate());

				serviceResponse.setEndDate(serviceEntity.getEndDate());

				serviceResponse.setStatus(serviceEntity.getStatus());

				serviceResponse.setNotes(serviceEntity.getNotes());

				serviceResponse.setCreatedDate(serviceEntity.getCreatedDate());

				serviceResponse.setUpdatedDate(serviceEntity.getUpdatedDate());

				serviceResponses.add(serviceResponse);
			}
		}

		response.setProviderServices(serviceResponses);

		List<ProviderDocumentReponseModel> documentResponses = new ArrayList<>();

		if (providerEntity.getDocuments() != null) {

			for (ProviderDocumentEntity documentEntity : providerEntity.getDocuments()) {

				ProviderDocumentReponseModel documentResponse = new ProviderDocumentReponseModel();

				documentResponse.setDocumentType(documentEntity.getDocumentType());

				documentResponse.setDocumentNumber(documentEntity.getDocumentNumber());

				documentResponse.setFileName(documentEntity.getFileName());

				documentResponse.setFilePath(documentEntity.getFilePath());

				documentResponse.setFileUrl(documentEntity.getFileUrl());

				documentResponse.setIssueDate(documentEntity.getIssueDate());

				documentResponse.setExpiryDate(documentEntity.getExpiryDate());

				documentResponse.setStatus(documentEntity.getStatus());

				documentResponses.add(documentResponse);
			}
		}

		response.setDocuments(documentResponses);

		return response;
	}

	private void ensureProviderIdentity(ProviderEntity provider) {
		boolean changed = false;
		if (provider.getUniqueId() == null || provider.getUniqueId().isBlank()) {
			provider.setUniqueId(commons.generateUniqueId());
			changed = true;
		}
		if (provider.getAvailabilityStatus() == null || provider.getAvailabilityStatus().isBlank()) {
			provider.setAvailabilityStatus("AVAILABLE_NOW");
			changed = true;
		}
		if (changed) providerRepository.save(provider);
	}
}