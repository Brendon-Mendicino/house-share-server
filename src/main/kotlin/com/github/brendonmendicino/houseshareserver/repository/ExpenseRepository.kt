package com.github.brendonmendicino.houseshareserver.repository

import com.github.brendonmendicino.houseshareserver.entity.Expense
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ExpenseRepository : JpaRepository<Expense, Long> {
    @Query("select e from Expense e where e.group.id = :groupId")
    fun findAllByGroupId(groupId: Long, pageable: Pageable): Page<Expense>

    fun findByIdAndGroupId(expenseId: Long, groupId: Long): Expense?

    fun deleteByIdAndGroupId(expenseId: Long, groupId: Long)
}