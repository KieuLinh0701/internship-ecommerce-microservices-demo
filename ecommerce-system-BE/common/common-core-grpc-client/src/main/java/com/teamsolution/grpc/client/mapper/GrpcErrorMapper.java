package com.teamsolution.grpc.client.mapper;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.PermanentException;
import com.teamsolution.common.core.exception.TemporaryException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import io.grpc.StatusRuntimeException;

public class GrpcErrorMapper {

  public AppException map(StatusRuntimeException e) {

    String grpcMessage = e.getStatus().getDescription();

    return switch (e.getStatus().getCode()) {
      case UNAVAILABLE -> new TemporaryException(CommonErrorCode.SERVICE_UNAVAILABLE, grpcMessage);

      case DEADLINE_EXCEEDED ->
          new TemporaryException(CommonErrorCode.DEADLINE_EXCEEDED, grpcMessage);

      case NOT_FOUND -> new PermanentException(CommonErrorCode.RESOURCE_NOT_FOUND, grpcMessage);

      case INVALID_ARGUMENT -> new PermanentException(CommonErrorCode.BAD_REQUEST, grpcMessage);

      case UNAUTHENTICATED -> new PermanentException(CommonErrorCode.UNAUTHORIZED, grpcMessage);

      case PERMISSION_DENIED -> new PermanentException(CommonErrorCode.FORBIDDEN, grpcMessage);

      default -> new TemporaryException(CommonErrorCode.INTERNAL_SERVER_ERROR, grpcMessage);
    };
  }
}
