package com.teamsolution.common.core.dto.admin.inventory.product.request.create;

import java.util.List;
import java.util.UUID;

public record VariantRequest(
    String sku,
    Long price,
    Long compareAtPrice,
    String imageUrl,
    List<UUID> attributeValueIds,
    InventoryRequest inventory) {}
