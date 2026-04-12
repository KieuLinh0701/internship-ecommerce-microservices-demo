package com.teamsolution.cart.service;

import com.teamsolution.cart.dto.response.CartResponse;
import com.teamsolution.cart.entity.Cart;

import java.util.UUID;

public interface CartService {

    CartResponse getCartByAccountId(UUID customerId);

    void removeCart(UUID customerId);

    Cart findOrCreateCart(UUID customerId);

    Cart findCartByCustomerId(UUID customerId);
}
