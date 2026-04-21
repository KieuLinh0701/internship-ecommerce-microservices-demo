package com.teamsolution.cart.service.internal.impl;

import com.teamsolution.cart.entity.CartItem;
import com.teamsolution.cart.enums.CartItemStatus;
import com.teamsolution.cart.exception.ErrorCode;
import com.teamsolution.cart.repository.CartItemRepository;
import com.teamsolution.cart.service.internal.CartItemInternalService;
import com.teamsolution.common.core.exception.PermanentException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartItemInternalServiceImpl
        implements CartItemInternalService {

    private final CartItemRepository cartItemRepository;

    @Override
    public List<CartItem> findByIdsAndCartId(List<UUID> cartItemIds, UUID cartId) {
        return cartItemRepository.findAllByIdInAndCartIdAndIsDeletedFalseAndStatus(
                cartItemIds,
                cartId,
                CartItemStatus.ACTIVE);
    }

    @Override
    @Transactional
    public void checkoutCartItems(List<UUID> cartItemIds) {
        int updated = cartItemRepository.updateStatusByIdsAndIsDeletedFalse(
                cartItemIds,
                CartItemStatus.ACTIVE,
                CartItemStatus.CHECKED_OUT);

        if (updated != cartItemIds.size()) {
            throw new PermanentException(
                    ErrorCode.CART_ITEM_CANNOT_CHECKOUT
            );
        }
    }

    @Override
    public void rollbackCartItems(List<UUID> cartItemIds) {
        int updated = cartItemRepository.updateStatusByIdsAndIsDeletedFalse(
                cartItemIds,
                CartItemStatus.CHECKED_OUT,
                CartItemStatus.ACTIVE);

        if (updated != cartItemIds.size()) {
            throw new PermanentException(
                    ErrorCode.CART_ITEM_CANNOT_ACTIVE
            );
        }
    }
}