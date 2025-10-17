package com.github.brendonmendicino.houseshareserver.controller

import com.github.brendonmendicino.houseshareserver.dto.CheckDto
import com.github.brendonmendicino.houseshareserver.service.ShoppingItemService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/shopping-items")
class ShoppingItemController(
    private val shoppingService: ShoppingItemService,
) {
    @PostMapping("/{shoppingItemId}/checkoff")
    fun checkShoppingItem(
        @PathVariable shoppingItemId: Long,
        @RequestBody dto: CheckDto
    ) = shoppingService.checkShoppingItem(shoppingItemId, dto)

    @DeleteMapping("/{shoppingItemId}/checkoff")
    fun uncheckShoppingItem(
        @PathVariable shoppingItemId: Long,
    ) = shoppingService.uncheckShoppingItem(shoppingItemId)
}
