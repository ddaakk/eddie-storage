package org.example.storage.common.exception

import jakarta.validation.ConstraintViolationException
import org.example.storage.common.dto.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException

@ControllerAdvice
class GlobalExceptionHandler {
    /**
     * 파일 업로드 용량 초과시 발생
     */
    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxUploadSizeExceededException(
        e: MaxUploadSizeExceededException?,
        model: Model
    ): String {
        model.addAttribute("errorMessage", "파일 업로드 용량을 초과하였습니다.")
        return "error"
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        e: IllegalArgumentException?,
        model: Model
    ): String {
        model.addAttribute("errorMessage", e?.message)
        return "error"
    }
}