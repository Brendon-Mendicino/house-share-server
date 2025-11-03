package com.github.brendonmendicino.houseshareserver.repository

import com.github.brendonmendicino.houseshareserver.entity.AppGroup
import com.github.brendonmendicino.houseshareserver.entity.AppUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface GroupRepository : JpaRepository<AppGroup, Long> {
    @Query("select u from AppGroup g join g.users u where g.id = :groupId and u.id = :userId")
    fun findUserById(groupId: Long, userId: Long): AppUser?

    @Query("select true from AppGroup g join g.users u where g.id = :groupId and u.id = :userId")
    fun existsUserById(groupId: Long, userId: Long): Boolean
}