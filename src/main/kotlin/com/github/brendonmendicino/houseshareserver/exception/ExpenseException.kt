package com.github.brendonmendicino.houseshareserver.exception

sealed class ExpenseException(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause) {

    data class NotFound(override val message: String, override val cause: Throwable? = null) :
        ExpenseException(message, cause) {
        companion object {
            @JvmStatic
            fun from(id: Long) = NotFound("Expense with id $id not found")
        }
    }
}