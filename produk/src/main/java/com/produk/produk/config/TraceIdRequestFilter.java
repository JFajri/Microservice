package com.produk.produk.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TraceIdRequestFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    private static final Logger log =
            LoggerFactory.getLogger(TraceIdRequestFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String traceId = request.getHeader(TRACE_ID_HEADER);

        System.out.println("========== TRACE DEBUG ==========");
        System.out.println("Path     : " + request.getRequestURI());
        System.out.println("Trace ID : " + traceId);
        System.out.println("===============================");

        if (traceId != null && !traceId.isBlank()) {
            MDC.put("traceId", traceId);

            log.info("Trace ID berhasil dimasukkan ke MDC");
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("traceId");
        }
    }
}