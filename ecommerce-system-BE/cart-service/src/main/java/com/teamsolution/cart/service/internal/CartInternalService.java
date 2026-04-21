package com.teamsolution.cart.service.internal;

import com.teamsolution.cart.entity.Cart;

import java.util.UUID;

public interface CartInternalService {

    Cart findOrCreateCart(UUID customerId);

    Cart findCartByCustomerId(UUID customerId);
}
