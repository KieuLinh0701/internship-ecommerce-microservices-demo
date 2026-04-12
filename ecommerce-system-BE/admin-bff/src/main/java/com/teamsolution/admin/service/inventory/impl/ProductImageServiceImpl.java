package com.teamsolution.admin.service.inventory.impl;

import com.teamsolution.admin.client.inventory.ProductImageInternalClient;
import com.teamsolution.admin.config.properties.ConsumerServicesProperties;
import com.teamsolution.admin.enums.ServiceKey;
import com.teamsolution.admin.service.inventory.ProductImageService;
import com.teamsolution.common.core.dto.admin.inventory.productimage.request.CreateProductImageRequest;
import com.teamsolution.common.core.dto.admin.inventory.productimage.response.ProductImageResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

  private final ConsumerServicesProperties consumerServicesProperties;
  private final ProductImageInternalClient internalClient;

  @Override
  public List<ProductImageResponse> createProductImage(CreateProductImageRequest request) {
    return internalClient.createProductImage(getServiceId(), request);
  }

  @Override
  public void deleteProductImageById(UUID productImageId) {
    internalClient.deleteProductImageById(getServiceId(), productImageId);
  }

  private String getServiceId() {
    return consumerServicesProperties.getServiceId(ServiceKey.INVENTORY);
  }
}
