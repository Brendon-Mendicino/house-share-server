package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.dto.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GroupService : CrudService<GroupDto> {
    fun addUser(groupId: Long, userId: Long): GroupDto

    fun removeUser(groupId: Long, userId: Long): GroupDto

    fun getUsers(groupId: Long): List<UserDto>

    fun getUserById(groupId: Long, userId: Long): UserDto

    fun addShoppingItem(groupId: Long, item: ShoppingItemDto): ShoppingItemDto

    fun getShoppingItems(groupId: Long, pageable: Pageable): Page<ShoppingItemDto>

    fun updateShoppingItem(groupId: Long, shoppingItemId: Long, item: ShoppingItemDto): ShoppingItemDto

    fun removeShoppingItem(groupId: Long, shoppingItemId: Long)

    fun checkShoppingItem(groupId: Long, shoppingItemId: Long, dto: CheckDto): CheckDto

    fun uncheckShoppingItem(groupId: Long, shoppingItemId: Long)

    fun addExpense(groupId: Long, expense: ExpenseDto): ExpenseDto

    fun getExpenses(groupId: Long, pageable: Pageable): Page<ExpenseDto>

    fun updateExpense(groupId: Long, expenseId: Long, dto: ExpenseDto): ExpenseDto

    fun deleteExpense(groupId: Long, expenseId: Long)
}