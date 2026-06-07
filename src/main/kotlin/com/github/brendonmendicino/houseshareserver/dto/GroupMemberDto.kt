package com.github.brendonmendicino.houseshareserver.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.URL

/**
 * DTO for {@link com.github.brendonmendicino.houseshareserver.entity.GroupMember}
 */
data class GroupMemberDto(
    val id: Long? = null,

    @field:NotBlank
    @field:Size(max = 255)
    val username: String,

    @URL
    val picture: String? = null,

    val groupId: Long? = null,
    val userId: Long? = null,
)