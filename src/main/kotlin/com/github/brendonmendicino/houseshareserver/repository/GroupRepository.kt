package com.github.brendonmendicino.houseshareserver.repository

import com.github.brendonmendicino.houseshareserver.entity.AppGroup
import com.github.brendonmendicino.houseshareserver.entity.AppUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface GroupRepository : JpaRepository<AppGroup, Long> {
    @EntityGraph(attributePaths = ["members", "users"])
    override fun findAll(pageable: Pageable): Page<AppGroup>

    @EntityGraph(attributePaths = ["members", "users"])
    override fun findById(groupId: Long): Optional<AppGroup>

    @Query("select u from AppGroup g join g.users u where g.id = :groupId and u.id = :userId")
    fun findUserById(groupId: Long, userId: Long): AppUser?

    @Query("select true from AppGroup g join g.users u where g.id = :groupId and u.id = :userId")
    fun existsUserById(groupId: Long, userId: Long): Boolean
}