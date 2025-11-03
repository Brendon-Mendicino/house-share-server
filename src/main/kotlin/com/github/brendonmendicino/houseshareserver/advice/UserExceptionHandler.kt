package com.github.brendonmendicino.houseshareserver.advice

import com.github.brendonmendicino.houseshareserver.exception.UserException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class UserExceptionHandler {
    @ExceptionHandler(UserException.NotFound::class, UserException.DuplicateId::class)
    fun handleGenericException(e: UserException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message)
}