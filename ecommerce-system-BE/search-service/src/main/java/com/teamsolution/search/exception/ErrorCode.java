package com.teamsolution.search.exception;

import com.teamsolution.common.core.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseErrorCode {
  PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product not found"),
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
