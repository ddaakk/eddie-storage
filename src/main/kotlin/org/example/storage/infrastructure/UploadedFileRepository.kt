package org.example.storage.infrastructure

import org.example.storage.domain.UploadedFile
import org.springframework.data.jpa.repository.JpaRepository

interface UploadedFileRepository : JpaRepository<UploadedFile, Long> {
    fun findByDownloadPath(path: String): UploadedFile?
}