package com.github.brendonmendicino.houseshareserver.dto

import com.github.brendonmendicino.houseshareserver.validator.NotBlankIfPresent
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class GroupDto(
    val id: Long,
    @field:NotBlank
    @field:Size(max = 250)
    val name: String,
    @field:NotBlankIfPresent
    @field:Size(max = 250)
    val description: String?,
    @field:Size(min = 1)
    val userIds: List<Long>,
)
