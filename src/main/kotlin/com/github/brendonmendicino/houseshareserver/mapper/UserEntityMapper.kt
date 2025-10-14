package com.github.brendonmendicino.houseshareserver.mapper

import com.github.brendonmendicino.houseshareserver.dto.UserDto
import com.github.brendonmendicino.houseshareserver.entity.AppUser
import org.springframework.context.annotation.Configuration

@Configuration
class UserEntityMapper : Mapper<AppUser, UserDto> {
    override fun map(it: AppUser): UserDto = UserDto(
        id = it.id,
        username = it.username,
    )
}