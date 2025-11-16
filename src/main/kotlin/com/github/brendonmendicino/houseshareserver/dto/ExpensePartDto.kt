package com.github.brendonmendicino.houseshareserver.dto

import jakarta.validation.constraints.Min

data class ExpensePartDto(
    val id: Long,
    val expenseId: Long,
    val userId: Long,
    @field:Min(1)
    val partAmount: Long,
)