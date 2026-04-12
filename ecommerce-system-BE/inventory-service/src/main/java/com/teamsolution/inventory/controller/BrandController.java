package com.teamsolution.inventory.controller;

import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import com.teamsolution.common.jpa.mapper.PageMapper;
import com.teamsolution.common.jpa.utils.PageableUtils;
import com.teamsolution.inventory.dto.request.BrandFilterRequest;
import com.teamsolution.inventory.dto.response.BrandDetailResponse;
import com.teamsolution.inventory.dto.response.BrandSummaryResponse;
import com.teamsolution.inventory.service.customer.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {

  private final BrandService brandService;

  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<BrandSummaryResponse>>> getActiveBrands(
      BrandFilterRequest filterRequest) {

    Pageable pageable =
        PageableUtils.toPageable(
            filterRequest.getPage(),
            filterRequest.getSize(),
            filterRequest.getSortBy(),
            filterRequest.getDirection(),
            false);

    Page<BrandSummaryResponse> brands = brandService.getActiveBrands(pageable, filterRequest);
    return ResponseEntity.ok(ApiResponse.success(PageMapper.toPageResponse(brands)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<BrandDetailResponse>> getById(@PathVariable UUID id) {

    BrandDetailResponse brand = brandService.getActiveBrandById(id);
    return ResponseEntity.ok(ApiResponse.success(brand));
  }
}
