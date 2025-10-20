package com.github.brendonmendicino.houseshareserver.mapper

import com.github.brendonmendicino.houseshareserver.dto.GroupDto
import com.github.brendonmendicino.houseshareserver.entity.AppGroup

fun AppGroup.toDto() = GroupDto(
    id = id,
    name = name,
    description = description,
    userIds = users.map { it.id },
)
