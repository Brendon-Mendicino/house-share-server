package com.github.brendonmendicino.houseshareserver.mapper

import com.github.brendonmendicino.houseshareserver.dto.CheckDto
import com.github.brendonmendicino.houseshareserver.dto.ShoppingItemDto
import com.github.brendonmendicino.houseshareserver.entity.ShoppingItem
import com.github.brendonmendicino.houseshareserver.util.mapNotNull

fun ShoppingItem.toDto() = ShoppingItemDto(
    id = id,
    price = price,
    priority = priority,
    name = name,
    amount = amount,
    groupId = appGroup.id,
    ownerId = owner.id,
    createdAt = audit.createdAt,
    check = Pair(
        checkingUser?.id,
        checkoffTimestamp
    ).mapNotNull { id, time -> CheckDto(id, time) }
)