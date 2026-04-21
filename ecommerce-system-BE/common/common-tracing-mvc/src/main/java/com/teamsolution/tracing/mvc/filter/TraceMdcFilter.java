package com.teamsolution.tracing.mvc.filter;

import com.teamsolution.tracing.context.TraceMdc;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class TraceMdcFilter
        extends OncePerRequestFilter {

    private final Tracer tracer;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {
            var span = tracer.currentSpan();

            if (span != null) {
                TraceMdc.put(
                        span.context()
                                .traceId(),
                        span.context()
                                .spanId()
                );
            }

            filterChain.doFilter(request, response);
        } finally {
            TraceMdc.clear();
        }
    }
}