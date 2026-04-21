package com.teamsolution.notification.grpc.client;

import com.teamsolution.common.core.util.UuidUtils;
import com.teamsolution.grpc.client.config.properties.GrpcProperties;
import com.teamsolution.grpc.client.executor.GrpcExecutor;
import com.teamsolution.proto.grpc.customer.CustomerServiceGrpc;
import com.teamsolution.proto.grpc.customer.GetAccountIdByCustomerIdRequest;
import com.teamsolution.proto.grpc.customer.GetAccountIdByCustomerIdResponse;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CustomerGrpcClient {
  private final GrpcProperties grpcProperties;
  private final GrpcExecutor grpcExecutor;

  @GrpcClient(com.teamsolution.common.grpc.constant.GrpcServiceName.CUSTOMER_SERVICE)
  private CustomerServiceGrpc.CustomerServiceBlockingStub stub;

  private CustomerServiceGrpc.CustomerServiceBlockingStub stubWithDeadline() {
    return stub.withDeadlineAfter(grpcProperties.getTimeout().toMillis(), TimeUnit.MILLISECONDS);
  }

  public UUID getAccountIdByCustomerId(UUID customerId) {

    return grpcExecutor.execute(
        () -> {
          GetAccountIdByCustomerIdRequest request =
              GetAccountIdByCustomerIdRequest.newBuilder()
                  .setCustomerId(customerId.toString())
                  .build();

          GetAccountIdByCustomerIdResponse response =
              stubWithDeadline().getAccountIdByCustomerId(request);

          return UuidUtils.parse(response.getAccountId());
        });
  }
}
