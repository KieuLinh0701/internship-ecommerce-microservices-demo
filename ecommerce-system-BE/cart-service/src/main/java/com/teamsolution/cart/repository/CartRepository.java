package com.teamsolution.cart.repository;

import com.teamsolution.cart.entity.Cart;
import com.teamsolution.common.jpa.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository
        extends BaseRepository<Cart, UUID> {

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems WHERE c.customerId = :customerId AND c.isDeleted = false")
    Optional<Cart> findByCustomerIdAndIsDeletedFalse(@Param("customerId") UUID customerId);
}
