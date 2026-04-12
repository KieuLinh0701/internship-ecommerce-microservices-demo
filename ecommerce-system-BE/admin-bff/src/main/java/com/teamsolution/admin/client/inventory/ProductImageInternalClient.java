package com.teamsolution.admin.client.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamsolution.admin.client.BaseInternalClient;
import com.teamsolution.common.core.dto.admin.inventory.productimage.request.CreateProductImageRequest;
import com.teamsolution.common.core.dto.admin.inventory.productimage.response.ProductImageResponse;
import com.teamsolution.common.core.dto.common.response.ApiResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductImageInternalClient extends BaseInternalClient {

  private static final ParameterizedTypeReference<ApiResponse<List<ProductImageResponse>>>
      LIST_IMAGE_TYPE = new ParameterizedTypeReference<>() {};

  public ProductImageInternalClient(RestClient restClient, ObjectMapper objectMapper) {
    super(restClient, objectMapper);
  }

  public List<ProductImageResponse> createProductImage(
      String serviceId, CreateProductImageRequest request) {

    var builder = new MultipartBodyBuilder();

    for (var file : request.files()) {
      builder.part("files", file.getResource()).filename(file.getOriginalFilename());
    }

    var body = builder.build();

    return postMultipart(serviceId, "/internal/product-images", body, LIST_IMAGE_TYPE);
  }

  public void deleteProductImageById(String serviceId, UUID productImageId) {
    deleteAction(serviceId, "/internal/product-images/" + productImageId);
  }
}
