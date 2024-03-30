package org.example.storage.presentation.valid

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.web.multipart.MultipartFile

class FilesNotEmptyValidator : ConstraintValidator<FilesNotEmpty, List<MultipartFile>> {
    override fun isValid(files: List<MultipartFile>?, context: ConstraintValidatorContext): Boolean {
        if (files.isNullOrEmpty()) return false
        return files.all { file -> !file.isEmpty }
    }
}