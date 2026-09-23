package com.seanchen.xinchat.feature.user.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * 头像文件校验结果密封接口
 */
sealed interface AvatarValidationResult {
    /**
     * 校验成功，携带准备好上传的表单数据部件
     */
    data class Success(val part: MultipartBody.Part) : AvatarValidationResult

    /**
     * 格式不支持，仅支持 jpg/jpeg/png/gif/webp
     */
    data object InvalidFormat : AvatarValidationResult

    /**
     * 文件超出 5MB 大小限制
     */
    data object SizeExceeded : AvatarValidationResult

    /**
     * 文件读取失败
     */
    data object ReadFailed : AvatarValidationResult
}

/**
 * 头像文件处理工具
 */
object AvatarFileUtil {
    /**
     * 头像文件最大限制 5MB
     */
    const val MAX_AVATAR_SIZE_BYTES = 5 * 1024 * 1024L

    private val SUPPORTED_MIME_TYPES = setOf(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/webp"
    )

    private val SUPPORTED_EXTENSIONS = setOf(
        "jpg", "jpeg", "png", "gif", "webp"
    )

    /**
     * 校验相册选取的 Uri 并在合法时构建 MultipartBody.Part
     *
     * @param context Android 上下文
     * @param uri 用户选取的图片 Uri
     * @return 校验与构建结果
     */
    fun validateAndCreatePart(context: Context, uri: Uri): AvatarValidationResult {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri) ?: getMimeTypeFromUri(uri)

        val isValidMime = mimeType?.lowercase() in SUPPORTED_MIME_TYPES
        val extension = getExtensionFromUri(context, uri)?.lowercase()
        val isValidExt = extension in SUPPORTED_EXTENSIONS

        if (!isValidMime && !isValidExt) {
            return AvatarValidationResult.InvalidFormat
        }

        // 前置大小检测
        val fileSize = getFileSize(context, uri)
        if (fileSize != null && fileSize > MAX_AVATAR_SIZE_BYTES) {
            return AvatarValidationResult.SizeExceeded
        }

        // 读取字节数组并严格二次校验
        val bytes = runCatching {
            contentResolver.openInputStream(uri)?.use { it.readBytes() }
        }.getOrNull() ?: return AvatarValidationResult.ReadFailed

        if (bytes.size > MAX_AVATAR_SIZE_BYTES) {
            return AvatarValidationResult.SizeExceeded
        }

        val effectiveMime = mimeType ?: "image/jpeg"
        val fileName = getFileName(context, uri) ?: "avatar.${extension ?: "jpg"}"

        val requestBody = bytes.toRequestBody(effectiveMime.toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", fileName, requestBody)

        return AvatarValidationResult.Success(part)
    }

    private fun getFileSize(context: Context, uri: Uri): Long? {
        return runCatching {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1 && cursor.moveToFirst()) {
                    cursor.getLong(sizeIndex)
                } else null
            }
        }.getOrNull()
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        return runCatching {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    cursor.getString(nameIndex)
                } else null
            }
        }.getOrNull()
    }

    private fun getExtensionFromUri(context: Context, uri: Uri): String? {
        val fileName = getFileName(context, uri)
        if (fileName != null && fileName.contains(".")) {
            return fileName.substringAfterLast('.', "")
        }
        val path = uri.path
        if (path != null && path.contains(".")) {
            return path.substringAfterLast('.', "")
        }
        return null
    }

    private fun getMimeTypeFromUri(uri: Uri): String? {
        val path = uri.path ?: return null
        return when (path.substringAfterLast('.', "").lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            else -> null
        }
    }
}
