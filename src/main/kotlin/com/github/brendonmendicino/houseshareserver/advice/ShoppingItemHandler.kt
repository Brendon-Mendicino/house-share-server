package com.github.brendonmendicino.houseshareserver.advice

import com.github.brendonmendicino.houseshareserver.exception.ShoppingItemException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ShoppingItemHandler {
    @ExceptionHandler(ShoppingItemException.NotFound::class)
    fun genericExpenseException(e: ShoppingItemException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message)
}