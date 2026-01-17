package com.github.brendonmendicino.houseshareserver.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val crr: ClientRegistrationRepository,
//    @param:Value("\${server.port}")
//    private val port: String,
    private val oidcUserService: OAuth2UserService<OidcUserRequest, OidcUser>,
) {

    /**
     * Handle RP-initiated logout
     */
    fun oidcLogoutSuccessHandler() = OidcClientInitiatedLogoutSuccessHandler(crr)
        // TODO: change
        .also { it.setPostLogoutRedirectUri("{baseUrl}") }

    /**
     * Maps roles from the keycloak userInfo to Spring roles.
     */
    private fun userAuthoritiesMapper(): GrantedAuthoritiesMapper =
        GrantedAuthoritiesMapper { authorities: Collection<GrantedAuthority> ->
            val mappedAuthorities = mutableSetOf<GrantedAuthority>()

            authorities.forEach { authority ->
                if (authority is OidcUserAuthority) {
                    val userInfo = authority.userInfo

                    // Map the claims found in idToken and/or userInfo
                    // to one or more GrantedAuthority's and add it to mappedAuthorities
                    val roles = userInfo
                        .claims["realm_access"]
                        ?.let { it as? Map<*, *> }
                        ?.get("roles")
                        ?.let { it as? List<*> }
                        ?.map { SimpleGrantedAuthority("ROLE_$it") }
                        ?: listOf()

                    mappedAuthorities.addAll(roles)
                } else if (authority is OAuth2UserAuthority) {
                    val userAttributes = authority.attributes

                    // Map the attributes found in userAttributes
                    // to one or more GrantedAuthority's and add it to mappedAuthorities
                    val roles = userAttributes["realm_access"]
                        ?.let { it as? Map<*, *> }
                        ?.get("roles")
                        ?.let { it as List<*> }
                        ?.map { SimpleGrantedAuthority("ROLE_$it") }
                        ?: listOf()

                    mappedAuthorities.addAll(roles)
                }
            }

            mappedAuthorities
        }

    @Bean
    @Profile("!no-security")
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                authorize("/", permitAll)
                authorize("/public/**", permitAll)
                authorize("/login/**", permitAll)
                authorize("/logout/**", permitAll)
                authorize("/error", permitAll)
                authorize("/.well-known/**", permitAll)
                authorize(anyRequest, authenticated)
            }

            oauth2Login {
                userInfoEndpoint {
                    oidcUserService = this@SecurityConfig.oidcUserService
                    userAuthoritiesMapper = userAuthoritiesMapper()
                }
            }

            exceptionHandling {
                defaultAuthenticationEntryPointFor(
                    HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    PathPatternRequestMatcher.withDefaults().matcher("/api/**")
                )
            }

            logout {
                logoutUrl = "/logout"
                logoutSuccessHandler = oidcLogoutSuccessHandler()
            }

            csrf {
                csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse()
                csrfTokenRequestHandler = SpaCsrfTokenRequestHandler()
            }
        }

        return http.build()
    }

    @Bean
    @Profile("no-security")
    fun noSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                authorize(anyRequest, permitAll)
            }
            csrf { disable() }
            cors { disable() }
        }

        return http.build()
    }
}
