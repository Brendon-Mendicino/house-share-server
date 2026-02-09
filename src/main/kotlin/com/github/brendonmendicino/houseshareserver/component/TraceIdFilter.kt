package com.github.brendonmendicino.houseshareserver.component

import io.micrometer.tracing.Tracer
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Add Micrometer TraceId to current Http Headers.
 */
@Component
class TraceIdFilter(
    private val tracer: Tracer
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val traceId = tracer.currentTraceContext().context()?.traceId()
        if (traceId != null) {
            response.setHeader("X-Trace-Id", traceId)
        }
        filterChain.doFilter(request, response)
    }

}