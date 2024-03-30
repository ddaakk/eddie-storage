package org.example.storage.application

import com.amazonaws.services.s3.AmazonS3
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.LocalDateTime
import org.example.storage.application.dto.DownloadFile
import org.example.storage.domain.UploadedFile
import org.example.storage.infrastructure.UploadedFileRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadService(
    private val amazonS3: AmazonS3,
    private val uploadedFileRepository: UploadedFileRepository,
    @Value("\${cloud.aws.s3.bucket}") private val bucket: String
) {

    private val logger = KotlinLogging.logger {}

    @Transactional
    fun downloadFile(path: String): DownloadFile {
        val uploadedFile =
            uploadedFileRepository.findByDownloadPath(path) ?: throw IllegalArgumentException("파일이 존재하지 않습니다.")
        val uploadedFileName = uploadedFile.fileName

        validUploadedFile(uploadedFile, uploadedFileName)

        val downloadFile = DownloadFile(
            fileName = uploadedFileName,
            fileArray = getFileFromStorage(uploadedFileName)
        )

        uploadedFile.decreaseMaxDownloads()

        logger.info { "파일이 다운로드 되었습니다. " +
                "파일명: $uploadedFileName, " +
                "남은 다운로드 횟수: ${uploadedFile.maxDownloads}, " +
                "다운로드 가능 시간: ${uploadedFile.downloadAbleTime}" }

        return downloadFile
    }

    @Cacheable("files")
    fun getFileFromStorage(uploadedFileName: String): ByteArray =
        amazonS3.getObject(bucket, uploadedFileName).objectContent.readAllBytes()

    private fun validUploadedFile(uploadedFile: UploadedFile, uploadedFileName: String) {
        if (uploadedFile.maxDownloads < 1) {
            throw IllegalArgumentException("다운로드 횟수를 초과했습니다.")
        }

        if (uploadedFile.downloadAbleTime.isBefore(LocalDateTime.now())) {
            throw IllegalArgumentException("다운로드 기간이 만료되었습니다.")
        }

        if (!amazonS3.doesObjectExist(bucket, uploadedFileName)) {
            throw IllegalArgumentException("파일이 존재하지 않습니다.")
        }
    }
}