package com.github.brendonmendicino.houseshareserver.repository

import com.github.brendonmendicino.houseshareserver.entity.AppGroup
import com.github.brendonmendicino.houseshareserver.entity.AppUser
import com.github.brendonmendicino.houseshareserver.entity.GroupMember
import org.springframework.data.jpa.repository.JpaRepository

interface GroupMemberRepository : JpaRepository<GroupMember, Long> {
    fun findByIdAndGroupId(id: Long, groupId: Long): GroupMember?

    fun findByUserAndGroup(user: AppUser, group: AppGroup): GroupMember?
}