package com.github.brendonmendicino.houseshareserver.exception

sealed class GroupException(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause) {

    data class NotFound(override val message: String, override val cause: Throwable? = null) :
        GroupException(message, cause) {
        companion object {
            @JvmStatic
            fun from(id: Long) = NotFound("Group with id $id not found")
        }
    }

    data class NotMember(override val message: String, override val cause: Throwable? = null) :
        GroupException(message, cause) {
        companion object {
            @JvmStatic
            fun from(groupId: Long, userId: Long) = NotMember("User@$userId is not a member of Group@$groupId")
        }
    }
}