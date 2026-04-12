package com.teamsolution.common.tracing.config;

import com.teamsolution.common.tracing.context.TraceContext;
import io.micrometer.tracing.Tracer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class TracingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TraceContext traceContext(Tracer tracer) {
        return new TraceContext(tracer);
    }
}