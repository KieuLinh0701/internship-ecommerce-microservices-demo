package com.teamsolution.customer.service.impl;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.customer.dto.request.CustomerUpdateRequest;
import com.teamsolution.customer.dto.response.CustomerResponse;
import com.teamsolution.customer.entity.Customer;
import com.teamsolution.customer.exception.ErrorCode;
import com.teamsolution.customer.mapper.CustomerMapper;
import com.teamsolution.customer.repository.CustomerRepository;
import com.teamsolution.customer.service.CustomerService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

  @Override
  public CustomerResponse getMyProfile(UUID customerId) {
    Customer entity = findById(customerId);

    return customerMapper.toDto(entity);
  }

  @Override
  public CustomerResponse updateMyProfile(UUID accountId, CustomerUpdateRequest request) {
    Customer customer = findById(accountId);

    customer.setFullName(request.getFullName());
    customer.setPhone(request.getPhone());
    customer.setAvatarUrl(request.getAvatarUrl());

    customerRepository.save(customer);

    return customerMapper.toDto(customer);
  }

  @Override
  public Customer findById(UUID accountId) {
    return customerRepository
        .findByIdAndIsDeletedFalse(accountId)
        .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
  }

  @Override
  public void createCustomer(UUID accountId, String fullName, String phone, String avatarUrl) {
    Customer customer =
        Customer.builder()
            .accountId(accountId)
            .fullName(fullName)
            .phone(phone)
            .avatarUrl(avatarUrl)
            .build();
    customerRepository.save(customer);
  }

  @Override
  public Customer findByAccountId(UUID accountId) {
    return customerRepository
        .findByAccountIdAndIsDeletedFalse(accountId)
        .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
  }

  @Override
  public boolean existsByAccountId(UUID accountId) {
    return customerRepository.existsByAccountIdAndIsDeletedFalse(accountId);
  }
}
