package com.teamsolution.order.dto.request;

import java.util.Optional;
import java.util.UUID;

public record UpdateOrderRequest(UUID addressId, Optional<String> notes) {}
