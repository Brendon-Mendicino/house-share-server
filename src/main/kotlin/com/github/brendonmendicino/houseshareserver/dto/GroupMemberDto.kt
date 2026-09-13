package com.github.brendonmendicino.houseshareserver.dto

import com.github.brendonmendicino.houseshareserver.validator.NotBlankIfPresent
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.URL

/**
 * DTO for {@link com.github.brendonmendicino.houseshareserver.entity.GroupMember}
 */
data class GroupMemberDto(
    val id: Long,

    @field:NotBlank
    @field:Size(max = 255)
    val firstName: String,

    @field:NotBlankIfPresent
    @field:Size(max = 255)
    val lastName: String?,

    @URL
    val picture: String? = null,

    val groupId: Long,
    val userId: Long? = null,
)