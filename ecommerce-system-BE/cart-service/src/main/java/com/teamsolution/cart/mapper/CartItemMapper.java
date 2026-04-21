package com.teamsolution.cart.mapper;

import com.teamsolution.cart.dto.response.cartitem.AttributeValueResponse;
import com.teamsolution.cart.dto.response.cartitem.CartItemResponse;
import com.teamsolution.cart.dto.response.cartitem.ProductResponse;
import com.teamsolution.cart.dto.response.cartitem.VariantResponse;
import com.teamsolution.cart.entity.CartItem;
import com.teamsolution.common.core.util.UuidUtils;
import com.teamsolution.proto.grpc.inventory.Variant;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CartItemMapper {
    public CartItemResponse toDto(CartItem item, Variant variant) {
        return CartItemResponse.builder()
                .id(item.getId())
                .quantity(item.getQuantity())
                .product(
                        ProductResponse.builder()
                                .id(UuidUtils.parse(variant.getProductId()))
                                .name(variant.getProductName())
                                .build()
                )
                .variant(
                        VariantResponse.builder()
                                .id(UuidUtils.parse(variant.getId()))
                                .price(variant.getPrice())
                                .imageUrl(variant.getImage())
                                .stock(variant.getStock())
                                .build()
                )
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
