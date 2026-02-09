package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.mapper.toUserEntity
import com.github.brendonmendicino.houseshareserver.repository.UserRepository
import io.micrometer.observation.annotation.Observed
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OAuth2Service(
    private val userRepository: UserRepository
) {
    companion object {
        val logger = LoggerFactory.getLogger(OAuth2Service::class.java)!!
    }

    val delegate = OidcUserService()

    /**
     * Save a new Oidc user in the database if not present already.
     */
    @Bean
    fun oidcUserService() = RegisterOAuth2User()

    open inner class RegisterOAuth2User : OAuth2UserService<OidcUserRequest, OidcUser> {
        @Observed(name = "oidc.user.load")
        override fun loadUser(userRequest: OidcUserRequest?): OidcUser {
            val user = delegate.loadUser(userRequest)

            val subject = user.subject

            if (!userRepository.existsBySub(subject)) {
                userRepository.save(user.idToken.toUserEntity())
                logger.info("Registered new user from oidc. username=${user.idToken.preferredUsername}")
            }

            return user
        }
    }
}