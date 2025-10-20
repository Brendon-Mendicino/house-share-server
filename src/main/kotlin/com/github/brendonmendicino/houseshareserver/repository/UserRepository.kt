package com.github.brendonmendicino.houseshareserver.repository

import com.github.brendonmendicino.houseshareserver.entity.AppUser
import org.springframework.data.jpa.repository.JpaRepository


interface UserRepository : JpaRepository<AppUser, Long> {
    fun existsByJti(jti: String): Boolean
}