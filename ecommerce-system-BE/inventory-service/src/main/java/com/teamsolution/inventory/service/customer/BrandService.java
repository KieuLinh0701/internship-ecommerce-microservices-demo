package com.teamsolution.inventory.service.customer;

import com.teamsolution.inventory.dto.request.BrandFilterRequest;
import com.teamsolution.inventory.dto.response.BrandDetailResponse;
import com.teamsolution.inventory.dto.response.BrandSummaryResponse;
import com.teamsolution.inventory.entity.Brand;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BrandService {

  // REST API
  Page<BrandSummaryResponse> getActiveBrands(Pageable pageable, BrandFilterRequest request);

  BrandDetailResponse getActiveBrandById(UUID id);

  // INTERNAL
  Brand getActiveById(UUID brandId);
}
