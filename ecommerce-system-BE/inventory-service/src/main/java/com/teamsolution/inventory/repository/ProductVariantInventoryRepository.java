package com.teamsolution.inventory.repository;

import com.teamsolution.common.jpa.repository.BaseRepository;
import com.teamsolution.inventory.entity.ProductVariantInventory;
import java.util.UUID;

public interface ProductVariantInventoryRepository
    extends BaseRepository<ProductVariantInventory, UUID> {}
