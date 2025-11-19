package com.github.brendonmendicino.houseshareserver.dto

import com.github.brendonmendicino.houseshareserver.entity.ShoppingItemPriority
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.OffsetDateTime

data class ShoppingItemDto(
    val id: Long,
    val ownerId: Long,
    val groupId: Long,
    @field:NotBlank
    @field:Size(max = 250)
    val name: String,
    val amount: Int,
    @field:Min(1)
    val price: Long?,
    val priority: ShoppingItemPriority,
    val createdAt: OffsetDateTime,
    @field:Valid
    val check: CheckDto?,
)
