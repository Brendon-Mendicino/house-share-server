package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.dto.GroupDto
import com.github.brendonmendicino.houseshareserver.dto.UserDto
import com.github.brendonmendicino.houseshareserver.exception.UserException
import com.github.brendonmendicino.houseshareserver.mapper.toDto
import com.github.brendonmendicino.houseshareserver.mapper.toEntity
import com.github.brendonmendicino.houseshareserver.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {
    companion object {
        private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)
    }

    @PreAuthorize("hasRole('admin')")
    override fun getAll(pageable: Pageable): Page<UserDto> =
        userRepository.findAll(pageable).map { it.toDto() }

    @PreAuthorize("hasRole('admin') || @authorizationService.isSelf(#id)")
    override fun getById(id: Long): UserDto =
        userRepository.findByIdOrNull(id)?.toDto() ?: throw UserException.NotFound.from(id)

    @PreAuthorize("hasRole('admin')")
    override fun save(dto: UserDto): UserDto =
        userRepository.save(dto.toEntity()).toDto().also {
            logger.info("Created User@${it.id}")
        }

    @PreAuthorize("hasRole('admin')")
    override fun update(id: Long, dto: UserDto): UserDto {
        val user = dto.toEntity()
        // Create new entity if it does not exist
        user.id = if (userRepository.existsById(id)) id else 0

        return userRepository.save(user).toDto().also {
            logger.info("Updated User@${it.id}")
        }
    }

    @PreAuthorize("hasRole('admin')")
    override fun delete(id: Long) {
        userRepository.deleteById(id)
        logger.info("Deleted User@$id")
    }

    @PreAuthorize("hasRole('admin') || @authorizationService.isSelf(#userId)")
    override fun findGroups(userId: Long): List<GroupDto> {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserException.NotFound.from(userId)
        return user.groups.map { it.toDto() }
    }

    override fun loggedUser(): UserDto {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw InsufficientAuthenticationException("User is not authenticated")

        val principal = authentication.principal as? OidcUser
            ?: throw InsufficientAuthenticationException("User is not an OidcUser")

        return userRepository.findBySub(principal.subject)?.toDto() ?: throw UserException.NotFound("No user found")
    }
}