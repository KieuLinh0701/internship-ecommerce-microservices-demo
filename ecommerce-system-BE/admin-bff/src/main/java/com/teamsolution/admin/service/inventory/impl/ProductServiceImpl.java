package com.teamsolution.admin.service.inventory.impl;

import com.teamsolution.admin.client.inventory.ProductInternalClient;
import com.teamsolution.admin.config.properties.ConsumerServicesProperties;
import com.teamsolution.admin.enums.ServiceKey;
import com.teamsolution.admin.service.inventory.ProductService;
import com.teamsolution.common.core.dto.admin.inventory.product.request.FilterProductRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.request.UpdateProductRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.request.UpdateProductStatusRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.request.create.CreateProductRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.response.ProductDetailResponse;
import com.teamsolution.common.core.dto.admin.inventory.product.response.ProductSummaryResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ConsumerServicesProperties consumerServicesProperties;
  private final ProductInternalClient productInternalClient;

  @Override
  public PageResponse<ProductSummaryResponse> getProducts(FilterProductRequest filterRequest) {
    return productInternalClient.getProducts(getServiceId(), filterRequest);
  }

  @Override
  public ProductDetailResponse getProductById(UUID productId) {
    return productInternalClient.getProductById(getServiceId(), productId);
  }

  @Override
  public ProductSummaryResponse createProduct(CreateProductRequest request) {
    return productInternalClient.createProduct(getServiceId(), request);
  }

  @Override
  public ProductDetailResponse updateProductById(UUID productId, UpdateProductRequest request) {
    return productInternalClient.updateProductById(getServiceId(), productId, request);
  }

  @Override
  public void deleteProductById(UUID productId) {
    productInternalClient.deleteProductById(getServiceId(), productId);
  }

  @Override
  public void restoreProductById(UUID productId) {
    productInternalClient.restoreProductById(getServiceId(), productId);
  }

  @Override
  public ProductSummaryResponse updateProductStatusById(
      UUID productId, UpdateProductStatusRequest request) {
    return productInternalClient.updateProductStatusById(getServiceId(), productId, request);
  }

  private String getServiceId() {
    return consumerServicesProperties.getServiceId(ServiceKey.INVENTORY);
  }
}
