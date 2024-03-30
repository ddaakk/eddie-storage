package org.example.storage.presentation.form

import jakarta.validation.constraints.Min
import org.example.storage.presentation.valid.FilesNotEmpty
import org.springframework.web.multipart.MultipartFile

data class UploadForm(
    @field:FilesNotEmpty(message = "파일을 선택해주세요.")
    var files: List<MultipartFile> = emptyList(),
    @field:Min(1, message = "유효시간은 0보다 커야합니다.")
    var validTime: Long = 0L,
    @field:Min(1, message = "최대 다운로드 가능 횟수는 0보다 커야합니다.")
    var maxDownloads: Int = 0,
)