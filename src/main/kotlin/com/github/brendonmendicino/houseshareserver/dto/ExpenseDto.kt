package com.github.brendonmendicino.houseshareserver.dto

import com.github.brendonmendicino.houseshareserver.entity.ExpenseCategory
import com.github.brendonmendicino.houseshareserver.validator.NotBlankIfPresent
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.OffsetDateTime

data class ExpenseDto(
    val id: Long,
    val category: ExpenseCategory,
    @field:NotBlank
    @field:Size(max = 250)
    val title: String,
    @field:NotBlankIfPresent
    @field:Size(max = 250)
    val description: String?,
    val ownerId: Long,
    val payerId: Long,
    val groupId: Long,
    val createdAt: OffsetDateTime,
    @field:Valid
    val expenseParts: List<ExpensePartDto>,
)