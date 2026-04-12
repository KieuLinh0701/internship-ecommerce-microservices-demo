package com.teamsolution.cart.service.impl;

import com.teamsolution.cart.dto.request.AddCartItemRequest;
import com.teamsolution.cart.dto.request.UpdateCartItemQuantityRequest;
import com.teamsolution.cart.dto.request.UpdateCartItemVariantRequest;
import com.teamsolution.cart.dto.response.cartitem.CartItemResponse;
import com.teamsolution.cart.entity.Cart;
import com.teamsolution.cart.entity.CartItem;
import com.teamsolution.cart.enums.CartItemStatus;
import com.teamsolution.cart.exception.ErrorCode;
import com.teamsolution.cart.grpc.client.InventoryGrpcClient;
import com.teamsolution.cart.mapper.CartItemMapper;
import com.teamsolution.cart.repository.CartItemRepository;
import com.teamsolution.cart.service.CartItemService;
import com.teamsolution.cart.service.CartService;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.util.UuidUtils;
import com.teamsolution.proto.grpc.inventory.Variant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    private final CartService cartService;

    private final InventoryGrpcClient inventoryGrpcClient;

    // Internal
    @Override
    public void saveAllEntity(List<CartItem> items) {
        cartItemRepository.saveAll(items);
    }

    // Customer
    @Override
    public CartItemResponse addCartItem(UUID customerId, AddCartItemRequest request) {
        Cart cart = cartService.findOrCreateCart(customerId);

        Variant variant = getAndValidateVariant(request.variantId());

        return cartItemRepository
                .findByCartIdAndVariantIdAndStatusIn(
                        cart.getId(),
                        request.variantId(),
                        List.of(CartItemStatus.ACTIVE, CartItemStatus.OUT_OF_STOCK))
                .map(existingItem -> {
                    if (existingItem.getStatus() == CartItemStatus.OUT_OF_STOCK && variant.getStock() > 0) {
                        existingItem.setStatus(CartItemStatus.ACTIVE);
                        if (existingItem.getQuantity() > variant.getStock()) {
                            existingItem.setQuantity((int) variant.getStock());
                        }
                        cartItemRepository.save(existingItem);
                    }
                    return updateExistingCartItem(existingItem, request.quantity(), variant);
                })
                .orElseGet(() -> createNewCartItem(cart, request, variant));
    }

    @Override
    public void deleteCartItem(UUID customerId, UUID cartItemId) {

        CartItem cartItem = validateCartItemOwnershipActiveOrOutOfStock(customerId, cartItemId);

        cartItem.setIsDeleted(true);
        cartItem.setStatus(CartItemStatus.REMOVED);
        cartItemRepository.save(cartItem);
    }

    @Override
    public CartItemResponse updateCartItemQuantity(
            UUID customerId,
            UUID cartItemId,
            UpdateCartItemQuantityRequest request) {
        CartItem item = validateCartItemOwnershipActive(customerId, cartItemId);

        if (request.quantity() == 0) {
            item.setStatus(CartItemStatus.REMOVED);
            item.setIsDeleted(true);
            return null;
        }

        Variant variant = getAndValidateVariant(item.getVariantId());
        validateStock(request.quantity(), variant.getStock());

        item.setQuantity(request.quantity());
        return cartItemMapper.toDto(cartItemRepository.save(item), variant);
    }

    @Override
    public CartItemResponse updateCartItemVariant(
            UUID customerId,
            UUID cartItemId,
            UpdateCartItemVariantRequest request) {
        CartItem oldItem = validateCartItemOwnershipActive(customerId, cartItemId);

        if (oldItem.getVariantId().equals(request.variantId())) {
            return updateQuantityOnly(oldItem, request.quantity());
        }

        return switchToNewVariant(oldItem, request);
    }

//    @Override
//    public List<CartItem> findByIdsAndCartId(List<UUID> cartItemIds, UUID cartId) {
//        return cartItemRepository.findAllByIdInAndCartIdAndIsDeleteFalseAndStatus(cartItemIds, cartId, CartItemStatus.ACTIVE);
//    }
//
//    @Override
//    @Transactional
//    public int checkoutCartItems(List<UUID> cartItemIds) {
//        return cartItemRepository.updateStatusByIdsAndIsDeleteFalse(
//                cartItemIds,
//                CartItemStatus.ACTIVE,
//                CartItemStatus.CHECKED_OUT);
//    }

    private CartItemResponse updateQuantityOnly(CartItem item, int newQuantity) {
        Variant variant = getAndValidateVariant(item.getVariantId());
        validateStock(newQuantity, variant.getStock());
        item.setQuantity(newQuantity);
        return cartItemMapper.toDto(cartItemRepository.save(item), variant);
    }

    private CartItemResponse switchToNewVariant(CartItem oldItem, UpdateCartItemVariantRequest request) {
        Variant newVariant = getAndValidateVariant(request.variantId());
        validateStock(request.quantity(), newVariant.getStock());

        oldItem.setIsDeleted(true);
        oldItem.setStatus(CartItemStatus.REMOVED);
        cartItemRepository.save(oldItem);

        Cart cart = oldItem.getCart();
        return cartItemRepository
                .findByCartIdAndVariantIdAndStatus(cart.getId(), request.variantId(), CartItemStatus.ACTIVE)
                .map(existing -> updateExistingCartItem(existing, request.quantity(), newVariant))
                .orElseGet(() -> createNewCartItem(cart,
                        new AddCartItemRequest(
                                request.variantId(),
                                request.quantity()),
                        newVariant));
    }

    private Variant getAndValidateVariant(UUID variantId) {
        List<Variant> variants = inventoryGrpcClient.getVariantsByIds(List.of(variantId.toString()));

        if (variants.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND);
        }

        return variants.getFirst();
    }

    private void validateStock(int requestedQuantity, long stock) {
        if (requestedQuantity > stock) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }
    }

    private CartItemResponse updateExistingCartItem(CartItem item, int addedQuantity, Variant variant) {
        int newQuantity = item.getQuantity() + addedQuantity;
        validateStock(newQuantity, variant.getStock());

        item.setQuantity(newQuantity);
        return cartItemMapper.toDto(cartItemRepository.save(item), variant);
    }

    private CartItemResponse createNewCartItem(Cart cart, AddCartItemRequest request, Variant variant) {
        validateStock(request.quantity(), variant.getStock());

        CartItem newItem = CartItem.builder()
                .variantId(request.variantId())
                .productId(UuidUtils.parse(variant.getProductId()))
                .quantity(request.quantity())
                .cart(cart)
                .status(CartItemStatus.ACTIVE)
                .build();

        return cartItemMapper.toDto(cartItemRepository.save(newItem), variant);
    }

    private CartItem validateCartItemOwnershipActive(UUID customerId, UUID cartItemId) {
        Cart cart = cartService.findCartByCustomerId(customerId);

        CartItem cartItem = findActiveById(cartItemId);

        validateCartItemBelongsToCart(cart.getCartItems(), cartItem);
        return cartItem;
    }

    private CartItem validateCartItemOwnershipActiveOrOutOfStock(UUID customerId, UUID cartItemId) {
        Cart cart = cartService.findCartByCustomerId(customerId);

        CartItem cartItem = findActiveOrOutOfStockById(cartItemId);

        validateCartItemBelongsToCart(cart.getCartItems(), cartItem);
        return cartItem;
    }

    private CartItem findActiveById(UUID cartItemId) {
        return cartItemRepository.findByIdAndIsDeletedFalseAndStatusIn(cartItemId, List.of(CartItemStatus.ACTIVE))
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));
    }

    private CartItem findActiveOrOutOfStockById(UUID cartItemId) {
        return cartItemRepository.findByIdAndIsDeletedFalseAndStatusIn(
                cartItemId,
                        List.of(
                                CartItemStatus.ACTIVE,
                                CartItemStatus.OUT_OF_STOCK
                        )
                )
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));
    }

    private void validateCartItemBelongsToCart(List<CartItem> list, CartItem cartItem) {
        if (!list.contains(cartItem)) {
            throw new AppException(ErrorCode.CART_ITEM_NOT_BELONG_TO_CART);
        }
    }
}
