package com.github.brendonmendicino.houseshareserver.mapper

import com.github.brendonmendicino.houseshareserver.dto.UserDto
import com.github.brendonmendicino.houseshareserver.entity.AppUser
import org.springframework.context.annotation.Configuration

@Configuration
class UserDtoMapper : Mapper<UserDto, AppUser> {
    override fun map(it: UserDto): AppUser = AppUser(
        it.username
    )
}