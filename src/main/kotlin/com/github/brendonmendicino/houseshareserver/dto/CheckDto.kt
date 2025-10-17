package com.github.brendonmendicino.houseshareserver.dto

import java.time.OffsetDateTime

data class CheckDto(
    val checkingUserId: Long,
    val checkoffTimestamp: OffsetDateTime,
)
