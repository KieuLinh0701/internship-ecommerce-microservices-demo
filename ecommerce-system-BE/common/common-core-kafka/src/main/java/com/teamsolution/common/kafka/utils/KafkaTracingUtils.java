package com.teamsolution.common.kafka.utils;

import io.opentelemetry.context.propagation.TextMapGetter;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;

public class KafkaTracingUtils {
    private KafkaTracingUtils() {}

    public static final TextMapGetter<Headers> KAFKA_GETTER = new TextMapGetter<Headers>() {
        @Override
        public Iterable<String> keys(Headers headers) {
            return () -> Arrays.stream(headers.toArray()).map(Header::key).iterator();
        }

        @Override
        public String get(Headers headers, String key) {
            if ("traceparent".equals(key)) {
                Iterator<Header> iterator = headers.headers(key).iterator();
                if (iterator.hasNext()) {
                    return new String(iterator.next().value(), StandardCharsets.UTF_8);
                }
            }
            Header h = headers.lastHeader(key);
            return h != null ? new String(h.value(), StandardCharsets.UTF_8) : null;
        }
    };
}