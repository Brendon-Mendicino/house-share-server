package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.dto.GroupDto
import com.github.brendonmendicino.houseshareserver.dto.UserDto
import com.github.brendonmendicino.houseshareserver.entity.AppGroup
import com.github.brendonmendicino.houseshareserver.entity.AppUser
import com.github.brendonmendicino.houseshareserver.exception.UserException
import com.github.brendonmendicino.houseshareserver.mapper.Mapper
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
    private val dtoMapper: Mapper<UserDto, AppUser>,
    private val entityMapper: Mapper<AppUser, UserDto>,
    private val groupMapper: Mapper<AppGroup, GroupDto>,
) : UserService {

    override fun getAll(pageable: Pageable): Page<UserDto> =
        userRepository.findAll(pageable).map { entityMapper.map(it) }

    override fun getById(id: Long): UserDto =
        userRepository.findByIdOrNull(id)?.let { entityMapper.map(it) } ?: throw UserException.NotFound.from(id)

    override fun save(dto: UserDto): UserDto =
        userRepository.save(dtoMapper.map(dto)).let { entityMapper.map(it) }

    override fun update(id: Long, dto: UserDto): UserDto {
        val user = dtoMapper.map(dto)
        // Create new entity if it does not exist
        user.id = if (userRepository.existsById(id)) id else 0

        return userRepository.save(user).let { entityMapper.map(it) }
    }

    override fun delete(id: Long) {
        userRepository.deleteById(id)
    }

    override fun findGroups(userId: Long): List<GroupDto> {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserException.NotFound.from(userId)
        return user.groups.map { groupMapper.map(it) }
    }
}