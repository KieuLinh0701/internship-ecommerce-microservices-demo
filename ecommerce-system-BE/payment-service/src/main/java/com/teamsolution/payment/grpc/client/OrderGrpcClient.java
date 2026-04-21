package com.teamsolution.payment.grpc.client;

import com.teamsolution.common.core.enums.order.OrderPaymentStatus;
import com.teamsolution.common.grpc.constant.GrpcServiceName;
import com.teamsolution.grpc.client.config.properties.GrpcProperties;
import com.teamsolution.grpc.client.executor.GrpcExecutor;
import com.teamsolution.payment.dto.response.OrderForPaymentResponse;
import com.teamsolution.proto.grpc.order.GetOrderForPaymentRequest;
import com.teamsolution.proto.grpc.order.GetOrderForPaymentResponse;
import com.teamsolution.proto.grpc.order.OrderServiceGrpc;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OrderGrpcClient {

    private final GrpcProperties grpcProperties;
    private final GrpcExecutor grpcExecutor;

    @GrpcClient(GrpcServiceName.ORDER_SERVICE)
    private OrderServiceGrpc.OrderServiceBlockingStub stub;

    private OrderServiceGrpc.OrderServiceBlockingStub stubWithDeadline() {
        return stub.withDeadlineAfter(grpcProperties.getTimeout()
                .toMillis(), TimeUnit.MILLISECONDS);
    }

    public OrderForPaymentResponse GetOrderForPayment(UUID customerId, UUID orderId) {
        return grpcExecutor.execute(
                () -> {
                    GetOrderForPaymentRequest request =
                            GetOrderForPaymentRequest.newBuilder()
                                    .setCustomerId(customerId.toString())
                                    .setOrderId(orderId.toString())
                                    .build();

                    GetOrderForPaymentResponse response = stubWithDeadline()
                                    .getOrderForPayment(request);

                    return OrderForPaymentResponse.builder()
                            .paymentStatus(OrderPaymentStatus.from(response.getPaymentStatus()))
                            .total(response.getTotal())
                            .build();
                });
    }
}
