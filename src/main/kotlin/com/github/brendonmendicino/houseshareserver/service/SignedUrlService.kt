package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.util.toB64Url
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.util.UriUtils
import java.net.URI
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.Clock
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit

/**
 * A context-relative [path] signed by [SignedUrlService.signPath].
 */
data class SignedPath(
    val path: String,
    val expires: Long,
    val nonce: String,
    val signature: String,
) {
    /**
     * Appends the path and the signing query params to [builder], e.g. a builder
     * already holding the scheme, host and context path of the current request.
     */
    fun applyTo(builder: UriComponentsBuilder): UriComponentsBuilder = builder
        .path(path)
        .queryParam(SignedUrlService.EXPIRES_QUERY, expires)
        .queryParam(SignedUrlService.NONCE_QUERY, nonce)
        .queryParam(SignedUrlService.SIGNATURE_QUERY, signature)

    /**
     * Context-relative URI.
     */
    fun toUri(): URI = applyTo(UriComponentsBuilder.newInstance()).encode().build().toUri()
}

@Service
class SignedUrlService(private val clock: Clock = Clock.systemUTC()) {
    companion object {
        private val logger = LoggerFactory.getLogger(SignedUrlService::class.java)

        // Query parameters
        const val SIGNATURE_QUERY = "signature"
        const val NONCE_QUERY = "nonce"
        const val EXPIRES_QUERY = "expires"

        private const val HMAC_ALGORITHM = "HmacSHA256"
        private val LIFETIME = 1.days
        private val SECRET: ByteArray = generateRandom()

        private fun generateRandom(): ByteArray {
            //  256-bit key
            val secretKeyBytes = ByteArray(32)
            SecureRandom().nextBytes(secretKeyBytes)
            return secretKeyBytes
        }
    }

    /**
     * Signs the canonical string `path \n expires \n nonce`, so the signature
     * does not depend on how the URL is encoded or where the app is mounted.
     */
    private fun computeSignature(path: String, expires: Long, nonce: String): ByteArray {
        val mac = Mac.getInstance(HMAC_ALGORITHM)
        mac.init(SecretKeySpec(SECRET, HMAC_ALGORITHM))

        val merged = listOf(path, expires, nonce)
            .map { it.toString().toByteArray(Charsets.UTF_8) }
            .map { Base64.getEncoder().encode(it) }
            .joinToString(".")

        return mac.doFinal(merged.toByteArray(Charsets.UTF_8))
    }

    /**
     * Signs a [path] with:
     *
     * - `expires`: expiry time in unix epoch seconds
     * - `nonce`: nonce created on each generated URI
     * - `signature`: signature of path, expires and nonce
     *
     * [path] must be relative to the servlet context path and not URL-encoded.
     */
    fun signPath(path: String): SignedPath {
        val expires = clock.instant().epochSecond + LIFETIME.toLong(DurationUnit.SECONDS)
        val nonce = generateRandom().toB64Url()
        val signature = computeSignature(path, expires, nonce).toB64Url()

        return SignedPath(path, expires, nonce, signature)
    }

    fun validate(path: String, expires: Long?, nonce: String?, signature: String?): Boolean {
        if (expires == null || nonce == null || signature == null) {
            logger.debug("Invalid signed path {}: missing or invalid signing params", path)
            return false
        }

        val provided = try {
            Base64.getUrlDecoder().decode(signature)
        } catch (_: IllegalArgumentException) {
            logger.debug("Invalid signed path {}: malformed signature", path)
            return false
        }

        if (!MessageDigest.isEqual(computeSignature(path, expires, nonce), provided)) {
            logger.debug("Invalid signed path {}: signature mismatch", path)
            return false
        }

        val now = clock.instant().epochSecond
        if (now > expires) {
            logger.debug("Invalid signed path {}: expired at {}, now {}", path, expires, now)
            return false
        }

        return true
    }

    /**
     * Validates [request] against its decoded, context-relative path, so that
     * signatures created by [signPath] remain valid regardless of the servlet
     * context path.
     */
    fun validateRequest(request: HttpServletRequest): Boolean = validate(
        path = UriUtils.decode(request.requestURI.removePrefix(request.contextPath), Charsets.UTF_8),
        expires = request.getParameter(EXPIRES_QUERY)?.toLongOrNull(),
        nonce = request.getParameter(NONCE_QUERY),
        signature = request.getParameter(SIGNATURE_QUERY),
    )

    fun validCurrentUri(): Boolean {
        val attributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
        if (attributes == null) {
            logger.debug("validCurrentUri called outside of a request")
            return false
        }

        return validateRequest(attributes.request)
    }
}
