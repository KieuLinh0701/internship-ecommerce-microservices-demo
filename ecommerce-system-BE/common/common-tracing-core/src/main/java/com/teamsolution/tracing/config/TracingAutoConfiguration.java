package com.teamsolution.tracing.config;

import com.teamsolution.tracing.context.TraceContext;
import io.micrometer.tracing.Tracer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.OncePerRequestFilter;

@AutoConfiguration
public class TracingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TraceContext traceContext(Tracer tracer) {
        return new TraceContext(tracer);
    }

}