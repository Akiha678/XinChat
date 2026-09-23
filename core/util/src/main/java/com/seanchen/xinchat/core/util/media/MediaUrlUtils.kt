package com.seanchen.xinchat.core.util.media

import com.seanchen.xinchat.core.util.BuildConfig

/**
 * 媒体资源 URL 处理工具
 */
object MediaUrlUtils {
    /**
     * 将后端返回的相对路径补全为完整可访问的 HTTP(S) 地址
     *
     * @param path 后端返回的相对路径或完整 URL
     * @return 完整的访问地址，如果入参为空则返回 null
     */
    fun toFullUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        if (path.startsWith("http://") || path.startsWith("https://")) return path

        val base = BuildConfig.BASE_URL.removeSuffix("/")
        val normalizedPath = if (path.startsWith("/")) path else "/$path"
        return "$base$normalizedPath"
    }
}

/**
 * 将相对路径字符串扩展转换为完整访问地址
 */
fun String?.toFullMediaUrl(): String? = MediaUrlUtils.toFullUrl(this)
