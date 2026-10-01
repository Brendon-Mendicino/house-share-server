package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.entity.AppUser
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
        val user = currentUser() ?: return false
        val member = user.groups.contains(BaseEntity(groupId))
        if (!member) logger.debug("isMemberOf: {} is not a member of Group@{}", user.ref(), groupId)
        return member
    }

    @Transactional(readOnly = true)
    fun isSelf(userId: Long): Boolean {
        val user = currentUser() ?: return false
        val self = user.id == userId
        if (!self) logger.debug("isSelf: {} is not User@{}", user.ref(), userId)
        return self
    }

    private fun currentUser(): AppUser? {
        val principal = oidcPrincipal
        if (principal == null) {
            logger.debug(
                "No OIDC principal in the security context: {}",
                SecurityContextHolder.getContext().authentication
            )
            return null
        }
        val user = userRepository.findBySub(principal.subject)
        if (user == null) logger.debug("No AppUser found for sub={}", principal.subject)
        return user
    }
}