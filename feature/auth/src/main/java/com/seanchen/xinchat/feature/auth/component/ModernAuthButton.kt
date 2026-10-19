package com.seanchen.xinchat.feature.auth.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 现代风格主操作按钮（支持高亮与置灰禁用状态、加载态）
 */
@Composable
fun ModernAuthButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (enabled) Color(0xFF1F2329) else Color(0xFFF2F3F5),
        label = "btnBgColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (enabled) Color.White else Color(0xFFC4C7CC),
        label = "btnTextColor"
    )

    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .background(backgroundColor, shape)
            .clickable(
                enabled = enabled && !isLoading,
                interactionSource = remember { MutableInteractionSource() },
                indication = if (enabled) ripple() else null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = if (enabled) Color.White else Color(0xFFC4C7CC),
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                text = text,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = if (enabled) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}
