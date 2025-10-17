package com.github.brendonmendicino.houseshareserver.dto

import com.github.brendonmendicino.houseshareserver.entity.ShoppingItemPriority
import java.time.OffsetDateTime

data class ShoppingItemDto(
    val id: Long,
    val ownerId: Long,
    val groupId: Long,
    val name: String,
    val amount: Int,
    val price: Double?,
    val priority: ShoppingItemPriority,
    val createdAt: OffsetDateTime,
    val check: CheckDto?,
)
