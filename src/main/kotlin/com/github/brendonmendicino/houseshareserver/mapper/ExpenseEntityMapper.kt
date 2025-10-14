package com.github.brendonmendicino.houseshareserver.mapper

import com.github.brendonmendicino.houseshareserver.dto.ExpenseDto
import com.github.brendonmendicino.houseshareserver.entity.Expense
import org.springframework.context.annotation.Configuration

@Configuration
class ExpenseEntityMapper : Mapper<Expense, ExpenseDto> {
    override fun map(it: Expense) = ExpenseDto(
        id = it.id,
        description = it.description,
        amount = it.amount,
        title = it.title,
        category = it.category,
        ownerId = it.owner.id,
        payerId = it.payer.id,
        groupId = it.group.id,
        createdAt = it.audit.createdAt,
    )
}