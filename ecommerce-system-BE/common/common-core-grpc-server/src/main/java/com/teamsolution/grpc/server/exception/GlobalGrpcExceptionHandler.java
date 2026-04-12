package com.teamsolution.grpc.server.exception;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.BaseErrorCode;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@Slf4j
@GrpcAdvice
public class GlobalGrpcExceptionHandler {

    @GrpcExceptionHandler(AppException.class)
    public StatusRuntimeException handleAppException(AppException ex) {

        Status status = toGrpcStatus(ex.getErrorCode());

        return status.withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

    @GrpcExceptionHandler(Exception.class)
    public StatusRuntimeException handleException(Exception ex) {

        log.error("Unhandled gRPC exception", ex);

        return Status.INTERNAL
                .withDescription(CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

    private Status toGrpcStatus(BaseErrorCode code) {
        return switch (code.getHttpStatus()) {
            case BAD_REQUEST -> Status.INVALID_ARGUMENT;
            case UNAUTHORIZED -> Status.UNAUTHENTICATED;
            case FORBIDDEN -> Status.PERMISSION_DENIED;
            case NOT_FOUND -> Status.NOT_FOUND;
            case CONFLICT -> Status.ALREADY_EXISTS;
            case TOO_MANY_REQUESTS -> Status.RESOURCE_EXHAUSTED;
            case GATEWAY_TIMEOUT -> Status.DEADLINE_EXCEEDED;
            case SERVICE_UNAVAILABLE -> Status.UNAVAILABLE;
            default -> Status.INTERNAL;
        };
    }
}
