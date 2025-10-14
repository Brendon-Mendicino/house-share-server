package com.github.brendonmendicino.houseshareserver.dto

import com.github.brendonmendicino.houseshareserver.entity.ShoppingItemPriority

data class ShoppingItemDto(
    val id: Long,
    val ownerId: Long,
    val groupId: Long,
    var name: String,
    var amount: Int,
    var price: Double?,
    var priority: ShoppingItemPriority,
)
