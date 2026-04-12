package com.teamsolution.common.tracing.utils;

import java.util.Random;

public class TraceUtils {
    private static final Random RANDOM = new Random();

    private TraceUtils() {}

    public static String generateSpanId() {
        return String.format("%016x", RANDOM.nextLong());
    }
}
