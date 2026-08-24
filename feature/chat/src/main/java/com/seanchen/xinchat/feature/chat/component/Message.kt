package com.seanchen.xinchat.feature.chat.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalSmall
import com.seanchen.xinchat.core.model.entity.Msg
import kotlinx.coroutines.delay


/**
 * 单条消息组件
 * @param msg 消息数据
 * @param isUserMe 是否为当前用户发送的消息
 */
@Composable
fun Message(
    msg: Msg,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    isNewMessage: Boolean = false,
    onAnimationFinished: () -> Unit = {}
){
    val spaceBetweenAuthors = if (isLastMessageByAuthor) Modifier.padding(top = SpaceVerticalSmall) else Modifier
    var visible by remember { mutableStateOf(!isNewMessage) }

    LaunchedEffect(isNewMessage) {
        if (isNewMessage) {
            visible = true
            delay(500)
            onAnimationFinished()
        }
    }

    AnimatedVisibility(
        visible = visible,
        entry = slideOutHorizontally (
            slideInHorizontally { fullWidth -> if (isUserMe) fullWidth else -fullWidth },

        )
    ) { }
}