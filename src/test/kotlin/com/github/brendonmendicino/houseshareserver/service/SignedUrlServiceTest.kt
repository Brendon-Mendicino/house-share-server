package com.github.brendonmendicino.houseshareserver.service

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.net.URI
import java.time.Clock
import java.time.Duration

class SignedUrlServiceTest {
    val service = SignedUrlService()

    @AfterEach
    fun resetRequestContext() {
        RequestContextHolder.resetRequestAttributes()
    }

    private fun requestFor(uri: URI, contextPath: String) = MockHttpServletRequest().apply {
        this.contextPath = contextPath
        requestURI = contextPath + uri.rawPath
        queryString = uri.rawQuery
        uri.query.split("&").map { it.split("=", limit = 2) }.forEach { (k, v) -> addParameter(k, v) }
    }

    private fun setCurrentRequest(uri: URI, contextPath: String = "") =
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(requestFor(uri, contextPath)))

    @Test
    fun `A path is signed to a uri`() {
        val uri = service.signPath("/test/this").toUri()
        assertEquals("/test/this", uri.path)
    }

    @Test
    fun `Test the signed path validation`() {
        val signed = service.signPath("/test/this")

        assertTrue(service.validate(signed.path, signed.expires, signed.nonce, signed.signature))
        assertFalse(service.validate("/test/diff", signed.expires, signed.nonce, signed.signature))
        assertFalse(service.validate(signed.path, signed.expires + 1, signed.nonce, signed.signature))
        assertFalse(service.validate(signed.path, signed.expires, "other", signed.signature))
        assertFalse(service.validate(signed.path, signed.expires, signed.nonce, "not base64!"))
        assertFalse(service.validate(signed.path, null, signed.nonce, signed.signature))
    }

    @Test
    fun `Should reject an expired signature`() {
        val signed = service.signPath("/test/this")
        val later = SignedUrlService(Clock.offset(Clock.systemUTC(), Duration.ofDays(2)))

        assertTrue(service.validate(signed.path, signed.expires, signed.nonce, signed.signature))
        assertFalse(later.validate(signed.path, signed.expires, signed.nonce, signed.signature))
    }

    @Test
    fun `Should return false when there is no current uri`() {
        assertFalse {
            service.validCurrentUri()
        }
    }

    @Test
    fun `Should validate the current request without a context path`() {
        setCurrentRequest(service.signPath("/test/this").toUri())

        assertTrue(service.validCurrentUri())
    }

    @Test
    fun `Should validate the current request under a context path`() {
        setCurrentRequest(service.signPath("/test/this").toUri(), "/house-share")

        assertTrue(service.validCurrentUri())
    }

    @Test
    fun `Should validate a path that needs encoding`() {
        val uri = service.signPath("/test/with space/è").toUri()
        assertNotEquals(uri.path, uri.rawPath)

        setCurrentRequest(uri, "/house-share")

        assertTrue(service.validCurrentUri())
    }

    @Test
    fun `Should reject a tampered request under a context path`() {
        val signed = service.signPath("/test/this")
        setCurrentRequest(signed.copy(path = "/test/diff").toUri(), "/house-share")

        assertFalse(service.validCurrentUri())
    }
}
