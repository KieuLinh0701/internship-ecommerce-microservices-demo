package com.teamsolution.common.kafka.event.order;

import java.util.UUID;

public record OrderItemEvent(UUID variantId, int quantity) {}
