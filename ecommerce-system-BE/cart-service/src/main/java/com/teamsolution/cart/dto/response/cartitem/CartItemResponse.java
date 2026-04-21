package com.teamsolution.cart.dto.response.cartitem;

import com.teamsolution.cart.enums.CartItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponse {
    private UUID id;
    private int quantity;
    private CartItemStatus status;

    // Product info
    private ProductResponse product;

    // Variant info
    private VariantResponse variant;

    private List<AttributeValueResponse> attributes;
}
