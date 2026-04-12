package com.teamsolution.grpc.client.executor;

import com.teamsolution.grpc.client.mapper.GrpcErrorMapper;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GrpcExecutor {

  private final GrpcErrorMapper errorMapper;
  private final GrpcRetryExecutor retryExecutor;

  public <T> T execute(Callable<T> callable) {
    return retryExecutor.execute(
        () -> {
          try {
            return callable.call();
          } catch (StatusRuntimeException e) {
              log.error(
                "gRPC call failed, status={}, description={}",
                e.getStatus().getCode(),
                e.getStatus().getDescription(),
                e);

            throw errorMapper.map(e);
          }
        });
  }
}
