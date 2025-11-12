package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.entity.BaseEntity
import com.github.brendonmendicino.houseshareserver.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthorizationService(private val userRepository: UserRepository) {
    companion object {
        private val logger = LoggerFactory.getLogger(AuthorizationService::class.java)
    }

    private val oidcPrincipal: OidcUser?
        get() = SecurityContextHolder.getContext().authentication?.principal as? OidcUser

    @Transactional(readOnly = true)
    fun isMemberOf(groupId: Long): Boolean {
        val principal = oidcPrincipal ?: return false
        val user = userRepository.findBySub(principal.subject) ?: return false
        return user.groups.contains(BaseEntity(groupId))
    }

    @Transactional(readOnly = true)
    fun isSelf(userId: Long): Boolean {
        val principal = oidcPrincipal ?: return false
        val user = userRepository.findBySub(principal.subject) ?: return false
        return user.id == userId
    }
}