package com.teamsolution.search.service;

import com.teamsolution.common.kafka.event.inventory.ProductChangedEvent;
import com.teamsolution.common.kafka.event.inventory.ProductStatusChangedEvent;
import com.teamsolution.search.dto.request.ProductFilterRequest;
import com.teamsolution.search.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductSearchService {
  Page<ProductResponse> search(ProductFilterRequest request, Pageable pageable);

  void sync(ProductChangedEvent event);

  void updateStatus(ProductStatusChangedEvent event);
}
