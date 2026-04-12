package com.teamsolution.common.kafka.utils;

import com.teamsolution.common.tracing.utils.TraceUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.nio.charset.StandardCharsets;

public class KafkaProducerHelper {

    public static void addTraceHeader(ProducerRecord<?, ?> record, String traceId) {
        if (traceId != null) {
            String traceParent = "00-" + traceId + "-" + TraceUtils.generateSpanId() + "-01";

            record.headers().remove("traceparent");

            record.headers().add("traceparent", traceParent.getBytes(StandardCharsets.UTF_8));
        }
    }
}