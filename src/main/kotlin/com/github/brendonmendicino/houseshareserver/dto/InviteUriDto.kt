package com.github.brendonmendicino.houseshareserver.dto

import java.net.URI

data class InviteUrlDto(
    /** Signed URI relative to the servlet context path. */
    val inviteUri: URI,
    /** Ready to use absolute URL, built from the current request's scheme, host and context path. */
    val inviteUrl: URI,
    val expires: Long,
    val nonce: String,
    val signature: String,
)
