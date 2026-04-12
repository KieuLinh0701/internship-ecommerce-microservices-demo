package com.teamsolution.customer.service;

import com.teamsolution.customer.dto.request.AddressCreateRequest;
import com.teamsolution.customer.dto.request.AddressUpdateRequest;
import com.teamsolution.customer.dto.response.AddressResponse;
import com.teamsolution.customer.entity.Address;
import java.util.List;
import java.util.UUID;

public interface AddressService {
  List<AddressResponse> getAddressesByCustomerId(UUID customerId);

  AddressResponse createAddress(UUID customerId, AddressCreateRequest request);

  AddressResponse updateAddress(UUID customerId, UUID addressId, AddressUpdateRequest request);

  void deleteAddress(UUID customerId, UUID addressId);

  AddressResponse setDefaultAddress(UUID customerId, UUID addressId);

  Address findByIdAndCustomerId(UUID addressId, UUID customerId);
}
