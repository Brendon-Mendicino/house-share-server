package com.github.brendonmendicino.houseshareserver.mapper

import com.github.brendonmendicino.houseshareserver.dto.AppUserDto
import com.github.brendonmendicino.houseshareserver.entity.AppUser

fun AppUserDto.toEntity() = AppUser(
    username = username,
    firstName = firstName,
    lastName = lastName,
    email = email,
    sub = null,
    picture = picture,
)

fun AppUser.toDto() = AppUserDto(
    id = id,
    username = username,
    lastName = lastName,
    firstName = firstName,
    email = email,
    picture = picture,
)