package com.github.brendonmendicino.houseshareserver.dto

import java.time.OffsetDateTime

data class CheckDto(
    val checkingMemberId: Long,
    val checkoffTimestamp: OffsetDateTime,
)
