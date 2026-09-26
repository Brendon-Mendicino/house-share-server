package com.github.brendonmendicino.houseshareserver.security

import com.github.brendonmendicino.houseshareserver.security.SecurityConfig.Companion.ADMIN_ROLE
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.HttpSecurityDsl
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.csrf.CookieCsrfTokenRepository

/**
 * The routes are split in filter chains, evaluated in order:
 *
 * 1. [adminFilterChain]: administrative/operational routes (actuator, OpenAPI docs),
 *    restricted to [ADMIN_ROLE]. Unauthenticated users are redirected to the login page.
 * 2. [apiFilterChain]: the REST API used by the SPA and the mobile app. Requires an
 *    authenticated session and answers `401` instead of redirecting. Fine-grained authorization
 *    is done with `@PreAuthorize` on the services.
 * 3. [publicFilterChain]: public resources and the OAuth2 login/logout flow, open to everyone.
 * 4. [denyAllFilterChain]: everything else is denied.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("!no-security")
class SecurityConfig(
    private val crr: ClientRegistrationRepository,
    private val oidcUserService: OAuth2UserService<OidcUserRequest, OidcUser>,
) {
    companion object {
        const val ADMIN_ROLE = "admin"
    }

    /**
     * Handle RP-initiated logout
     */
    private fun oidcLogoutSuccessHandler() = OidcClientInitiatedLogoutSuccessHandler(crr)
        // TODO: change
        .also { it.setPostLogoutRedirectUri("{baseUrl}") }

    /**
     * CSRF protection shared by every chain: the token is exposed in a JS-readable cookie (SPA pattern).
     */
    private fun HttpSecurityDsl.spaCsrf() {
        csrf {
            csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse()
            csrfTokenRequestHandler = SpaCsrfTokenRequestHandler()
        }
    }

    @Bean
    @Order(1)
    fun adminFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            securityMatcher(
                "/actuator/**",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**",
            )

            authorizeHttpRequests {
                // Must stay reachable by infrastructure probes
                authorize("/actuator/health/**", permitAll)
                authorize(anyRequest, hasRole(ADMIN_ROLE))
            }

            // The login flow lives in the web chain, send unauthenticated users there
            exceptionHandling {
                authenticationEntryPoint = LoginUrlAuthenticationEntryPoint("/login")
            }

            spaCsrf()
        }

        return http.build()
    }

    @Bean
    @Order(2)
    fun apiFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            securityMatcher(
                "/api/**",
                "/csrf",
            )

            authorizeHttpRequests {
                authorize(anyRequest, authenticated)
            }

            // API clients must not be redirected to the login page
            exceptionHandling {
                authenticationEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
            }

            spaCsrf()
        }

        return http.build()
    }

    @Bean
    @Order(3)
    fun publicFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            securityMatcher(
                "/",
                "/public/**",
                "/login/**",
                "/logout/**",
                "/oauth2/**",
                "/error",
                "/.well-known/**",
                // Served by Spring Security for the generated login/logout pages
                "/default-ui.css",
            )
            // favicon, /css/**, /js/**, /images/**, /webjars/**, ...
            securityMatcher(PathRequest.toStaticResources().atCommonLocations())

            authorizeHttpRequests {
                authorize(anyRequest, permitAll)
            }

            oauth2Login {
                userInfoEndpoint {
                    oidcUserService = this@SecurityConfig.oidcUserService
                    userAuthoritiesMapper = KeycloakAuthoritiesMapper()
                }
            }

            logout {
                logoutUrl = "/logout"
                logoutSuccessHandler = oidcLogoutSuccessHandler()
            }


            spaCsrf()
        }

        return http.build()
    }

    @Bean
    @Order(4)
    fun denyAllFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                authorize(anyRequest, denyAll)
            }
        }

        return http.build()
    }
}
