package com.teamsolution.cart.grpc.server;

import com.teamsolution.cart.entity.Cart;
import com.teamsolution.cart.entity.CartItem;
import com.teamsolution.cart.exception.ErrorCode;
import com.teamsolution.cart.service.internal.CartInternalService;
import com.teamsolution.cart.service.internal.CartItemInternalService;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.util.UuidUtils;
import com.teamsolution.proto.grpc.cart.CartServiceGrpc;
import com.teamsolution.proto.grpc.cart.GetCartItemByIdsAndCustomerIdRequest;
import com.teamsolution.proto.grpc.cart.GetCartItemByIdsAndCustomerIdResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class CartGrpcService
        extends CartServiceGrpc.CartServiceImplBase {

    private final CartInternalService cartInternalService;
    private final CartItemInternalService cartItemInternalService;

    @Transactional(readOnly = true)
    @Override
    public void getCartItemByIdsAndCustomerId(GetCartItemByIdsAndCustomerIdRequest request,
            StreamObserver<GetCartItemByIdsAndCustomerIdResponse> observer) {
        UUID customerId = UuidUtils.parse(request.getCustomerId());

        Cart cart = cartInternalService.findCartByCustomerId(customerId);

        List<UUID> cartItemIds = request.getCartItemIdsList()
                .stream()
                .map(UuidUtils::parse)
                .toList();

        List<CartItem> cartItems = cartItemInternalService.findByIdsAndCartId(cartItemIds, cart.getId());

        if (cartItems.size() != cartItemIds.size()) {
            throw new AppException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        List<com.teamsolution.proto.grpc.cart.CartItem> protos = cartItems.stream()
                .map(item -> com.teamsolution.proto.grpc.cart.CartItem.newBuilder()
                        .setVariantId(item.getVariantId().toString())
                        .setQuantity(item.getQuantity())
                        .build())
                .toList();

        observer.onNext(GetCartItemByIdsAndCustomerIdResponse.newBuilder()
                .addAllItems(protos)
                .build());
        observer.onCompleted();
    }
}