package com.github.brendonmendicino.houseshareserver.advice

import com.github.brendonmendicino.houseshareserver.exception.ExpenseException
import com.github.brendonmendicino.houseshareserver.exception.GroupException
import com.github.brendonmendicino.houseshareserver.exception.ShoppingItemException
import com.github.brendonmendicino.houseshareserver.exception.UserException
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ProblemDetail {
        logger.error("Unexpected error occurred while processing the request", e)
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
    }


    @ExceptionHandler(ExpenseException.NotFound::class)
    fun handleExpenseNotFound(e: ExpenseException.NotFound): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message)


    @ExceptionHandler(GroupException.NotFound::class, GroupException.NotMember::class)
    fun handleGroupNotFound(e: GroupException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message)


    @ExceptionHandler(ShoppingItemException.NotFound::class)
    fun handleShoppingItemNotFound(e: ShoppingItemException.NotFound): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message)


    @ExceptionHandler(UserException::class)
    fun handleUserException(e: UserException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message)

    @ExceptionHandler(UserException.NotFound::class)
    fun handleUserNotFound(e: UserException.NotFound): ProblemDetail = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message)


    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ProblemDetail {
        val errors = e.bindingResult.fieldErrors.joinToString { "${it.field}: ${it.defaultMessage}" }
        logger.warn(
            "Method argument not valid: {}, method: {}, index: {}",
            errors,
            e.parameter.executable.toGenericString(),
            e.parameter.parameterIndex
        )

        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errors).apply {
            setProperty("errors", e.bindingResult.fieldErrors)
        }
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(e: ConstraintViolationException): ProblemDetail {
        val errors = e.constraintViolations.associate { it.propertyPath.toString() to it.message }
        logger.warn("ConstraintViolation exception: {}", errors)

        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid arguments").apply {
            setProperty("errors", errors)
        }
    }
}