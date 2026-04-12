package com.teamsolution.common.core.dto.admin.inventory.product.request;

import com.teamsolution.common.core.enums.inventory.ProductStatus;

public record UpdateProductStatusRequest(ProductStatus status) {}
