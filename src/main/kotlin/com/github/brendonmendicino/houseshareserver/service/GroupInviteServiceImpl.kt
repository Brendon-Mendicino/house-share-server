package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.dto.GroupDto
import com.github.brendonmendicino.houseshareserver.dto.InviteUrlDto
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder

@Service
class GroupInviteServiceImpl(
    private val signedUrlService: SignedUrlService,
    private val userService: UserService,
    private val groupService: GroupService,
) : GroupInviteService {
    companion object {
        private val logger = LoggerFactory.getLogger(GroupInviteServiceImpl::class.java)
    }

    @PreAuthorize("@authorizationService.isMemberOf(#groupId)")
    override fun createInviteUrl(groupId: Long): InviteUrlDto {
        val inviteUri = signedUrlService.signPath("/api/v1/groups/${groupId}/invite/join")
        val uriComponents = UriComponentsBuilder.fromUri(inviteUri).build()

        logger.info("Created InviteUrl for Group@$groupId")

        return InviteUrlDto(
            inviteUri = inviteUri,
            expires = uriComponents.queryParams.getFirst("expires")!!.toLong(),
            nonce = uriComponents.queryParams.getFirst("nonce")!!,
            signature = uriComponents.queryParams.getFirst("signature")!!,
        )
    }

    @PreAuthorize("@signedUrlService.validCurrentUri()")
    override fun joinFromInviteUrl(groupId: Long): GroupDto {
        val loggedUser = userService.loggedUser()

        return groupService.addUserNoMember(groupId, loggedUser.id).also {
            logger.info("User${loggedUser.id} joined Group@$groupId via InviteUrl")
        }
    }
}