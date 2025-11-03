package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.dto.CheckDto
import com.github.brendonmendicino.houseshareserver.dto.ExpenseDto
import com.github.brendonmendicino.houseshareserver.dto.GroupDto
import com.github.brendonmendicino.houseshareserver.dto.ShoppingItemDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GroupService : CrudService<GroupDto> {
    fun addUser(groupId: Long, userId: Long): GroupDto

    fun removeUser(groupId: Long, userId: Long): GroupDto

    fun addShoppingItem(groupId: Long, item: ShoppingItemDto): ShoppingItemDto

    fun getShoppingItems(groupId: Long, pageable: Pageable): Page<ShoppingItemDto>

    fun checkShoppingItem(groupId: Long, shoppingItemId: Long, dto: CheckDto): CheckDto

    fun uncheckShoppingItem(groupId: Long, shoppingItemId: Long)

    fun addExpense(groupId: Long, expense: ExpenseDto): ExpenseDto

    fun getExpenses(groupId: Long, pageable: Pageable): Page<ExpenseDto>
}