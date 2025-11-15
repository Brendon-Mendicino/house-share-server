package com.github.brendonmendicino.houseshareserver.validator

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [NotBlankIfPresentValidator::class])
@Target(FIELD, VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class NotBlankIfPresent(
    val message: String = "{jakarta.validation.constraints.NotBlank.message}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class NotBlankIfPresentValidator : ConstraintValidator<NotBlankIfPresent, String?> {
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        return value == null || value.isNotBlank()
    }
}