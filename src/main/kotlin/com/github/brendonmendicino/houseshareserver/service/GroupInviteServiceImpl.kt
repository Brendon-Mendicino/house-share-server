package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.dto.AppGroupDto
import com.github.brendonmendicino.houseshareserver.dto.InviteUrlDto
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

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
        val signed = signedUrlService.signPath("/api/v1/groups/${groupId}/invite/join")
        val inviteUrl = signed.applyTo(ServletUriComponentsBuilder.fromCurrentContextPath())
            .encode()
            .build()
            .toUri()

        logger.info("Created InviteUrl for Group@$groupId")

        return InviteUrlDto(
            inviteUri = signed.toUri(),
            inviteUrl = inviteUrl,
            expires = signed.expires,
            nonce = signed.nonce,
            signature = signed.signature,
        )
    }

    @PreAuthorize("@signedUrlService.validCurrentUri()")
    override fun joinFromInviteUrl(groupId: Long): AppGroupDto {
        val loggedUser = userService.loggedUser()

        return groupService.addUserNoMember(groupId, loggedUser.id).also {
            logger.info("User${loggedUser.id} joined Group@$groupId via InviteUrl")
        }
    }
}