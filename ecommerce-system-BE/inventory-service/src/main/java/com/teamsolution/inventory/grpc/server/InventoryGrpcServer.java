package com.teamsolution.inventory.grpc.server;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.util.UuidUtils;
import com.teamsolution.inventory.enums.ProductVariantStatus;
import com.teamsolution.inventory.exception.ErrorCode;
import com.teamsolution.inventory.mapper.VariantMapper;
import com.teamsolution.inventory.repository.ProductVariantRepository;
import com.teamsolution.proto.grpc.inventory.GetVariantsByIdsRequest;
import com.teamsolution.proto.grpc.inventory.GetVariantsByIdsResponse;
import com.teamsolution.proto.grpc.inventory.InventoryServiceGrpc;
import com.teamsolution.proto.grpc.inventory.Variant;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class InventoryGrpcServer
        extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final ProductVariantRepository productVariantRepository;
    private final VariantMapper variantMapper;

    @Override
    @Transactional(readOnly = true)
    public void getVariantsByIds(GetVariantsByIdsRequest request,
            StreamObserver<GetVariantsByIdsResponse> observer) {

        List<UUID> ids = request.getVariantIdsList()
                .stream()
                .map(UuidUtils::parse)
                .toList();

        List<Variant> protos = productVariantRepository
                .findAllByIdInAndIsDeletedFalseAndStatus(
                        ids,
                        ProductVariantStatus.ACTIVE
                )
                .stream()
                .map(variantMapper::toVariant)
                .toList();

        if (protos.size() != ids.size()) {
            throw new AppException(ErrorCode.PRODUCT_VARIANT_INVENTORY_NOT_FOUND);
        }

        observer.onNext(GetVariantsByIdsResponse.newBuilder()
                .addAllVariants(protos)
                .build());
        observer.onCompleted();
    }
}