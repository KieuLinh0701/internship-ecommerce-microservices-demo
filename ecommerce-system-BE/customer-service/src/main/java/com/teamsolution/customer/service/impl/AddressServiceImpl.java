package com.teamsolution.customer.service.impl;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.customer.dto.request.AddressCreateRequest;
import com.teamsolution.customer.dto.request.AddressUpdateRequest;
import com.teamsolution.customer.dto.response.AddressResponse;
import com.teamsolution.customer.entity.Address;
import com.teamsolution.customer.entity.Customer;
import com.teamsolution.customer.exception.ErrorCode;
import com.teamsolution.customer.mapper.AddressMapper;
import com.teamsolution.customer.repository.AddressRepository;
import com.teamsolution.customer.service.AddressService;
import com.teamsolution.customer.service.CustomerService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

  private final AddressRepository addressRepository;
  private final CustomerService customerService;
  private final AddressMapper addressMapper;

  @Override
  public List<AddressResponse> getAddressesByCustomerId(UUID customerId) {
    Customer customer = customerService.findById(customerId);

    List<Address> addresses =
        addressRepository.findByCustomerIdAndIsDeletedFalseOrderByIsDefaultDesc(customer.getId());

    return addressMapper.toDtoList(addresses);
  }

  @Override
  @Transactional
  public AddressResponse createAddress(UUID customerId, AddressCreateRequest request) {
    Customer customer = customerService.findById(customerId);

    long count = addressRepository.countByCustomerIdAndIsDeletedFalse(customer.getId());
    boolean shouldBeDefault = request.isDefault() || count == 0;

    if (shouldBeDefault) {
      addressRepository.unsetDefaultByCustomerId(customer.getId());
    }

    Address address =
        Address.builder()
            .customer(customer)
            .name(request.name())
            .phone(request.phone())
            .cityCode(request.cityCode())
            .cityName(request.cityName())
            .wardCode(request.wardCode())
            .wardName(request.wardName())
            .detail(request.detail())
            .isDefault(shouldBeDefault)
            .build();

    return addressMapper.toDto(addressRepository.save(address));
  }

  @Override
  @Transactional
  public AddressResponse updateAddress(
      UUID customerId, UUID addressId, AddressUpdateRequest request) {
    Customer customer = customerService.findById(customerId);
    Address address = findById(addressId);

    assertOwnership(address, customer);

    address.setName(request.name());
    address.setPhone(request.phone());
    address.setCityCode(request.cityCode());
    address.setCityName(request.cityName());
    address.setWardCode(request.wardCode());
    address.setWardName(request.wardName());
    address.setDetail(request.detail());

    return addressMapper.toDto(addressRepository.save(address));
  }

  @Override
  @Transactional
  public void deleteAddress(UUID customerId, UUID addressId) {
    Customer customer = customerService.findById(customerId);
    Address address = findById(addressId);

    assertOwnership(address, customer);

    // Cannot delete the default address if it is the only remaining address
    if (address.getIsDefault()) {
      long remaining = addressRepository.countByCustomerIdAndIsDeletedFalse(customer.getId());
      if (remaining <= 1) {
        throw new AppException(ErrorCode.ADDRESS_DEFAULT_CANNOT_DELETE);
      }

      addressRepository
          .findFirstByCustomerIdAndIsDeletedFalseAndIdNotOrderByCreatedAtAsc(
              customer.getId(), addressId)
          .ifPresent(
              other -> {
                other.setIsDefault(true);
                addressRepository.save(other);
              });
    }

    address.setIsDeleted(true);

    addressRepository.save(address);
  }

  @Override
  @Transactional
  public AddressResponse setDefaultAddress(UUID customerId, UUID addressId) {
    Customer customer = customerService.findById(customerId);
    Address address = findById(addressId);

    assertOwnership(address, customer);

    if (address.getIsDefault()) {
      throw new AppException(ErrorCode.ADDRESS_ALREADY_DEFAULT);
    }

    // Unset old default
    addressRepository.unsetDefaultByCustomerId(customer.getId());

    // Set new default
    address.setIsDefault(true);
    return addressMapper.toDto(addressRepository.save(address));
  }

  @Override
  public Address findByIdAndCustomerId(UUID addressId, UUID customerId) {
    return addressRepository
        .findAddressByIdAndCustomerIdAndIsDeletedFalse(addressId, customerId)
        .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
  }

  private Address findById(UUID id) {
    return addressRepository
        .findByIdAndIsDeletedFalse(id)
        .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
  }

  private void assertOwnership(Address address, Customer customer) {
    if (!address.getCustomer().getId().equals(customer.getId())) {
      throw new AppException(ErrorCode.ADDRESS_ACCESS_DENIED);
    }
  }
}
