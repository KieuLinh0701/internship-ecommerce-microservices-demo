package com.teamsolution.grpc.client.executor;

import com.teamsolution.common.core.exception.TemporaryException;
import com.teamsolution.grpc.client.config.properties.GrpcProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GrpcRetryExecutor {

  private final GrpcProperties grpcProperties;

  public <T> T execute(Callable<T> callable) {

    int attempt = 0;

    while (true) {
      try {
        // actual execution (gRPC call happens here)
        return callable.call();
      } catch (TemporaryException ex) {
        // retry only for temporary errors (network, server unavailable)

        attempt++;

        // stop retry when reaching max attempts
        if (attempt >= grpcProperties.getRetry().getMaxAttempts()) {
            log.error("Retry failed after {} attempts", attempt);
          throw ex;
        }

        // exponential backoff
        long delay = (long) Math.pow(2, attempt) * grpcProperties.getRetry().getBaseDelayMs();

          log.warn("Retrying... attempt={}, delay={}ms", attempt, delay);

        // wait before next retry
        sleep(delay);
      } catch (RuntimeException ex) {
        // do not retry (PermanentException, AppException, business errors)
        throw ex;
      } catch (Exception ex) {
        // fallback for checked exceptions
        throw new RuntimeException(ex);
      }
    }
  }

  private void sleep(long delay) {
    try {
      Thread.sleep(delay);
    } catch (InterruptedException ignored) {
      // ignore interruption
    }
  }
}
