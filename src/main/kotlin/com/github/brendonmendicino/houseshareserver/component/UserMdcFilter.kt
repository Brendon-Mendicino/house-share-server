package com.github.brendonmendicino.houseshareserver.component

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class UserMdcFilter : OncePerRequestFilter() {
    companion object {
        private const val USER_SUB = "user.sub"
    }

    private val oidcPrincipal: OidcUser?
        get() = SecurityContextHolder.getContext().authentication?.principal as? OidcUser

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val principal = oidcPrincipal
        if (principal != null) {
            MDC.put(USER_SUB, principal.subject)
        }

        try {
            filterChain.doFilter(request, response)
        } finally {
            MDC.remove(USER_SUB)
        }
    }
}