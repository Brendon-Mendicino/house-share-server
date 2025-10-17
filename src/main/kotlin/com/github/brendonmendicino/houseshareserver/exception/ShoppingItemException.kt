package com.github.brendonmendicino.houseshareserver.exception

sealed class ShoppingItemException(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause) {

    data class NotFound(override val message: String, override val cause: Throwable? = null) :
        ShoppingItemException(message, cause) {
        companion object {
            @JvmStatic
            fun from(id: Long) = NotFound("Shopping item with id $id not found")
        }
    }
}
