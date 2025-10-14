package com.github.brendonmendicino.houseshareserver.exception

sealed class UserException(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause) {

    data class NotFound(override val message: String, override val cause: Throwable? = null) :
        UserException(message, cause) {
        companion object {
            @JvmStatic
            fun from(id: Long) = NotFound("User with id $id not found")
        }
    }
}