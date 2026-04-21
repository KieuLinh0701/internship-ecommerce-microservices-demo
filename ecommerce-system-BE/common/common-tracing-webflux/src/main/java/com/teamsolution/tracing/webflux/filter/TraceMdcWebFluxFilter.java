package com.teamsolution.tracing.webflux.filter;

import com.teamsolution.tracing.context.TraceMdc;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TraceMdcWebFluxFilter implements GlobalFilter, Ordered {

    private final Tracer tracer;

    @Override
    public Mono<Void> filter(org.springframework.web.server.ServerWebExchange exchange,
            org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        var span = tracer.currentSpan();

        if (span != null) {
            TraceMdc.put(
                    span.context().traceId(),
                    span.context().spanId()
            );
        }

        return chain.filter(exchange)
                .doFinally(signalType -> TraceMdc.clear());
    }

    @Override
    public int getOrder() {
        return -1;
    }
}