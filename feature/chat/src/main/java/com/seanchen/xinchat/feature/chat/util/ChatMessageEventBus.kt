package com.seanchen.xinchat.feature.chat.util

import com.seanchen.xinchat.core.model.entity.Msg
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 在聊天详情页和会话列表之间同步本地发送成功的消息摘要。
 *
 * 列表和详情页属于不同的 Navigation 3 entry，各自拥有独立的 ViewModel，
 * 不能依赖某一个页面的 WebSocket 回调来刷新另一个页面。
 */
@Singleton
class ChatMessageEventBus @Inject constructor() {
    private val _sentMessages = MutableSharedFlow<Msg>(
        extraBufferCapacity = 32,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val sentMessages: SharedFlow<Msg> = _sentMessages.asSharedFlow()

    fun publishSentMessage(message: Msg) {
        _sentMessages.tryEmit(message)
    }
}
