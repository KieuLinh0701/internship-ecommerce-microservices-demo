package com.teamsolution.customer.service;

import com.teamsolution.customer.dto.request.CustomerUpdateRequest;
import com.teamsolution.customer.dto.response.CustomerResponse;
import com.teamsolution.customer.entity.Customer;
import java.util.UUID;

public interface CustomerService {

  CustomerResponse getMyProfile(UUID customerId);

  CustomerResponse updateMyProfile(UUID customer, CustomerUpdateRequest request);

  Customer findById(UUID customerId);

  void createCustomer(UUID accountId, String fullName, String phone, String avatarUrl);

  Customer findByAccountId(UUID accountId);

  boolean existsByAccountId(UUID accountId);
}
