package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.dto.GroupDto
import com.github.brendonmendicino.houseshareserver.dto.UserDto
import com.github.brendonmendicino.houseshareserver.exception.UserException
import com.github.brendonmendicino.houseshareserver.mapper.toDto
import com.github.brendonmendicino.houseshareserver.mapper.toEntity
import com.github.brendonmendicino.houseshareserver.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {

    override fun getAll(pageable: Pageable): Page<UserDto> =
        userRepository.findAll(pageable).map { it.toDto() }

    override fun getById(id: Long): UserDto =
        userRepository.findByIdOrNull(id)?.toDto() ?: throw UserException.NotFound.from(id)

    override fun save(dto: UserDto): UserDto =
        userRepository.save(dto.toEntity()).toDto()

    override fun update(id: Long, dto: UserDto): UserDto {
        val user = dto.toEntity()
        // Create new entity if it does not exist
        user.id = if (userRepository.existsById(id)) id else 0

        return userRepository.save(user).toDto()
    }

    override fun delete(id: Long) {
        userRepository.deleteById(id)
    }

    override fun findGroups(userId: Long): List<GroupDto> {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserException.NotFound.from(userId)
        return user.groups.map { it.toDto() }
    }

    override fun findUserBySub(userSub: String): UserDto {
        return userRepository.findBySub(userSub)?.toDto() ?: throw UserException.NotFound("No user found")
    }
}