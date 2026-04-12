package com.teamsolution.customer.controller;

import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.security.SecurityUtils;
import com.teamsolution.customer.dto.request.CustomerUpdateRequest;
import com.teamsolution.customer.dto.response.CustomerResponse;
import com.teamsolution.customer.service.CustomerService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerService customerService;

  @GetMapping
  public ResponseEntity<ApiResponse<CustomerResponse>> getMe() {

    UUID currentCustomerId = SecurityUtils.getCurrentCustomerId();

    CustomerResponse customerResponse = customerService.getMyProfile(currentCustomerId);
    return ResponseEntity.ok(ApiResponse.success(customerResponse));
  }

  @PutMapping
  public ResponseEntity<ApiResponse<CustomerResponse>> updateMe(
      @Valid @RequestBody CustomerUpdateRequest request) {

    UUID currentCustomerId = SecurityUtils.getCurrentCustomerId();

    CustomerResponse customerResponse = customerService.updateMyProfile(currentCustomerId, request);
    return ResponseEntity.ok(ApiResponse.success(customerResponse));
  }
}
