package com.teamsolution.common.core.exception.enums;

import com.teamsolution.common.core.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements BaseErrorCode {

  // System
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong"),
  VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation error"),
  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),
  INVALID_UUID(HttpStatus.BAD_REQUEST, "Invalid UUID"),
  DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Database error: %s"),
  INVALID_ENUM_VALUE(HttpStatus.BAD_REQUEST, "Invalid enum value"),
  UNEXPECTED_ENUM_VALUE(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected enum value"),

  // Event
  UNKNOWN_EVENT_TYPE(HttpStatus.NOT_FOUND, "Unknow event type"),

  // Failed Event
  FAILED_EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Failed event not found"),
  FAILED_EVENT_ALREADY_SUCCESS(
      HttpStatus.BAD_REQUEST, "Event has already been processed successfully"),
  FAILED_EVENT_ALREADY_DEAD(
      HttpStatus.BAD_REQUEST, "Event is already in DEAD state and no further actions are allowed"),
  FAILED_EVENT_IS_RETRYING(HttpStatus.BAD_REQUEST, "Event is currently being retried"),
  OUTBOX_EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Outbox event not found"),
  FAILED_EVENT_NOT_RETRYABLE(
      HttpStatus.BAD_REQUEST, "This failed event is not eligible for retry anymore"),

  // gRPC
  GRPC_SERVICE_NOT_FOUND(HttpStatus.SERVICE_UNAVAILABLE, "Service unavailable"),
  GRPC_NO_INSTANCE_AVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Service unavailable"),
  GRPC_METADATA_MISSING(HttpStatus.SERVICE_UNAVAILABLE, "Service unavailable"),

  SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Service temporarily unavailable"),
  DEADLINE_EXCEEDED(HttpStatus.GATEWAY_TIMEOUT, "Request timed out"),
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request"),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized"),
  FORBIDDEN(HttpStatus.FORBIDDEN, "Forbidden"),

  // JsonUtils
  JSON_SERIALIZATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to serialize object to JSON"),
  JSON_DESERIALIZATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to deserialize JSON"),
  ;

  private final HttpStatus httpStatus;
  private final String message;

  public String getCode() {
    return this.name();
  }
}
