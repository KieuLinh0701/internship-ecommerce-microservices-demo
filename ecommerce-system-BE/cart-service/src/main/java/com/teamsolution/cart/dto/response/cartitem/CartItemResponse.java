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

    // Product info
    private UUID productId;
    private String productName;
    private String productSlug;

    // Variant info
    private UUID variantId;
    private Long variantPrice;
    private String variantImageUrl;

    private List<AttributeValueResponse> attributes;

    private CartItemStatus status;
}
