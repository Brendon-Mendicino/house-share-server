package com.github.brendonmendicino.houseshareserver.dto

import java.net.URI

data class InviteUrlDto(
    val inviteUri: URI,
    val expires: Long,
    val nonce: String,
    val signature: String,
)