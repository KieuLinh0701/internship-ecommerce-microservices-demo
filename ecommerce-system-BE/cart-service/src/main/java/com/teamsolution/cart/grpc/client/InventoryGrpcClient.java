package com.teamsolution.cart.grpc.client;

import com.teamsolution.common.grpc.constant.GrpcServiceName;
import com.teamsolution.grpc.client.config.properties.GrpcProperties;
import com.teamsolution.grpc.client.executor.GrpcExecutor;
import com.teamsolution.proto.grpc.inventory.GetVariantsByIdsRequest;
import com.teamsolution.proto.grpc.inventory.GetVariantsByIdsResponse;
import com.teamsolution.proto.grpc.inventory.InventoryServiceGrpc;
import com.teamsolution.proto.grpc.inventory.Variant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryGrpcClient {
    private final GrpcProperties grpcProperties;

    private final GrpcExecutor grpcExecutor;

    @GrpcClient(GrpcServiceName.INVENTORY_SERVICE)
    private InventoryServiceGrpc.InventoryServiceBlockingStub stub;

    private InventoryServiceGrpc.InventoryServiceBlockingStub stub() {
        return stub.withDeadlineAfter(grpcProperties.getTimeout()
                .toMillis(), TimeUnit.MILLISECONDS);
    }

    public List<Variant> getVariantsByIds(List<String> variantIds) {
        return grpcExecutor.execute(
                () -> {
                    GetVariantsByIdsRequest request = GetVariantsByIdsRequest.newBuilder()
                            .addAllVariantIds(variantIds)
                            .build();

                    GetVariantsByIdsResponse response = stub().getVariantsByIds(request);
                    return response.getVariantsList();
                });
    }
}