package com.teamsolution.inventory.mapper;

import com.teamsolution.inventory.entity.ProductImage;
import com.teamsolution.inventory.entity.ProductVariant;
import com.teamsolution.inventory.enums.ProductImageStatus;
import com.teamsolution.proto.grpc.inventory.AttributeValue;
import com.teamsolution.proto.grpc.inventory.Variant;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class VariantMapper {

    public Variant toVariant(ProductVariant variant) {
        return Variant.newBuilder()
                .setId(variant.getId().toString())
                .setPrice(variant.getPrice())
                .setImage(resolveImage(variant))
                .setStock(resolveStock(variant))
                .setProductId(variant.getProduct().getId().toString())
                .setProductName(variant.getProduct().getName())
                .setProductSlug(variant.getProduct().getSlug())
                .addAllAttributes(resolveAttributes(variant))
                .build();
    }

    private int resolveStock(ProductVariant variant) {
        var inventory = variant.getInventory();
        if (inventory == null) return 0;
        return inventory.getAvailableQuantity();
    }

    private String resolveImage(ProductVariant variant) {
        if (variant.getImageUrl() != null && !variant.getImageUrl().isBlank()) {
            return variant.getImageUrl();
        }

        var images = variant.getProduct().getImages();
        return images.stream()
                .filter(img -> img.getStatus() == ProductImageStatus.ACTIVE)
                .min(Comparator.comparingInt(ProductImage::getSortOrder))
                .map(ProductImage::getImageUrl)
                .orElse("");
    }

    private List<AttributeValue> resolveAttributes(ProductVariant variant) {
        return variant.getAttributeValues().stream()
                .map(pvav -> AttributeValue.newBuilder()
                        .setId(pvav.getAttributeValue().getId().toString())
                        .setValue(pvav.getAttributeValue().getValue())
                        .build())
                .toList();
    }

}
