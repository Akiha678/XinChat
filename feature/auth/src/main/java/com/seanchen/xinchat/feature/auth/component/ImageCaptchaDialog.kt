package com.seanchen.xinchat.feature.auth.component

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seanchen.widget.ui.button.AppButton
import com.seanchen.widget.ui.modal.BottomModal
import com.seanchen.xinchat.core.designsystem.theme.Primary
import com.seanchen.xinchat.core.designsystem.theme.PrimaryDefault
import com.seanchen.xinchat.core.model.entity.Captcha
import com.seanchen.xinchat.feature.auth.R

/**
 * 图形验证码防人机底部弹窗
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCaptchaDialog(
    visible: Boolean,
    captcha: Captcha,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    onRefreshCaptcha: () -> Unit = {},
    isLoading: Boolean = false,
    isVerifying: Boolean = false,
    title: String = stringResource(id = R.string.security_verification),
    buttonText: String = stringResource(id = R.string.complete_verification)
) {
    BottomModal(
        visible = visible,
        title = title,
        onDismiss = onDismiss
    ) {
        val imageCode = remember { mutableStateOf("") }
        val imageCodeFieldFocused = remember { mutableStateOf(false) }

        // 当重新获取/换了一张验证码时，清空当前已输入的字符
        LaunchedEffect(captcha.captchaId) {
            imageCode.value = ""
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 图形验证码输入框（4位字母/数字）
                BasicTextField(
                    value = imageCode.value,
                    onValueChange = { newValue ->
                        if (newValue.length <= 4 && (newValue.isEmpty() || newValue.all { it.isLetterOrDigit() })) {
                            imageCode.value = newValue.uppercase()
                        }
                    },
                    textStyle = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp,
                        color = Color(0xFF1F2329)
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Ascii,
                        capitalization = KeyboardCapitalization.Characters,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (imageCode.value.length == 4 && !isVerifying && !isLoading) {
                                onConfirm(imageCode.value)
                            }
                        }
                    ),
                    cursorBrush = SolidColor(PrimaryDefault),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { imageCodeFieldFocused.value = it.isFocused }
                ) { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (imageCode.value.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.image_captcha_hint),
                                color = Color(0xFF8F959E),
                                fontSize = 15.sp,
                                letterSpacing = 0.sp
                            )
                        }
                        innerTextField()
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // 图形验证码图片
                CaptchaImage(
                    captcha = captcha,
                    isLoading = isLoading,
                    onRefresh = onRefreshCaptcha
                )
            }

            FocusableDivider(focusState = imageCodeFieldFocused)

            Spacer(modifier = Modifier.height(24.dp))

            AppButton(
                text = if (isVerifying) "验证中..." else buttonText,
                onClick = { onConfirm(imageCode.value) },
                enabled = imageCode.value.length == 4 && !isVerifying && !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * 图形验证码图片渲染组件（点击可刷新）
 */
@Composable
fun CaptchaImage(
    modifier: Modifier = Modifier,
    captcha: Captcha,
    isLoading: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    val boxModifier = modifier
        .height(40.dp)
        .width(120.dp)
        .clip(RoundedCornerShape(6.dp))
        .border(1.dp, Color(0xFFE5E6EB), RoundedCornerShape(6.dp))

    if (isLoading || captcha.data.isBlank()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = boxModifier
                .background(Color(0xFFF7F8FA))
                .clickable(enabled = !isLoading, onClick = onRefresh)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = PrimaryDefault
                )
            } else {
                Text(
                    text = stringResource(id = R.string.refresh_captcha),
                    color = Color(0xFF8F959E),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }

    val imageData = captcha.data
    val pureBase64 = if (imageData.contains("base64,")) {
        imageData.substringAfter("base64,")
    } else {
        imageData
    }

    val decodedBytes = try {
        Base64.decode(pureBase64, Base64.DEFAULT)
    } catch (e: Exception) {
        Log.e("CaptchaImage", "Base64解码失败: ${e.message}")
        null
    }

    if (decodedBytes == null || decodedBytes.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = boxModifier
                .background(Color(0xFFF7F8FA))
                .clickable(onClick = onRefresh)
        ) {
            Text(
                text = stringResource(id = R.string.refresh_captcha),
                color = Color(0xFF8F959E),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val bitmap = try {
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        Log.e("CaptchaImage", "位图创建失败: ${e.message}")
        null
    }

    if (bitmap == null) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = boxModifier
                .background(Color(0xFFF7F8FA))
                .clickable(onClick = onRefresh)
        ) {
            Text(
                text = stringResource(id = R.string.refresh_captcha),
                color = Color(0xFF8F959E),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    Image(
        painter = BitmapPainter(bitmap.asImageBitmap()),
        contentDescription = stringResource(id = R.string.image_captcha_description),
        modifier = boxModifier
            .clickable(onClick = onRefresh)
    )
}
