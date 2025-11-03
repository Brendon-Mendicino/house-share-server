package com.github.brendonmendicino.houseshareserver.advice

import com.github.brendonmendicino.houseshareserver.exception.ExpenseException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExpenseExceptionHandler {
    @ExceptionHandler(ExpenseException.NotFound::class)
    fun genericExpenseException(e: ExpenseException) = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message)
}