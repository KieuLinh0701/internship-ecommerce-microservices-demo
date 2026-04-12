package com.teamsolution.common.grpc.exception;

import io.grpc.Status;

public interface GrpcErrorCode {

  Status getGrpcStatus();
}
