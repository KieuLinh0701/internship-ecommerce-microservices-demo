package com.teamsolution.common.tracing.context;

import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TraceContext {
    private final Tracer tracer;

    public String currentTraceId() {
        if (tracer.currentSpan() == null) return "no-trace";
        return tracer.currentSpan().context().traceId();
    }
}