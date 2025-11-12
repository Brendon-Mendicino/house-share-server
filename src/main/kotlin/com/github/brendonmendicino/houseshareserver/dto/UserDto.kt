package com.github.brendonmendicino.houseshareserver.dto

import java.net.URI

data class UserDto(
    val id: Long,
    val username: String,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val picture: URI?,
)