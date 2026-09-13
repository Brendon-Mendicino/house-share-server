package com.github.brendonmendicino.houseshareserver.mapper

import com.github.brendonmendicino.houseshareserver.dto.GroupMemberDto
import com.github.brendonmendicino.houseshareserver.entity.GroupMember

fun GroupMember.toDto(): GroupMemberDto = GroupMemberDto(
    id = id,
    firstName = firstName,
    lastName = lastName,
    picture = picture?.toString(),
    groupId = group.id,
    userId = user?.id,
)