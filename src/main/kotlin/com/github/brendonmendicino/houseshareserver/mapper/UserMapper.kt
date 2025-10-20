package com.github.brendonmendicino.houseshareserver.mapper

import com.github.brendonmendicino.houseshareserver.dto.UserDto
import com.github.brendonmendicino.houseshareserver.entity.AppUser

fun UserDto.toEntity() = AppUser(
    username = username,
    firstName = firstName,
    lastName = lastName,
    email = email,
    jti = null,
)

fun AppUser.toDto() = UserDto(
    id = id,
    username = username,
    lastName = lastName,
    firstName = firstName,
    email = email,
)