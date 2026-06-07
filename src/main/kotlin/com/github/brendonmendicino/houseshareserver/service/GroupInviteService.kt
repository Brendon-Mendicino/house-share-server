package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.dto.AppGroupDto
import com.github.brendonmendicino.houseshareserver.dto.InviteUrlDto

interface GroupInviteService {
    fun createInviteUrl(groupId: Long): InviteUrlDto

    fun joinFromInviteUrl(groupId: Long): AppGroupDto
}