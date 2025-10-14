package com.github.brendonmendicino.houseshareserver.repository

import com.github.brendonmendicino.houseshareserver.entity.AppGroup
import org.springframework.data.jpa.repository.JpaRepository

interface GroupRepository : JpaRepository<AppGroup, Long>