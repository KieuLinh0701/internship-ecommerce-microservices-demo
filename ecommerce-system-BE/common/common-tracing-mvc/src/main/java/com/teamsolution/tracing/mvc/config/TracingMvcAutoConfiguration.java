package com.teamsolution.tracing.mvc.config;

import com.teamsolution.tracing.mvc.filter.TraceMdcFilter;
import io.micrometer.tracing.Tracer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.OncePerRequestFilter;

@AutoConfiguration
public class TracingMvcAutoConfiguration {

    @Bean
    @ConditionalOnClass(OncePerRequestFilter.class)
    public TraceMdcFilter traceMdcFilter(Tracer tracer) {
        return new TraceMdcFilter(tracer);
    }
}