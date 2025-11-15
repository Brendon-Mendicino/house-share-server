package com.github.brendonmendicino.houseshareserver.dto

import com.github.brendonmendicino.houseshareserver.validator.NotBlankIfPresent
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.net.URI

data class UserDto(
    val id: Long,
    @field:NotBlank
    @field:Size(max = 250)
    val username: String,
    @field:NotBlankIfPresent
    @field:Size(max = 250)
    @field:Email
    val email: String?,
    @field:NotBlankIfPresent
    @field:Size(max = 250)
    val firstName: String?,
    @field:NotBlankIfPresent
    @field:Size(max = 250)
    val lastName: String?,
    val picture: URI?,
)