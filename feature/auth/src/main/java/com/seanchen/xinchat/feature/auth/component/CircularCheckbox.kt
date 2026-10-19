package com.seanchen.xinchat.feature.auth.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seanchen.xinchat.feature.auth.R

/**
 * 圆形复选框，用于协议勾选等场景
 */
@Composable
fun CircularCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    activeColor: Color = Color(0xFF1A1A1A),
    inactiveBorderColor: Color = Color(0xFFC4C7CC)
) {
    val interactionSource = remember { MutableInteractionSource() }
    val backgroundColor by animateColorAsState(
        targetValue = if (checked) activeColor else Color.Transparent,
        label = "checkboxBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) activeColor else inactiveBorderColor,
        label = "checkboxBorder"
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .border(width = 1.dp, color = borderColor, shape = CircleShape)
            .background(backgroundColor, shape = CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, radius = size),
                onClick = { onCheckedChange(!checked) }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(size * 0.65f)
            )
        }
    }
}
