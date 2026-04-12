package com.teamsolution.inventory.dto.request;

import java.util.List;
import java.util.UUID;

public record MatchVariantRequest(
        List<UUID> attributeValueIds
) {}
