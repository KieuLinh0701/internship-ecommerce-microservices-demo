package com.teamsolution.grpc.server.config;

import com.teamsolution.grpc.server.exception.GlobalGrpcExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class GrpcServerAutoConfiguration {

    @Bean
    public GlobalGrpcExceptionHandler globalGrpcExceptionHandler() {
        return new GlobalGrpcExceptionHandler();
    }
}