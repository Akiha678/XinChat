package com.seanchen.xinchat.core.ui.component.image

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seanchen.xinchat.core.ui.R

@Composable
fun Avatar(
    avatarUrl: String? = null,
    size: Dp = 36.dp,
    cornerShape: Shape = CircleShape,
    contentScale: ContentScale = ContentScale.Crop,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
){
    val finalModifier = modifier
        .size(size)
        .clip(cornerShape)
        .let{ mod ->
            if (onClick != null) {
                mod.clickable { onClick.invoke()}
            } else {
                mod
            }
        }

    val shouldShowDefaultAvatar = avatarUrl.isNullOrEmpty()

    if (shouldShowDefaultAvatar) {
        DefaultAvatar(
            size = size,

        )
    }
}

@Composable
fun DefaultAvatar(
    size: Dp,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_my_fill),
            contentDescription = "默认头像",
            modifier = Modifier.size(size * 0.5f),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun SmallAvatar(
    avatarUrl: String? = null,
    size: Dp = 36.dp,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {

}