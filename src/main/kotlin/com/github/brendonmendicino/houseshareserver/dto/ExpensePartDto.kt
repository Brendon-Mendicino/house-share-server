package com.github.brendonmendicino.houseshareserver.dto

import jakarta.validation.constraints.DecimalMin

data class ExpensePartDto(
    val id: Long,
    val expenseId: Long,
    val userId: Long,
    @field:DecimalMin("0.01")
    val partAmount: Double,
)