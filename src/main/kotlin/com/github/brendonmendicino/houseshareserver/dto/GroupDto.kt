package com.github.brendonmendicino.houseshareserver.dto

data class GroupDto(
    val id: Long,
    val name: String,
    val description: String?,
    val userIds: List<Long>,
)
