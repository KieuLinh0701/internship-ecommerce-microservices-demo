package com.teamsolution.grpc.client.config;

import com.teamsolution.grpc.client.config.properties.GrpcProperties;
import com.teamsolution.grpc.client.executor.GrpcExecutor;
import com.teamsolution.grpc.client.executor.GrpcRetryExecutor;
import com.teamsolution.grpc.client.mapper.GrpcErrorMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(GrpcProperties.class)
public class GrpcClientAutoConfiguration {

    @Bean
    public GrpcErrorMapper grpcErrorMapper() {
        return new GrpcErrorMapper();
    }

    @Bean
    public GrpcRetryExecutor grpcRetryExecutor(GrpcProperties grpcProperties) {
        return new GrpcRetryExecutor(grpcProperties);
    }

    @Bean
    public GrpcExecutor grpcExecutor(GrpcErrorMapper grpcErrorMapper, GrpcRetryExecutor grpcRetryExecutor) {
        return new GrpcExecutor(grpcErrorMapper, grpcRetryExecutor);
    }
}
