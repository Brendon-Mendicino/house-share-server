package com.github.brendonmendicino.houseshareserver.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority

/**
 * Maps Keycloak realm roles (`realm_access.roles`) to Spring `ROLE_*` authorities.
 *
 * Read the doc: https://docs.spring.io/spring-security/reference/servlet/oauth2/login/advanced.html#oauth2login-advanced-map-authorities-grantedauthoritiesmapper
 */
class KeycloakAuthoritiesMapper : GrantedAuthoritiesMapper {
    override fun mapAuthorities(authorities: Collection<GrantedAuthority>): Collection<GrantedAuthority> =
        authorities
            .flatMap { authority ->
                when (authority) {
                    // Roles can be in the ID token and/or in the userInfo
                    is OidcUserAuthority -> realmRoles(authority.userInfo?.claims) + realmRoles(authority.idToken.claims)
                    is OAuth2UserAuthority -> realmRoles(authority.attributes)
                    else -> listOf()
                }
            }
            .distinct()
            .map { role -> SimpleGrantedAuthority("ROLE_$role") }
            .toSet()

    private fun realmRoles(claims: Map<String, Any?>?): List<Any> =
        claims
            ?.get("realm_access")
            ?.let { it as? Map<*, *> }
            ?.get("roles")
            ?.let { it as? List<*> }
            ?.filterNotNull()
            ?: listOf()
}
