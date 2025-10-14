package com.github.brendonmendicino.houseshareserver.advice

import com.github.brendonmendicino.houseshareserver.exception.GroupException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GroupExceptionHandler {
    @ExceptionHandler(GroupException.NotFound::class)
    fun handleNotFound(e: GroupException) = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message)
}