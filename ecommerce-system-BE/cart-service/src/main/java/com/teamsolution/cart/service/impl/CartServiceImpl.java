package com.teamsolution.cart.service.impl;

import com.teamsolution.cart.dto.response.CartResponse;
import com.teamsolution.cart.dto.response.cartitem.CartItemResponse;
import com.teamsolution.cart.entity.Cart;
import com.teamsolution.cart.entity.CartItem;
import com.teamsolution.cart.enums.CartItemStatus;
import com.teamsolution.cart.exception.ErrorCode;
import com.teamsolution.cart.grpc.client.InventoryGrpcClient;
import com.teamsolution.cart.mapper.CartItemMapper;
import com.teamsolution.cart.repository.CartRepository;
import com.teamsolution.cart.service.CartItemService;
import com.teamsolution.cart.service.CartService;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.proto.grpc.inventory.Variant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final InventoryGrpcClient inventoryGrpcClient;
    private final CartItemService cartItemService;
    private final CartItemMapper cartItemMapper;

    @Override
    @Transactional
    public CartResponse getCartByAccountId(UUID customerId) {
        Cart cart = findOrCreateCart(customerId);

        List<CartItem> activeItems = cart.getCartItems()
                .stream()
                .filter(item -> !item.getIsDeleted() && item.getStatus()
                        .isVisible())
                .toList();

        Map<String, Variant> variantMap = fetchVariantMap(activeItems);

        List<CartItemResponse> itemResponses = buildCartItemResponses(activeItems, variantMap);

        return CartResponse.builder()
                .id(cart.getId())
                .cartItems(itemResponses)
                .build();
    }

    @Override
    @Transactional
    public void removeCart(UUID customerId) {
        Cart cart = findCartByCustomerId(customerId);

        cart.getCartItems()
                .forEach(item -> {
                    item.setIsDeleted(true);
                    item.setStatus(CartItemStatus.REMOVED);
                });

        cartRepository.save(cart);
    }

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

    private List<CartItemResponse> buildCartItemResponses(
            List<CartItem> cartItems,
            Map<String, Variant> variantMap) {

        List<CartItem> updatedItems = new ArrayList<>();

        List<CartItemResponse> responses = cartItems.stream()
                .map(item -> {
                    Variant variant = variantMap.get(item.getVariantId().toString());

                    if (item.getStatus() == CartItemStatus.ACTIVE) {
                        if (variant.getStock() == 0) {
                            item.setStatus(CartItemStatus.OUT_OF_STOCK);
                            updatedItems.add(item);
                        } else if (variant.getStock() < item.getQuantity()) {
                            item.setQuantity((int) variant.getStock());
                            updatedItems.add(item);
                        }
                    } else if (variant.getStock() > 0 && item.getStatus() == CartItemStatus.OUT_OF_STOCK) {
                        item.setStatus(CartItemStatus.ACTIVE);
                        if (item.getQuantity() > variant.getStock()) {
                            item.setQuantity((int) variant.getStock());
                        }
                        updatedItems.add(item);
                    }

                    return cartItemMapper.toDto(item, variant);
                })
                .toList();

        if (!updatedItems.isEmpty()) {
            cartItemService.saveAllEntity(updatedItems);
        }

        return responses;
    }
}