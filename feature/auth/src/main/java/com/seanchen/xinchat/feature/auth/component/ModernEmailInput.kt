package com.seanchen.xinchat.feature.auth.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 现代风格的邮箱下划线输入框
 */
@Composable
fun ModernEmailInput(
    email: String,
    onEmailChange: (String) -> Unit,
    placeholder: String = "请输入邮箱",
    imeAction: ImeAction = ImeAction.Done,
    onDone: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    val lineColor by animateColorAsState(
        targetValue = if (isFocused) Color(0xFF1F2329) else Color(0xFFE5E6EB),
        label = "emailLineColor"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = email,
                onValueChange = onEmailChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused },
                textStyle = TextStyle(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF1F2329)
                ),
                cursorBrush = SolidColor(Color(0xFF1F2329)),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = imeAction
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onDone() },
                    onNext = { onDone() }
                ),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (email.isEmpty()) {
                            Text(
                                text = placeholder,
                                fontSize = 17.sp,
                                color = Color(0xFFC4C7CC)
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        // 下划线
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(lineColor)
        )
    }
}
