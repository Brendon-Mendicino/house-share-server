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
}