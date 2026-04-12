package com.teamsolution.cart.mapper;

import com.teamsolution.cart.dto.response.cartitem.AttributeValueResponse;
import com.teamsolution.cart.dto.response.cartitem.CartItemResponse;
import com.teamsolution.cart.entity.CartItem;
import com.teamsolution.proto.grpc.inventory.Variant;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CartItemMapper {
    public CartItemResponse toDto(CartItem item, Variant variant) {
        return CartItemResponse.builder()
                .id(item.getId())
                .quantity(item.getQuantity())
                .productId(UUID.fromString(variant.getProductId()))
                .productName(variant.getProductName())
                .productSlug(variant.getProductSlug())
                .variantId(item.getVariantId())
                .variantPrice(variant.getPrice())
                .variantImageUrl(variant.getImage())
                .attributes(variant.getAttributesList().stream()
                        .map(av -> AttributeValueResponse.builder()
                                .id(UUID.fromString(av.getId()))
                                .value(av.getValue())
                                .build())
                        .toList())
                .status(item.getStatus())
                .build();
    }
}
