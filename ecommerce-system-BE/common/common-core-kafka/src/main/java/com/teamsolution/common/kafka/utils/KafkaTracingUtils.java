package com.teamsolution.common.kafka.utils;

import com.teamsolution.tracing.context.TraceMdc;
import com.teamsolution.tracing.utils.TraceUtils;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;

public final class KafkaTracingUtils {

    private static final String TRACEPARENT_HEADER  = "traceparent";
    private static final String TRACEPARENT_VERSION = "00";
    private static final String TRACEPARENT_FLAGS   = "01";

    public static final TextMapGetter<Headers> KAFKA_GETTER = new TextMapGetter<>() {
        @Override
        public Iterable<String> keys(Headers headers) {
            return () -> Arrays.stream(headers.toArray()).map(Header::key).iterator();
        }

        @Override
        public String get(Headers headers, String key) {
            if (TRACEPARENT_HEADER.equals(key)) {
                Iterator<Header> iterator = headers.headers(key).iterator();
                if (iterator.hasNext()) {
                    return new String(iterator.next().value(), StandardCharsets.UTF_8);
                }
            }
            Header h = headers.lastHeader(key);
            return h != null ? new String(h.value(), StandardCharsets.UTF_8) : null;
        }
    };

    private KafkaTracingUtils() {}

    public static void addTraceHeader(ProducerRecord<?, ?> record, String traceId) {
        if (traceId == null) return;

        String traceParent = String.join("-",
                TRACEPARENT_VERSION,
                traceId,
                TraceUtils.generateSpanId(),
                TRACEPARENT_FLAGS);

        record.headers().remove(TRACEPARENT_HEADER);
        record.headers().add(TRACEPARENT_HEADER, traceParent.getBytes(StandardCharsets.UTF_8));
    }

    public static <K, V> void run(
            OpenTelemetry openTelemetry,
            ConsumerRecord<K, V> record,
            Runnable task) {

        Context ctx = openTelemetry.getPropagators()
                .getTextMapPropagator()
                .extract(Context.current(), record.headers(), KAFKA_GETTER);

        try (Scope scope = ctx.makeCurrent()) {
            Span currentSpan = Span.current();

            if (currentSpan.getSpanContext().isValid()) {
                TraceMdc.put(
                        currentSpan.getSpanContext().getTraceId(),
                        currentSpan.getSpanContext().getSpanId()
                );
            }

            task.run();

        } finally {
            TraceMdc.clear();
        }
    }
}