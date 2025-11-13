package com.github.brendonmendicino.houseshareserver.dto

import com.github.brendonmendicino.houseshareserver.entity.ExpenseCategory
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import java.time.OffsetDateTime

data class ExpenseDto(
    val id: Long,
    val category: ExpenseCategory,
    @field:NotBlank
    val title: String,
    val description: String?,
    val ownerId: Long,
    val payerId: Long,
    val groupId: Long,
    val createdAt: OffsetDateTime,
    @field:Valid
    val expenseParts: List<ExpensePartDto>,
)