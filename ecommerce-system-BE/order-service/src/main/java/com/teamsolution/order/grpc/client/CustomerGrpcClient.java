package com.teamsolution.order.grpc.client;

import com.teamsolution.common.grpc.constant.GrpcServiceName;
import com.teamsolution.grpc.client.config.properties.GrpcProperties;
import com.teamsolution.grpc.client.executor.GrpcExecutor;
import com.teamsolution.proto.grpc.customer.CustomerServiceGrpc;
import com.teamsolution.proto.grpc.customer.GetAddressByCustomerIdAndAddressIdRequest;
import com.teamsolution.proto.grpc.customer.GetAddressByCustomerIdAndAddressIdResponse;
import com.teamsolution.proto.grpc.customer.GetCustomerNameByCustomerIdRequest;
import com.teamsolution.proto.grpc.customer.GetCustomerNameByCustomerIdResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerGrpcClient {

  private final GrpcProperties grpcProperties;
  private final GrpcExecutor grpcExecutor;

  @GrpcClient(GrpcServiceName.CUSTOMER_SERVICE)
  private CustomerServiceGrpc.CustomerServiceBlockingStub stub;

  private CustomerServiceGrpc.CustomerServiceBlockingStub stub() {
    return stub.withDeadlineAfter(grpcProperties.getTimeout().toMillis(), TimeUnit.MILLISECONDS);
  }

  public String getCustomerNameByCustomerId(UUID customerId) {

    return grpcExecutor.execute(
        () -> {
          GetCustomerNameByCustomerIdRequest request =
              GetCustomerNameByCustomerIdRequest.newBuilder()
                  .setCustomerId(customerId.toString())
                  .build();

          GetCustomerNameByCustomerIdResponse response =
              stub().getCustomerNameByCustomerId(request);

          return response.getName();
        });
  }

  public GetAddressByCustomerIdAndAddressIdResponse getAddressByCustomerIdAndAddressId(
      UUID addressId, UUID customerId) {

    return grpcExecutor.execute(
        () -> {
          GetAddressByCustomerIdAndAddressIdRequest request =
              GetAddressByCustomerIdAndAddressIdRequest.newBuilder()
                  .setCustomerId(customerId.toString())
                  .setAddressId(addressId.toString())
                  .build();

          return stub().getAddressByCustomerIdAndAddressId(request);
        });
  }
}
