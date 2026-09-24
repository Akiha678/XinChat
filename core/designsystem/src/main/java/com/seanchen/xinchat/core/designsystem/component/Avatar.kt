package com.seanchen.xinchat.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.seanchen.xinchat.core.designsystem.R

/**
 * 通用头像组件
 *
 * 支持传入网络图片地址 (String)、本地相册图片 (Uri)、文件 (File) 等多种数据源 [avatarUrl]。
 */
@Composable
fun AppAvatar(
    avatarUrl: Any?,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    cornerShape: Shape = CircleShape,
    contentScale: ContentScale = ContentScale.Crop,
    onClick: (() -> Unit)? = null,
) {
    val clickableModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    val finalModifier = clickableModifier
        .size(size)
        .clip(cornerShape)

    val isBlankModel = avatarUrl == null || (avatarUrl is String && avatarUrl.isBlank())

    Box(
        modifier = finalModifier
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (isBlankModel) {
            DefaultAvatarIcon(size = size)
        } else {
            val context = LocalContext.current
            val imageRequest = remember(avatarUrl, context) {
                ImageRequest.Builder(context)
                    .data(avatarUrl)
                    .crossfade(true)
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.ic_default_avatar)
                    .fallback(R.drawable.ic_default_avatar)
                    .build()
            }

            AsyncImage(
                model = imageRequest,
                contentDescription = "用户头像",
                contentScale = contentScale,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}

/**
 * 默认头像图标
 */
@Composable
internal fun DefaultAvatarIcon(size: Dp) {
    Icon(
        painter = painterResource(id = R.drawable.ic_default_avatar),
        contentDescription = "默认头像",
        modifier = Modifier.size(size * 0.5f),
        tint = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

/**
 * 默认占位头像组件
 */
@Composable
fun DefaultAvatar(
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        DefaultAvatarIcon(size = size)
    }
}
