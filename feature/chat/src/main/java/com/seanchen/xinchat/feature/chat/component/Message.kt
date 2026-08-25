package com.seanchen.xinchat.feature.chat.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seanchen.xinchat.core.designsystem.theme.Primary
import com.seanchen.xinchat.core.designsystem.theme.ShapeMedium
import com.seanchen.xinchat.core.designsystem.theme.SpaceHorizontalSmall
import com.seanchen.xinchat.core.designsystem.theme.SpacePaddingMedium
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalSmall
import com.seanchen.xinchat.core.designsystem.theme.TextWhite
import com.seanchen.xinchat.core.designsystem.theme.appTextColors
import com.seanchen.xinchat.core.model.entity.Msg
import com.seanchen.xinchat.feature.chat.R
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
        enter = fadeIn(animationSpec = tween(220)) + slideInHorizontally(
            animationSpec = tween(260, easing = FastOutSlowInEasing),
            initialOffsetX = { width -> if (isUserMe) width / 2 else -width / 2 }
        ),
        exit = fadeOut(animationSpec = tween(160)) + slideOutHorizontally(
            animationSpec = tween(160),
            targetOffsetX = { width -> if (isUserMe) width / 2 else -width / 2 }
        )
    ) {
        MessageRow(
            msg = msg,
            isUserMe = isUserMe,
            showAuthor = isFirstMessageByAuthor,
            modifier = Modifier
                .fillMaxWidth()
                .then(spaceBetweenAuthors)
                .padding(horizontal = SpacePaddingMedium, vertical = 2.dp)
        )
    }
}

@Composable
private fun MessageRow(
    msg: Msg,
    isUserMe: Boolean,
    showAuthor: Boolean,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        horizontalArrangement = if (isUserMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUserMe) {
            MessageAvatar(
                name = msg.nickName.ifBlank { stringResource(R.string.chat_online_support) },
                visible = showAuthor
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(SpaceHorizontalSmall))
        }

        Column(
            horizontalAlignment = if (isUserMe) Alignment.End else Alignment.Start
        ) {
            if (!isUserMe && showAuthor && msg.nickName.isNotBlank()) {
                Text(
                    text = msg.nickName,
                    style = MaterialTheme.typography.bodySmall,
                    color = appTextColors().tertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            Text(
                text = msg.content?.data?.takeIf { it.isNotBlank() }
                    ?: stringResource(R.string.chat_default_message),
                modifier = Modifier
                    .widthIn(max = 284.dp)
                    .clip(ShapeMedium)
                    .background(
                        if (isUserMe) Primary else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .padding(horizontal = SpacePaddingMedium, vertical = SpaceVerticalSmall),
                color = if (isUserMe) TextWhite else appTextColors().primary,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (isUserMe) {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(SpaceHorizontalSmall))
            MessageAvatar(
                name = msg.nickName.ifBlank { stringResource(R.string.chat_me) },
                visible = showAuthor
            )
        }
    }
}

@Composable
private fun MessageAvatar(
    name: String,
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    if (!visible) {
        Box(modifier = modifier.size(34.dp))
        return
    }

    Box(
        modifier = modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.take(1),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 1
        )
    }
}
