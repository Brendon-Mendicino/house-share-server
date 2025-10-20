package com.github.brendonmendicino.houseshareserver.dto

data class UserDto(
    val id: Long,
    val username: String,
    var email: String?,
    var firstName: String?,
    var lastName: String?,
)