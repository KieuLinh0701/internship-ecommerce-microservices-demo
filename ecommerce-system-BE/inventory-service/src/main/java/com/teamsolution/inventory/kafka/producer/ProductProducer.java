package com.teamsolution.inventory.kafka.producer;

import com.teamsolution.common.core.enums.inventory.ProductStatus;
import com.teamsolution.inventory.entity.Brand;
import com.teamsolution.inventory.entity.Category;
import com.teamsolution.inventory.entity.Product;
import com.teamsolution.inventory.enums.InventoryEventType;
import java.util.UUID;

public interface ProductProducer {
  public void changeProduct(
      Product product,
      Category category,
      Brand brand,
      String thumbnail,
      InventoryEventType eventType);

  void changeProductStatus(
      UUID productId, ProductStatus productStatus, InventoryEventType eventType);
}
