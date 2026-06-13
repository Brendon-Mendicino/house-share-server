package com.github.brendonmendicino.houseshareserver.mapper

import com.github.brendonmendicino.houseshareserver.dto.AppGroupDto
import com.github.brendonmendicino.houseshareserver.entity.AppGroup

fun AppGroup.toDto() = AppGroupDto(
    id = id,
    name = name,
    description = description,
    userIds = users.map { it.id },
    memberIds = members.map { it.id },
    imageUrl = imageUrl,
)
