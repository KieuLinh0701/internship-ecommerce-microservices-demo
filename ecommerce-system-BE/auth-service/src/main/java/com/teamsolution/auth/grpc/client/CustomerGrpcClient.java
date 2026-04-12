package com.teamsolution.auth.grpc.client;

import com.teamsolution.common.core.util.UuidUtils;
import com.teamsolution.common.grpc.constant.GrpcServiceName;
import com.teamsolution.grpc.client.config.properties.GrpcProperties;
import com.teamsolution.grpc.client.executor.GrpcExecutor;
import com.teamsolution.proto.grpc.customer.CreateCustomerRequest;
import com.teamsolution.proto.grpc.customer.CustomerServiceGrpc;
import com.teamsolution.proto.grpc.customer.GetCustomerIdByAccountIdRequest;
import com.teamsolution.proto.grpc.customer.GetCustomerIdByAccountIdResponse;
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

    @GrpcClient(GrpcServiceName.CUSTOMER_SERVICE)
    private CustomerServiceGrpc.CustomerServiceBlockingStub stub;

    private CustomerServiceGrpc.CustomerServiceBlockingStub stub() {
        return stub.withDeadlineAfter(grpcProperties.getTimeout()
                .toMillis(), TimeUnit.MILLISECONDS);
    }

    public void createNewCustomer(UUID accountId, String fullName, String phone, String avatarUrl) {

        grpcExecutor.execute(
                () -> {
                    CreateCustomerRequest request =
                            CreateCustomerRequest.newBuilder()
                                    .setAccountId(accountId.toString())
                                    .setFullName(fullName)
                                    .setPhone(phone)
                                    .setAvatarUrl(avatarUrl)
                                    .build();

                    stub().createCustomer(request);

                    return null;
                });
    }

    public UUID getCustomerIdByAccountId(UUID accountId) {

        return grpcExecutor.execute(
                () -> {
                    GetCustomerIdByAccountIdRequest request =
                            GetCustomerIdByAccountIdRequest.newBuilder()
                                    .setAccountId(accountId.toString())
                                    .build();

                    GetCustomerIdByAccountIdResponse response = stub().getCustomerIdByAccountId(request);

                    return UuidUtils.parse(response.getCustomerId());
                });
    }
}
