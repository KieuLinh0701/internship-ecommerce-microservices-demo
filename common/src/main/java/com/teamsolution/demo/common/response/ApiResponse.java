package com.teamsolution.demo.common.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
  private boolean success;
  private String message;
  private T data;
  private LocalDateTime timestamp;

  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<T>(true, message, data, LocalDateTime.now());
  }

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<T>(true, "Success", data, LocalDateTime.now());
  }

  public static <T> ApiResponse<T> failure(String message) {
    return new ApiResponse<T>(false, message, null, LocalDateTime.now());
  }
}
