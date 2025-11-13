package com.github.brendonmendicino.houseshareserver.mapper

import com.github.brendonmendicino.houseshareserver.dto.ExpenseDto
import com.github.brendonmendicino.houseshareserver.dto.ExpensePartDto
import com.github.brendonmendicino.houseshareserver.entity.Expense
import com.github.brendonmendicino.houseshareserver.entity.ExpensePart

fun Expense.toDto() = ExpenseDto(
    id = id,
    description = description,
    title = title,
    category = category,
    ownerId = owner.id,
    payerId = payer.id,
    groupId = group.id,
    createdAt = audit.createdAt,
    expenseParts = expenseParts.map { it.toDto() },
)

fun ExpensePart.toDto() = ExpensePartDto(
    id = id,
    expenseId = partOf.id,
    userId = userPart.id,
    partAmount = partAmount,
)