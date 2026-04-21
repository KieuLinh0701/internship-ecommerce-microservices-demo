package com.teamsolution.order.grpc.client;

import com.teamsolution.common.grpc.constant.GrpcServiceName;
import com.teamsolution.grpc.client.config.properties.GrpcProperties;
import com.teamsolution.grpc.client.executor.GrpcExecutor;
import com.teamsolution.proto.grpc.cart.CartItem;
import com.teamsolution.proto.grpc.cart.CartServiceGrpc;
import com.teamsolution.proto.grpc.cart.GetCartItemByIdsAndCustomerIdRequest;
import com.teamsolution.proto.grpc.cart.GetCartItemByIdsAndCustomerIdResponse;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartGrpcClient {

  private final GrpcProperties grpcProperties;
  private final GrpcExecutor grpcExecutor;

  @GrpcClient(GrpcServiceName.CART_SERVICE)
  private CartServiceGrpc.CartServiceBlockingStub stub;

  private CartServiceGrpc.CartServiceBlockingStub stub() {
    return stub.withDeadlineAfter(grpcProperties.getTimeout().toMillis(), TimeUnit.MILLISECONDS);
  }

  public List<CartItem> getCartItemsByIdsAndCustomerId(List<UUID> cartItemIds, UUID customerId) {
    return grpcExecutor.execute(
        () -> {
          List<String> cartItemStrList = cartItemIds.stream().map(UUID::toString).toList();

          GetCartItemByIdsAndCustomerIdRequest request =
              GetCartItemByIdsAndCustomerIdRequest.newBuilder()
                  .setCustomerId(customerId.toString())
                  .addAllCartItemIds(cartItemStrList)
                  .build();

          GetCartItemByIdsAndCustomerIdResponse response =
              stub().getCartItemByIdsAndCustomerId(request);
          return response.getItemsList();
        });
  }
}
