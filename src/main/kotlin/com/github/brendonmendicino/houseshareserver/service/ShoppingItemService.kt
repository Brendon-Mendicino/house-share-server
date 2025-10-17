package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.dto.CheckDto

interface ShoppingItemService {
    fun checkShoppingItem(shoppingItemId: Long, dto: CheckDto): CheckDto

    fun uncheckShoppingItem(shoppingItemId: Long)
}