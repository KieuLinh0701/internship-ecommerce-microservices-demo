package com.teamsolution.common.jpa.exception;

import com.teamsolution.common.core.dto.common.response.ApiResponse;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class JpaExceptionHandler {

  @ExceptionHandler(OptimisticLockException.class)
  public ResponseEntity<ApiResponse<Void>> handleOptimisticLock(OptimisticLockException ex) {
    log.warn("Optimistic lock conflict occurred", ex);
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiResponse.failure("Request conflict, please try again"));
  }
}
