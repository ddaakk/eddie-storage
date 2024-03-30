package org.example.storage.presentation.valid

import jakarta.validation.Constraint
import jakarta.validation.Payload
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [FilesNotEmptyValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class FilesNotEmpty(
    val message: String = "파일을 선택해주세요.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Any>> = []
)