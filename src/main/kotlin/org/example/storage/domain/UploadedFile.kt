package org.example.storage.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.util.*

@Entity
class UploadedFile(
    val fileName: String,
    val downloadPath: String,
    var downloadAbleTime: LocalDateTime,
    var maxDownloads: Int,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    fun decreaseMaxDownloads() {
        if (this.maxDownloads > 0) {
            this.maxDownloads--
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is UploadedFile) {
            return false
        }

        return this.id != null && Objects.equals(this.id, other.id)
    }

    override fun hashCode(): Int {
        return Objects.hash(this.id)
    }
}