package com.teamsolution.inventory.repository;

import com.teamsolution.common.jpa.repository.BaseRepository;
import com.teamsolution.inventory.entity.ProductVariantInventory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProductVariantInventoryRepository
        extends BaseRepository<ProductVariantInventory, UUID> {

    // Atomic reserve when order create
    @Modifying
    @Query("""
                UPDATE ProductVariantInventory i
                SET i.reservedQuantity = i.reservedQuantity + :quantity
                WHERE i.variant.id = :variantId
                  AND (i.quantity - i.reservedQuantity) >= :quantity
            """)
    int reserveStockIfSufficient(
            @Param("variantId") UUID variantId,
            @Param("quantity") int quantity
    );

    // Finalize inventory after payment:
    // - Convert reserved stock to sold
    // - Deduct from available quantity
    // - Ensure atomic update to prevent overselling
    @Modifying
    @Query("""
                UPDATE ProductVariantInventory i
                SET i.quantity         = i.quantity - :quantity,
                    i.reservedQuantity = i.reservedQuantity - :quantity,
                    i.soldQuantity     = i.soldQuantity + :quantity
                WHERE i.variant.id = :variantId
                  AND i.reservedQuantity >= :quantity
            """)
    int confirmStock(
            @Param("variantId") UUID variantId,
            @Param("quantity") int quantity
    );

    @Modifying
    @Query("""
                UPDATE ProductVariantInventory i
                SET i.quantity      = i.quantity + :quantity,
                    i.soldQuantity  = i.soldQuantity - :quantity
                WHERE i.variant.id = :variantId
                  AND i.soldQuantity >= :quantity
            """)
    int restoreConfirmedStock(
            @Param("variantId") UUID variantId,
            @Param("quantity") int quantity
    );
}
