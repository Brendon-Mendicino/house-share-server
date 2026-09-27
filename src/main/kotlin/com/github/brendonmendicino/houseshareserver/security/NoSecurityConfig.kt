package com.github.brendonmendicino.houseshareserver.security

import com.github.brendonmendicino.houseshareserver.configuration.Profiles
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile(Profiles.NO_SECURITY)
class NoSecurityConfig {
    @Bean
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