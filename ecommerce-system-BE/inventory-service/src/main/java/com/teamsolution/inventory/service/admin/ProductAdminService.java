package com.teamsolution.inventory.service.admin;

import com.teamsolution.common.core.dto.admin.inventory.product.request.FilterProductRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.request.create.CreateProductRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.response.ProductSummaryResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductAdminService {

  Page<ProductSummaryResponse> getAll(FilterProductRequest filterRequest, Pageable pageable);

  void delete(UUID productId, UUID adminAccountId);

  void restore(UUID productId, UUID adminAccountId);

  ProductSummaryResponse create(CreateProductRequest request);
}
