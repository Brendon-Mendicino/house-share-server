package com.github.brendonmendicino.houseshareserver.exception

sealed class GroupMemberException(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause) {

    data class NotFound(override val message: String, override val cause: Throwable? = null) :
        GroupMemberException(message, cause) {
        companion object {
            @JvmStatic
            fun from(id: Long) = NotFound("GroupMember with id $id not found")
        }
    }
}