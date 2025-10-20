package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.mapper.toUserEntity
import com.github.brendonmendicino.houseshareserver.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.oidc.user.OidcUser

@Configuration
class OAuth2Service(
    private val userRepository: UserRepository
) {
    companion object {
        val logger = LoggerFactory.getLogger(OAuth2Service::class.java)!!
    }

    val delegate = OidcUserService()

    @Bean
    fun oidcUserService() = OAuth2UserService<OidcUserRequest, OidcUser> { userRequest ->
        val user = delegate.loadUser(userRequest)

        val jti = user.attributes["jti"]?.toString() ?: throw OAuth2AuthenticationException("'jti' field missing.")

        if (!userRepository.existsByJti(jti)) {
            userRepository.save(user.idToken.toUserEntity())
            logger.info("Registered new user from oidc. username=${user.idToken.preferredUsername}")
        }

        user
    }
}