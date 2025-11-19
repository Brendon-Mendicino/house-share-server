package com.github.brendonmendicino.houseshareserver.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.web.util.UriComponentsBuilder

class SignedUrlServiceTest {
    val service = SignedUrlService()

    @Test
    fun `A path is signed to a uri`() {
        val uri = service.signPath("/test/this")
        assertEquals(uri.path, "/test/this")
    }

    @Test
    fun `Test the uri validation`() {
        val uri = service.signPath("/test/this")
        val modified = UriComponentsBuilder.fromUri(uri).path("/test/diff").build().toUri()

        assertTrue(service.validateUri(uri))
        assertFalse(service.validateUri(modified))
    }

    @Test
    fun `Should return false when there is no current uri`() {
        assertFalse {
            service.validCurrentUri()
        }
    }
}