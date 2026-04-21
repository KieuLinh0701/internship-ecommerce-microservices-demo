package com.teamsolution.cart.service.internal.impl;

import com.teamsolution.cart.entity.Cart;
import com.teamsolution.cart.entity.CartItem;
import com.teamsolution.cart.exception.ErrorCode;
import com.teamsolution.cart.grpc.client.InventoryGrpcClient;
import com.teamsolution.cart.repository.CartRepository;
import com.teamsolution.cart.service.internal.CartInternalService;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.proto.grpc.inventory.Variant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartInternalServiceImpl
        implements CartInternalService {

    private final CartRepository cartRepository;
    private final InventoryGrpcClient inventoryGrpcClient;

    @Override
    @Transactional
    public Cart findOrCreateCart(UUID customerId) {
        return cartRepository.findByCustomerIdAndIsDeletedFalse(customerId)
                .orElseGet(() -> {
                    Cart cart = Cart.builder()
                            .customerId(customerId)
                            .build();
                    return cartRepository.save(cart);
                });
    }

    @Override
    public Cart findCartByCustomerId(UUID customerId) {
        return cartRepository.findByCustomerIdAndIsDeletedFalse(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
    }

    private Map<String, Variant> fetchVariantMap(List<CartItem> cartItems) {
        List<String> variantIds = cartItems
                .stream()
                .map(item -> item.getVariantId().toString())
                .toList();

        List<Variant> variants = inventoryGrpcClient.getVariantsByIds(variantIds);

        return variants.stream()
                .collect(Collectors.toMap(Variant::getId, v -> v));
    }
}