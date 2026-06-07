package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.dto.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GroupService : CrudService<AppGroupDto> {
    fun addUser(groupId: Long, userId: Long): AppGroupDto

    fun addUserNoMember(groupId: Long, userId: Long): AppGroupDto

    fun removeUser(groupId: Long, userId: Long): AppGroupDto

    fun getUsers(groupId: Long): List<AppUserDto>

    fun getUserById(groupId: Long, userId: Long): AppUserDto

    fun addMember(groupId: Long, member: GroupMemberDto): GroupMemberDto

    fun updateMember(groupId: Long, memberId: Long, member: GroupMemberDto): GroupMemberDto

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