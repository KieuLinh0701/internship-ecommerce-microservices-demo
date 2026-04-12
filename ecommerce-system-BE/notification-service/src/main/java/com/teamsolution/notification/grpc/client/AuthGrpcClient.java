package com.teamsolution.notification.grpc.client;

import com.teamsolution.common.core.util.UuidUtils;
import com.teamsolution.grpc.client.config.properties.GrpcProperties;
import com.teamsolution.grpc.client.executor.GrpcExecutor;
import com.teamsolution.proto.grpc.auth.AuthServiceGrpc;
import com.teamsolution.proto.grpc.auth.GetAccountRoleIdByAccountIdAndRoleCustomerRequest;
import com.teamsolution.proto.grpc.auth.GetAllAdminAccountIdsAndAccountRolesResponse;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthGrpcClient {

  private final GrpcProperties grpcProperties;
  private final GrpcExecutor grpcExecutor;

  @GrpcClient(com.teamsolution.common.grpc.constant.GrpcServiceName.AUTH_SERVICE)
  private AuthServiceGrpc.AuthServiceBlockingStub stub;

  private AuthServiceGrpc.AuthServiceBlockingStub stubWithDeadline() {
    return stub.withDeadlineAfter(grpcProperties.getTimeout().toMillis(), TimeUnit.MILLISECONDS);
  }

  public Map<UUID, UUID> getAllAdminAccountIdAndAccountRoleId() {

    return grpcExecutor.execute(
        () -> {
          GetAllAdminAccountIdsAndAccountRolesResponse response =
              stubWithDeadline()
                  .getAllAdminAccountIdsAndAccountRoles(
                      com.google.protobuf.Empty.getDefaultInstance());

          return response.getAccountIdAccountRoleIdMapMap().entrySet().stream()
              .collect(
                  Collectors.toMap(
                      e -> UuidUtils.parse(e.getKey()), e -> UuidUtils.parse(e.getValue())));
        });
  }

  public UUID getAccountRoleIdByAccountIdAndRoleCustomer(UUID accountId) {
    return grpcExecutor.execute(
        () -> {
          GetAccountRoleIdByAccountIdAndRoleCustomerRequest request =
              GetAccountRoleIdByAccountIdAndRoleCustomerRequest.newBuilder()
                  .setAccountId(accountId.toString())
                  .build();

          return UuidUtils.parse(
              stubWithDeadline()
                  .getAccountRoleIdByAccountIdAndRoleCustomer(request)
                  .getAccountRoleId());
        });
  }
}
