package com.teamsolution.cart.service.internal;

import com.teamsolution.cart.entity.CartItem;

import java.util.List;
import java.util.UUID;

public interface CartItemInternalService {

    List<CartItem> findByIdsAndCartId(List<UUID> cartItemIds, UUID cartId);

    void checkoutCartItems(List<UUID> cartItemIds);

    void rollbackCartItems(List<UUID> cartItemIds);
}
