package com.seanchen.xinchat.feature.auth.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seanchen.xinchat.feature.auth.R

/**
 * 现代风格密码输入框（支持明暗文切换、下划线聚焦动画）
 */
@Composable
fun ModernPasswordInput(
    password: String,
    onPasswordChange: (String) -> Unit,
    placeholder: String = "请输入密码",
    imeAction: ImeAction = ImeAction.Done,
    onDone: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val lineColor by animateColorAsState(
        targetValue = if (isFocused) Color(0xFF1F2329) else Color(0xFFE5E6EB),
        label = "pwdLineColor"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = password,
                onValueChange = onPasswordChange,
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { isFocused = it.isFocused },
                textStyle = TextStyle(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF1F2329)
                ),
                cursorBrush = SolidColor(Color(0xFF1F2329)),
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = imeAction
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onDone() },
                    onNext = { onDone() }
                ),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (password.isEmpty()) {
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

            // 眼睛图标切换明暗文
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = false, radius = 18.dp),
                        onClick = { isPasswordVisible = !isPasswordVisible }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isPasswordVisible) R.drawable.ic_eye else R.drawable.ic_eye_hide
                    ),
                    contentDescription = if (isPasswordVisible) "隐藏密码" else "显示密码",
                    tint = Color(0xFF8F959E),
                    modifier = Modifier.size(20.dp)
                )
            }
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
