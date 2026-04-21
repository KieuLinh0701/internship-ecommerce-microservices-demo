package com.teamsolution.cart.service.customer;

import com.teamsolution.cart.dto.request.CreateCartItemRequest;
import com.teamsolution.cart.dto.request.UpdateCartItemQuantityRequest;
import com.teamsolution.cart.dto.request.UpdateCartItemVariantRequest;
import com.teamsolution.cart.dto.response.cartitem.CartItemResponse;
import com.teamsolution.cart.entity.CartItem;

import java.util.List;
import java.util.UUID;

public interface CartItemService {

    void saveAllEntity(List<CartItem> items);

    CartItemResponse addCartItem(UUID customerId, CreateCartItemRequest request);

    void deleteCartItem(UUID customerId, UUID cartItemId);

    CartItemResponse updateCartItemQuantity(
            UUID customerId,
            UUID cartItemId,
            UpdateCartItemQuantityRequest request);

    CartItemResponse updateCartItemVariant(UUID customerId, UUID cartItemId, UpdateCartItemVariantRequest request);

//    List<CartItem> findByIdsAndCartId(List<UUID> cartItemIds, UUID cartId);
//
//    int checkoutCartItems(List<UUID> cartItemIds);
}
