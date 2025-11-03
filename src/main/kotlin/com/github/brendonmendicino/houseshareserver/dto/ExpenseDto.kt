package com.github.brendonmendicino.houseshareserver.dto

import com.github.brendonmendicino.houseshareserver.entity.ExpenseCategory
import java.time.OffsetDateTime

data class ExpenseDto(
    val id: Long,
    val amount: Double,
    val category: ExpenseCategory,
    val title: String,
    val description: String?,
    val ownerId: Long,
    val payerId: Long,
    val groupId: Long,
    val createdAt: OffsetDateTime,
    val expenseParts: List<ExpensePartDto>,
)