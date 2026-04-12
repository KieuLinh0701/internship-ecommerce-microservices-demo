package com.teamsolution.customer.exception;

import com.teamsolution.common.core.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseErrorCode {

  // address
  ADDRESS_DEFAULT_CANNOT_DELETE(HttpStatus.BAD_REQUEST, "Cannot delete the only default address"),
  ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "Address not found"),
  ADDRESS_ACCESS_DENIED(HttpStatus.FORBIDDEN, "You do not have permission to access this address"),
  ADDRESS_ALREADY_DEFAULT(HttpStatus.BAD_REQUEST, "This address is already the default address"),

  // customer
  CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "Customer not found"),
  CUSTOMER_ACCOUNT_ALREADY_EXISTS(HttpStatus.CONFLICT, "Customer already exists for this account"),
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
