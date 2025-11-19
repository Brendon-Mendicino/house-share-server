package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.util.toB64Url
import org.springframework.stereotype.Service
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.security.SecureRandom
import java.time.Instant
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit

@Service
class SignedUrlService {
    companion object {
        private const val HMAC_ALGORITHM = "HmacSHA256"
        private val SECRET: ByteArray = generateRandom()

        private fun generateRandom(): ByteArray {
            //  256-bit key
            val secretKeyBytes = ByteArray(32)
            SecureRandom().nextBytes(secretKeyBytes)
            return secretKeyBytes
        }

        private val LIFETIME = 1.days
    }

    fun computeSignature(uri: URI): ByteArray {
        val mac = Mac.getInstance(HMAC_ALGORITHM)
        mac.init(SecretKeySpec(SECRET, HMAC_ALGORITHM))

        val signature = mac.doFinal(uri.toString().toByteArray(Charsets.UTF_8))

        return signature
    }

    /**
     * Creates a URI with a new set of query params:
     *
     * - `expires`: expiry time in unix epoch seconds
     * - `nonce`: nonce created on each generated URI
     * - `signature`: signature of the URI
     */
    fun signPath(path: String): URI {
        val expires = Instant.now().epochSecond + LIFETIME.toLong(DurationUnit.SECONDS)
        val nonce = generateRandom().toB64Url()

        val uriWithExpiry = UriComponentsBuilder.newInstance()
            .path(path)
            .queryParam("expires", expires)
            .queryParam("nonce", nonce)
            .build()
            .toUri()

        val signature = computeSignature(uriWithExpiry).toB64Url()

        return UriComponentsBuilder.fromUri(uriWithExpiry)
            .queryParam("signature", signature)
            .build()
            .toUri()
    }

    fun validateUri(uri: URI): Boolean {
        val uriComponents = UriComponentsBuilder.fromUri(uri).build()

        val expires = uriComponents.queryParams.getFirst("expires")?.toLongOrNull() ?: return false
        val signature = uriComponents.queryParams.getFirst("signature") ?: return false

        // Validate signature
        val uriWoSignature = UriComponentsBuilder.newInstance()
            .path(uri.path)
            .query(uri.query)
            .replaceQueryParam("signature")
            .build()
            .toUri()

        val computedSignature = computeSignature(uriWoSignature).toB64Url()

        if (computedSignature != signature) {
            return false
        }

        // Validate expiration
        val now = Instant.now().epochSecond

        return now <= expires
    }

    fun validCurrentUri(): Boolean {
        val uri = try {
            ServletUriComponentsBuilder.fromCurrentRequest().build().toUri()
        } catch (_: IllegalStateException) {
            return false
        }

        return validateUri(uri)
    }
}