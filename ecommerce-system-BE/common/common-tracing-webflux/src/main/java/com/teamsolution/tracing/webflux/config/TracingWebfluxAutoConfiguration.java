package com.teamsolution.tracing.webflux.config;

import com.teamsolution.tracing.webflux.filter.TraceMdcWebFluxFilter;
import io.micrometer.tracing.Tracer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class TracingWebfluxAutoConfiguration {
    @Bean
    @ConditionalOnClass(GlobalFilter.class)
    public TraceMdcWebFluxFilter traceMdcWebFluxFilter(Tracer tracer) {
        return new TraceMdcWebFluxFilter(tracer);
    }
}