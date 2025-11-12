package com.github.brendonmendicino.houseshareserver.component

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor


@Component // Registers this class as a Spring component
class LoggingInterceptor : HandlerInterceptor {
    companion object {
        // Logger for this class
        private val logger = LoggerFactory.getLogger(LoggingInterceptor::class.java)
    }

    // Logs HTTP method and URI before the request is handled
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        logger.info("Request: {} {}", request.method, request.requestURI)
        return true // Allows the request to proceed
    }

    // Logs response status and URI after request completion. Logs exceptions if any
    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        logger.info("Response: {} {}", response.status, request.requestURI)
        if (ex != null) {
            // Logs any exception
            logger.error("Exception: ", ex)
        }
    }
}

