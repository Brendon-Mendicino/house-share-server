package com.github.brendonmendicino.houseshareserver.repository

import com.github.brendonmendicino.houseshareserver.entity.ShoppingItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ShoppingItemRepository : JpaRepository<ShoppingItem, Long> {
    @Query("select s from ShoppingItem s where s.appGroup.id = :groupId")
    fun findAllByGroupId(groupId: Long, pageable: Pageable): Page<ShoppingItem>

    fun findByIdAndAppGroupId(shoppingItemId: Long, groupId: Long): ShoppingItem?
}