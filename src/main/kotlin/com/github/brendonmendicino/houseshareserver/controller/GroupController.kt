package com.github.brendonmendicino.houseshareserver.controller

import com.github.brendonmendicino.houseshareserver.dto.CheckDto
import com.github.brendonmendicino.houseshareserver.dto.ExpenseDto
import com.github.brendonmendicino.houseshareserver.dto.GroupDto
import com.github.brendonmendicino.houseshareserver.dto.ShoppingItemDto
import com.github.brendonmendicino.houseshareserver.service.GroupInviteService
import com.github.brendonmendicino.houseshareserver.service.GroupService
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/groups")
class GroupController(
    private val groupService: GroupService,
    private val groupInviteService: GroupInviteService,
) : CrudController<GroupDto>(groupService) {
    @PostMapping("/{groupId}/invite")
    fun inviteUrl(@PathVariable groupId: Long) = groupInviteService.createInviteUrl(groupId)

    @PostMapping("/{groupId}/invite/join")
    fun joinFromInviteUrl(
        @PathVariable groupId: Long,
        @RequestParam expires: Long,
        @RequestParam nonce: String,
        @RequestParam signature: String
    ): GroupDto =
        groupInviteService.joinFromInviteUrl(groupId)

    @PutMapping("/{groupId}/users/{userId}")
    fun addUser(@PathVariable groupId: Long, @PathVariable userId: Long) = groupService.addUser(groupId, userId)

    @DeleteMapping("/{groupId}/users/{userId}")
    fun deleteUser(@PathVariable groupId: Long, @PathVariable userId: Long) = groupService.removeUser(groupId, userId)

    @GetMapping("/{groupId}/users")
    fun getUsers(@PathVariable groupId: Long) = groupService.getUsers(groupId)

    @GetMapping("/{groupId}/users/{userId}")
    fun getUserById(@PathVariable groupId: Long, @PathVariable userId: Long) = groupService.getUserById(groupId, userId)

    @PostMapping("/{groupId}/shopping-items")
    fun addShoppingItem(@PathVariable groupId: Long, @Valid @RequestBody item: ShoppingItemDto) =
        groupService.addShoppingItem(groupId, item)

    @GetMapping("/{groupId}/shopping-items")
    fun getShoppingItems(@PathVariable groupId: Long, pageable: Pageable) =
        groupService.getShoppingItems(groupId, pageable)

    @PutMapping("/{groupId}/shopping-items/{shoppingItemId}")
    fun updateShoppingItem(
        @PathVariable groupId: Long,
        @PathVariable shoppingItemId: Long,
        @Valid @RequestBody item: ShoppingItemDto
    ) = groupService.updateShoppingItem(groupId, shoppingItemId, item)

    @DeleteMapping("/{groupId}/shopping-items/{shoppingItemId}")
    fun deleteShoppingItem(@PathVariable groupId: Long, @PathVariable shoppingItemId: Long) =
        groupService.removeShoppingItem(groupId, shoppingItemId)

    @PostMapping("/{groupId}/shopping-items/{shoppingItemId}/checkoff")
    fun checkShoppingItem(
        @PathVariable groupId: Long,
        @PathVariable shoppingItemId: Long,
        @Valid
        @RequestBody dto: CheckDto
    ) = groupService.checkShoppingItem(groupId, shoppingItemId, dto)

    @DeleteMapping("/{groupId}/shopping-items/{shoppingItemId}/checkoff")
    fun uncheckShoppingItem(
        @PathVariable groupId: Long,
        @PathVariable shoppingItemId: Long,
    ) = groupService.uncheckShoppingItem(groupId, shoppingItemId)

    @GetMapping("/{groupId}/expenses")
    fun getExpenses(@PathVariable groupId: Long, pageable: Pageable) = groupService.getExpenses(groupId, pageable)

    @PostMapping("/{groupId}/expenses")
    fun addExpense(@PathVariable groupId: Long, @Valid @RequestBody dto: ExpenseDto) =
        groupService.addExpense(groupId, dto)

    @PutMapping("/{groupId}/expenses/{expenseId}")
    fun updateExpense(@PathVariable groupId: Long, @PathVariable expenseId: Long, @Valid @RequestBody dto: ExpenseDto) =
        groupService.updateExpense(groupId, expenseId, dto)

    @DeleteMapping("/{groupId}/expenses/{expenseId}")
    fun deleteExpense(@PathVariable groupId: Long, @PathVariable expenseId: Long) =
        groupService.deleteExpense(groupId, expenseId)
}