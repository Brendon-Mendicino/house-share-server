package com.github.brendonmendicino.houseshareserver.mapper

import com.github.brendonmendicino.houseshareserver.dto.CheckDto
import com.github.brendonmendicino.houseshareserver.dto.ShoppingItemDto
import com.github.brendonmendicino.houseshareserver.entity.ShoppingItem
import org.springframework.context.annotation.Configuration

@Configuration
class ShoppingItemEntityMapper : Mapper<ShoppingItem, ShoppingItemDto> {
    override fun map(it: ShoppingItem) = ShoppingItemDto(
        id = it.id,
        price = it.price,
        priority = it.priority,
        name = it.name,
        amount = it.amount,
        groupId = it.appGroup.id,
        ownerId = it.owner.id,
        createdAt = it.audit.createdAt,
        check = Pair(
            it.checkingUser?.id,
            it.checkoffTimestamp
        ).let { (id, time) -> if (id == null || time == null) null else CheckDto(id, time) },
    )
}