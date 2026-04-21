package com.teamsolution.inventory.kafka.producer.impl;

import com.teamsolution.common.core.enums.inventory.ProductStatus;
import com.teamsolution.common.core.util.JsonUtils;
import com.teamsolution.common.kafka.event.inventory.ProductChangedEvent;
import com.teamsolution.common.kafka.event.inventory.ProductStatusChangedEvent;
import com.teamsolution.inventory.entity.Brand;
import com.teamsolution.inventory.entity.Category;
import com.teamsolution.inventory.entity.OutboxEvent;
import com.teamsolution.inventory.entity.Product;
import com.teamsolution.inventory.enums.EntityName;
import com.teamsolution.inventory.enums.InventoryEventType;
import com.teamsolution.inventory.kafka.producer.ProductProducer;
import com.teamsolution.inventory.repository.OutboxEventRepository;
import com.teamsolution.tracing.context.TraceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductProducerImpl
        implements ProductProducer {

    private final OutboxEventRepository outboxEventRepository;
    private final TraceContext traceContext;

    @Override
    public void changeProduct(
            Product product,
            Category category,
            Brand brand,
            String thumbnail,
            InventoryEventType eventType) {

        ProductChangedEvent event =
                ProductChangedEvent.builder()
                        .productId(product.getId()
                                .toString())
                        .name(product.getName())
                        .description(product.getDescription())
                        .categoryId(category != null ? category.getId()
                                .toString() : null)
                        .categoryName(category != null ? category.getName() : null)
                        .brandId(brand != null ? brand.getId()
                                .toString() : null)
                        .brandName(brand != null ? brand.getName() : null)
                        .maxPrice(product.getMaxPrice())
                        .minPrice(product.getMinPrice())
                        .status(product.getStatus()
                                .name())
                        .thumbnail(thumbnail)
                        .createdAt(product.getCreatedAt())
                        .build();

        OutboxEvent outboxEvent =
                OutboxEvent.builder()
                        .traceId(traceContext.currentTraceId())
                        .aggregateId(product.getId())
                        .aggregateType(EntityName.PRODUCT.getValue())
                        .eventType(eventType.name())
                        .nextRetryAt(LocalDateTime.now())
                        .payload(JsonUtils.toJson(event))
                        .build();

        outboxEventRepository.save(outboxEvent);
    }

    @Override
    public void changeProductStatus(
            UUID productId, ProductStatus productStatus, InventoryEventType eventType) {

        ProductStatusChangedEvent event =
                ProductStatusChangedEvent.builder()
                        .productId(productId.toString())
                        .status(productStatus.name())
                        .build();

        OutboxEvent outboxEvent =
                OutboxEvent.builder()
                        .traceId(traceContext.currentTraceId())
                        .aggregateId(productId)
                        .aggregateType(EntityName.PRODUCT.getValue())
                        .eventType(eventType.name())
                        .nextRetryAt(LocalDateTime.now())
                        .payload(JsonUtils.toJson(event))
                        .build();

        outboxEventRepository.save(outboxEvent);
    }
}
