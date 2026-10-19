package com.seanchen.xinchat.feature.auth.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 4位数字验证码输入框组件（支持自动聚焦、方框视觉反馈与自动提交）
 */
@Composable
fun ModernCodeBoxes(
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    codeLength: Int = 4,
    onComplete: (String) -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // 隐藏的真实输入框，接收键盘输入
        BasicTextField(
            value = code,
            onValueChange = { input ->
                val digits = input.filter { it.isDigit() }.take(codeLength)
                onCodeChange(digits)
                if (digits.length == codeLength) {
                    onComplete(digits)
                }
            },
            modifier = Modifier
                .focusRequester(focusRequester)
                .size(1.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (code.length == codeLength) {
                        onComplete(code)
                    }
                }
            ),
            cursorBrush = SolidColor(Color.Transparent),
            textStyle = TextStyle(color = Color.Transparent)
        )

        // 视觉呈现的4个方框
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { focusRequester.requestFocus() }
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until codeLength) {
                val char = code.getOrNull(i)?.toString().orEmpty()
                val isFocusedBox = i == code.length.coerceAtMost(codeLength - 1)

                val borderColor by animateColorAsState(
                    targetValue = when {
                        isFocusedBox -> Color(0xFF1F2329)
                        char.isNotEmpty() -> Color(0xFF8F959E)
                        else -> Color(0xFFE5E6EB)
                    },
                    label = "codeBoxBorder"
                )

                Box(
                    modifier = Modifier
                        .size(width = 68.dp, height = 62.dp)
                        .background(Color(0xFFF7F8FA), RoundedCornerShape(8.dp))
                        .border(
                            width = if (isFocusedBox) 1.5.dp else 1.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2329)
                    )
                }
            }
        }
    }
}
