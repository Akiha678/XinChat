package com.seanchen.xinchat.feature.chat.util

import com.seanchen.xinchat.core.util.storage.MMKVUtils
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 管理会话设置（置顶、免打扰、本地清空记录截断点等）。
 * 使用 MMKV 持久化并提供响应式通知。
 */
@Singleton
class ChatSettingsManager @Inject constructor() {

    private val _settingsChangedEvent = MutableSharedFlow<Long>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val settingsChangedEvent: SharedFlow<Long> = _settingsChangedEvent.asSharedFlow()

    fun isPinned(sessionId: Long): Boolean {
        if (sessionId <= 0L) return false
        return MMKVUtils.getBoolean("chat_pinned_$sessionId", false)
    }

    fun setPinned(sessionId: Long, pinned: Boolean) {
        if (sessionId <= 0L) return
        MMKVUtils.putBoolean("chat_pinned_$sessionId", pinned)
        _settingsChangedEvent.tryEmit(sessionId)
    }

    fun isMuted(sessionId: Long): Boolean {
        if (sessionId <= 0L) return false
        return MMKVUtils.getBoolean("chat_muted_$sessionId", false)
    }

    fun setMuted(sessionId: Long, muted: Boolean) {
        if (sessionId <= 0L) return
        MMKVUtils.putBoolean("chat_muted_$sessionId", muted)
        _settingsChangedEvent.tryEmit(sessionId)
    }

    /**
     * 清空指定会话的本地历史记录截断时间戳。
     */
    fun clearHistory(sessionId: Long) {
        if (sessionId <= 0L) return
        val now = System.currentTimeMillis()
        MMKVUtils.putLong("chat_cleared_at_$sessionId", now)
        _settingsChangedEvent.tryEmit(sessionId)
    }

    /**
     * 获取会话清空历史的时间戳（毫秒），早于此时间戳的消息将被过滤。
     */
    fun getClearedTime(sessionId: Long): Long {
        if (sessionId <= 0L) return 0L
        return MMKVUtils.getLong("chat_cleared_at_$sessionId", 0L)
    }
}
