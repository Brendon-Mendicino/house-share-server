package com.github.brendonmendicino.houseshareserver.dto

import com.github.brendonmendicino.houseshareserver.validator.NotBlankIfPresent
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.net.URI

data class AppGroupDto(
    val id: Long,

    @field:NotBlank(groups = [])
    @field:Size(max = 250)
    val name: String,

    @field:NotBlankIfPresent
    @field:Size(max = 250)
    val description: String?,

    @field:Size(min = 1)
    val userIds: List<Long>,

    val memberIds: List<Long>,

    val imageUrl: URI?,
)
