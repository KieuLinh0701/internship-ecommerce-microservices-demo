package com.teamsolution.common.core.dto.admin.inventory.product.request.create;

import java.time.LocalDate;

public record InventoryRequest(
    Integer quantity, Integer lowStockThreshold, LocalDate manufactureDate, LocalDate expiryDate) {}
