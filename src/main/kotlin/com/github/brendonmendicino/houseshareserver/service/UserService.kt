package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.dto.GroupDto
import com.github.brendonmendicino.houseshareserver.dto.UserDto

interface UserService : CrudService<UserDto> {
    fun findGroups(userId: Long): List<GroupDto>
}