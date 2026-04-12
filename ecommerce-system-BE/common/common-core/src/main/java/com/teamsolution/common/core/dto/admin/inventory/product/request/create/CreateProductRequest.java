package com.teamsolution.common.core.dto.admin.inventory.product.request.create;

import com.teamsolution.common.core.enums.inventory.ProductStatus;
import java.util.List;
import java.util.UUID;

public record CreateProductRequest(
    String name,
    String slug,
    String description,
    UUID categoryId,
    UUID brandId,
    ProductStatus status,
    List<ImageRequest> images,
    List<VariantRequest> variants) {}
