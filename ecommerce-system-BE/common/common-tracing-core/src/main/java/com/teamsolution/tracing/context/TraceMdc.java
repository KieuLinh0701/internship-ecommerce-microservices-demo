package com.teamsolution.tracing.context;

import org.slf4j.MDC;

public final class TraceMdc {

    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";

    private TraceMdc() {
    }

    public static void put(String traceId, String spanId) {
        if (traceId != null) MDC.put(TRACE_ID, traceId);
        if (spanId != null) MDC.put(SPAN_ID, spanId);
    }

    public static void clear() {
        MDC.remove(TRACE_ID);
        MDC.remove(SPAN_ID);
    }
}
