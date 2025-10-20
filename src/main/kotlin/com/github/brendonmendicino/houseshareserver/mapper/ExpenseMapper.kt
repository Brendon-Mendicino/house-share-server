package com.github.brendonmendicino.houseshareserver.mapper

import com.github.brendonmendicino.houseshareserver.dto.ExpenseDto
import com.github.brendonmendicino.houseshareserver.entity.Expense

fun Expense.toDto() = ExpenseDto(
    id = id,
    description = description,
    amount = amount,
    title = title,
    category = category,
    ownerId = owner.id,
    payerId = payer.id,
    groupId = group.id,
    createdAt = audit.createdAt,
)