package com.teamsolution.search.controller;

import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import com.teamsolution.common.jpa.mapper.PageMapper;
import com.teamsolution.search.dto.request.ProductFilterRequest;
import com.teamsolution.search.dto.response.ProductResponse;
import com.teamsolution.search.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductSearchController {
  private final ProductSearchService searchService;

  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> search(
      ProductFilterRequest filterRequest) {

    Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize());

    Page<ProductResponse> productResponses = searchService.search(filterRequest, pageable);
    return ResponseEntity.ok(ApiResponse.success(PageMapper.toPageResponse(productResponses)));
  }
}
