package com.github.brendonmendicino.houseshareserver.repository

import com.github.brendonmendicino.houseshareserver.entity.AppUser
import org.springframework.data.jpa.repository.JpaRepository


interface UserRepository : JpaRepository<AppUser, Long> {
    fun findByIdAndGroups_Id(id: Long, groupsId: Long): AppUser?

    fun existsBySub(sub: String): Boolean

    fun findBySub(sub: String): AppUser?
}