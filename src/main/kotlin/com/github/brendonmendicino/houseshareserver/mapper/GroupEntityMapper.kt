package com.github.brendonmendicino.houseshareserver.mapper

import com.github.brendonmendicino.houseshareserver.dto.GroupDto
import com.github.brendonmendicino.houseshareserver.entity.AppGroup
import org.springframework.context.annotation.Configuration

@Configuration
class GroupEntityMapper : Mapper<AppGroup, GroupDto> {
    override fun map(it: AppGroup): GroupDto = GroupDto(
        id = it.id,
        name = it.name,
        description = it.description,
        userIds = it.users.keys.toList(),
    )
}