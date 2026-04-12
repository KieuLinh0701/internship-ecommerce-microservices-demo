package com.teamsolution.cart.repository;

import com.teamsolution.cart.entity.CartItem;
import com.teamsolution.cart.enums.CartItemStatus;
import com.teamsolution.common.jpa.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository
        extends BaseRepository<CartItem, UUID> {

    Optional<CartItem> findByCartIdAndVariantIdAndStatus(UUID cartId, UUID variantId, CartItemStatus status);

    Optional<CartItem> findByCartIdAndVariantIdAndStatusIn(UUID cartId, UUID variantId, List<CartItemStatus> statuses);

    List<CartItem> findAllByIdInAndCartIdAndIsDeletedFalseAndStatus(List<UUID> cartItemIds, UUID cartId,
            CartItemStatus status);

    Optional<CartItem> findByIdAndIsDeletedFalseAndStatus(UUID cartId, CartItemStatus status);

    Optional<CartItem> findByIdAndIsDeletedFalseAndStatusIn(UUID cartId, List<CartItemStatus> statuses);

    @Modifying
    @Query("""
                UPDATE CartItem c
                SET c.status = :newStatus
                WHERE c.id IN :ids
                  AND c.isDeleted = false
                  AND c.status = :oldStatus
            """)
    int updateStatusByIdsAndIsDeletedFalse(
            @Param("ids") List<UUID> ids,
            @Param("oldStatus") CartItemStatus oldStatus,
            @Param("newStatus") CartItemStatus newStatus
    );
}
