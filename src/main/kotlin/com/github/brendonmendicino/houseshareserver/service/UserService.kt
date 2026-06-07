package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.dto.AppGroupDto
import com.github.brendonmendicino.houseshareserver.dto.AppUserDto

interface UserService : CrudService<AppUserDto> {
    fun findGroups(userId: Long): List<AppGroupDto>

    fun loggedUser(): AppUserDto
}