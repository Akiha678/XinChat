package com.seanchen.xinchat.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.seanchen.xinchat.core.designsystem.R

/**
 * 通用头像组件
 *
 * 优先加载 [avatarUrl] 网络图片，如果为空、加载中或加载失败则回退展示默认占位头像。
 */
@Composable
fun AppAvatar(
    avatarUrl: String?,
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

    if (avatarUrl.isNullOrBlank()) {
        DefaultAvatar(
            size = size,
            modifier = finalModifier
        )
    } else {
        val context = LocalContext.current
        val imageRequest = ImageRequest.Builder(context)
            .data(avatarUrl)
            .crossfade(true)
            .build()

        SubcomposeAsyncImage(
            model = imageRequest,
            contentDescription = "用户头像",
            contentScale = contentScale,
            modifier = finalModifier,
            loading = {
                DefaultAvatar(
                    size = size,
                    modifier = Modifier.matchParentSize()
                )
            },
            error = {
                DefaultAvatar(
                    size = size,
                    modifier = Modifier.matchParentSize()
                )
            }
        )
    }
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
        Icon(
            painter = painterResource(id = R.drawable.ic_default_avatar),
            contentDescription = "默认头像",
            modifier = Modifier.size(size * 0.5f),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
