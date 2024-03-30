package org.example.storage.application

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import io.github.oshai.kotlinlogging.KotlinLogging

import java.io.IOException
import java.time.LocalDateTime
import java.util.function.Consumer
import org.example.storage.common.factory.FileFormatter
import org.example.storage.common.factory.RandomStringGenerator
import org.example.storage.domain.UploadedFile
import org.example.storage.infrastructure.UploadedFileRepository
import org.example.storage.presentation.form.UploadForm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile


@Service
class UploadService(
    private val amazonS3: AmazonS3,
    private val uploadedFileRepository: UploadedFileRepository,
    @Value("\${cloud.aws.s3.bucket}") private val bucket: String
) {

    private val logger = KotlinLogging.logger {}

    @Transactional
    fun uploadFile(uploadForm: UploadForm): List<String> {
        val (files, validTime, maxDownloads) = uploadForm

        validFiles(files)

        val downloadPaths: MutableList<String> = ArrayList()

        files.forEach(Consumer { file: MultipartFile ->
            val fileName: String = FileFormatter.buildFileName(file.originalFilename!!)
            val objectMetadata = ObjectMetadata()
            objectMetadata.contentLength = file.size
            objectMetadata.contentType = file.contentType

            try {
                file.inputStream.use { inputStream ->
                    amazonS3.putObject(
                        PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
                    )
                }
            } catch (e: IOException) {
                logger.error { "파일 업로드에 실패했습니다." }
                throw IllegalArgumentException("파일 업로드에 실패했습니다.")
            }
            val downloadPath = RandomStringGenerator.generateRandomString()
            downloadPaths.add(downloadPath)

            val uploadedFile = UploadedFile(
                fileName = fileName,
                downloadPath = downloadPath,
                downloadAbleTime = LocalDateTime.now().plusMinutes(validTime),
                maxDownloads = maxDownloads
            )
            uploadedFileRepository.save(uploadedFile)

            logger.info { "파일 업로드에 성공하였습니다. " +
                    "파일명: $fileName, " +
                    "다운로드 경로: $downloadPath, " +
                    "다운로드 가능 시간: ${uploadedFile.downloadAbleTime}, " +
                    "최대 다운로드 횟수: ${uploadedFile.maxDownloads}" }
        })

        return downloadPaths
    }

    private fun validFiles(files: List<MultipartFile>) {
        if (files.isEmpty()) {
            throw IllegalArgumentException("업로드할 파일이 존재하지 않습니다.")
        }
    }
}