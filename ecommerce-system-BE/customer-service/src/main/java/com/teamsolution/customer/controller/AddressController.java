package com.teamsolution.customer.controller;

import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.security.SecurityUtils;
import com.teamsolution.customer.dto.request.AddressCreateRequest;
import com.teamsolution.customer.dto.request.AddressUpdateRequest;
import com.teamsolution.customer.dto.response.AddressResponse;
import com.teamsolution.customer.service.AddressService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me/addresses")
@RequiredArgsConstructor
public class AddressController {

  private final AddressService addressService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses() {

    UUID currentCustomerId = SecurityUtils.getCurrentCustomerId();

    List<AddressResponse> addressResponse =
        addressService.getAddressesByCustomerId(currentCustomerId);
    return ResponseEntity.ok(ApiResponse.success(addressResponse));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
      @Valid @RequestBody AddressCreateRequest request) {

    UUID currentCustomerId = SecurityUtils.getCurrentCustomerId();

    AddressResponse addressResponse = addressService.createAddress(currentCustomerId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(addressResponse));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
      @PathVariable UUID id, @Valid @RequestBody AddressUpdateRequest request) {

    UUID currentCustomerId = SecurityUtils.getCurrentCustomerId();

    AddressResponse addressResponse = addressService.updateAddress(currentCustomerId, id, request);
    return ResponseEntity.ok(ApiResponse.success(addressResponse));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable UUID id) {

    UUID currentCustomerId = SecurityUtils.getCurrentCustomerId();

    addressService.deleteAddress(currentCustomerId, id);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PutMapping("/{id}/set-default")
  public ResponseEntity<ApiResponse<AddressResponse>> setDefault(@PathVariable UUID id) {

    UUID currentCustomerId = SecurityUtils.getCurrentCustomerId();

    AddressResponse addressResponse = addressService.setDefaultAddress(currentCustomerId, id);
    return ResponseEntity.ok(ApiResponse.success(addressResponse));
  }
}
