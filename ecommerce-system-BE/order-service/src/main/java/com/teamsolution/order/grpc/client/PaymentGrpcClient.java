package com.teamsolution.order.grpc.client;

import com.teamsolution.common.core.enums.order.OrderPaymentMethod;
import com.teamsolution.common.grpc.constant.GrpcServiceName;
import com.teamsolution.grpc.client.config.properties.GrpcProperties;
import com.teamsolution.grpc.client.executor.GrpcExecutor;
import com.teamsolution.proto.grpc.payment.CreatePaymentRequest;
import com.teamsolution.proto.grpc.payment.CreatePaymentResponse;
import com.teamsolution.proto.grpc.payment.PaymentServiceGrpc;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentGrpcClient {

  private final GrpcProperties grpcProperties;
  private final GrpcExecutor grpcExecutor;

  @GrpcClient(GrpcServiceName.PAYMENT_SERVICE)
  private PaymentServiceGrpc.PaymentServiceBlockingStub stub;

  private PaymentServiceGrpc.PaymentServiceBlockingStub stub() {
    return stub.withDeadlineAfter(grpcProperties.getTimeout().toMillis(), TimeUnit.MILLISECONDS);
  }

  public String createPayment(
      UUID orderId, Long amount, OrderPaymentMethod method, String clientIp, UUID customerId) {
    return grpcExecutor.execute(
        () -> {
          CreatePaymentRequest request =
              CreatePaymentRequest.newBuilder()
                  .setOrderId(orderId.toString())
                  .setAmount(amount)
                  .setMethod(method.name())
                  .setClientIp(clientIp)
                  .setCustomerId(customerId.toString())
                  .build();

          CreatePaymentResponse response = stub().createPayment(request);
          return response.getPaymentUrl();
        });
  }
}
