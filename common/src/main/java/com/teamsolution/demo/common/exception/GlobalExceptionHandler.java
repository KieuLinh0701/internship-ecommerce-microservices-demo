package com.teamsolution.demo.common.exception;

import com.teamsolution.demo.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ResourceNotFoundException.class,
            NoRecordsFoundException.class
    })
    public ResponseEntity<ApiResponse> handleNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure(
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleOtherExceptions(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure(
                        "An unexpected error occurred: " + ex.getMessage()
                ));
    }
}
