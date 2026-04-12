package com.teamsolution.admin.client.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamsolution.admin.client.BaseInternalClient;
import com.teamsolution.common.core.dto.admin.inventory.product.request.FilterProductRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.request.UpdateProductRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.request.UpdateProductStatusRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.request.create.CreateProductRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.response.ProductDetailResponse;
import com.teamsolution.common.core.dto.admin.inventory.product.response.ProductSummaryResponse;
import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import java.util.Map;
import java.util.UUID;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductInternalClient extends BaseInternalClient {

  private static final ParameterizedTypeReference<ApiResponse<PageResponse<ProductSummaryResponse>>>
      PAGE_TYPE = new ParameterizedTypeReference<>() {};

  private static final ParameterizedTypeReference<ApiResponse<ProductSummaryResponse>>
      SUMMARY_TYPE = new ParameterizedTypeReference<>() {};

  private static final ParameterizedTypeReference<ApiResponse<ProductDetailResponse>> DETAIL_TYPE =
      new ParameterizedTypeReference<>() {};

  public ProductInternalClient(RestClient restClient, ObjectMapper objectMapper) {
    super(restClient, objectMapper);
  }

  public PageResponse<ProductSummaryResponse> getProducts(
      String serviceId, FilterProductRequest request) {
    return fetchPage(serviceId, "/internal/products", request, PAGE_TYPE);
  }

  public ProductDetailResponse getProductById(String serviceId, UUID productId) {
    return getAction(serviceId, "/internal/products/" + productId, DETAIL_TYPE);
  }

  public ProductSummaryResponse createProduct(String serviceId, CreateProductRequest request) {
    return postAction(serviceId, "/internal/products", request, SUMMARY_TYPE);
  }

  public ProductDetailResponse updateProductById(
      String serviceId, UUID productId, UpdateProductRequest request) {
    return patchAction(serviceId, "/internal/products/" + productId, request, DETAIL_TYPE);
  }

  public void restoreProductById(String serviceId, UUID productId) {
    patchAction(serviceId, "/internal/products/" + productId + "/restore", Map.of(), VOID_TYPE);
  }

  public void deleteProductById(String serviceId, UUID productId) {
    deleteAction(serviceId, "/internal/products/" + productId);
  }

  public ProductSummaryResponse updateProductStatusById(
      String serviceId, UUID productId, UpdateProductStatusRequest request) {
    return patchAction(
        serviceId, "/internal/products/" + productId + "/status", request, SUMMARY_TYPE);
  }
}
