package com.seanchen.xinchat.feature.auth.component

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seanchen.xinchat.core.designsystem.component.StartRow
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalXLarge
import com.seanchen.xinchat.core.model.entity.Captcha
import com.seanchen.xinchat.core.ui.component.button.AppButton
import com.seanchen.xinchat.core.ui.component.modal.BottomModal
import com.seanchen.xinchat.feature.auth.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCaptchaDialog(
    visible: Boolean,
    captcha: Captcha,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    onRefreshCaptcha: () -> Unit = {},
    title: String = stringResource(id = R.string.security_verification),
    buttonText: String = stringResource(id = R.string.complete_verification)
){
    BottomModal(
        visible = visible,
        title = title,
        onDismiss = onDismiss
    ) {
        val imageCodeFieldFocused = remember { mutableStateOf(false) }
        val imageCode = remember { mutableStateOf("") }

        StartRow {
            BasicTextField(
                value = imageCode.value,
                onValueChange = { newValue ->
                    if (newValue.length <= 4 && (newValue.isEmpty() || newValue.all { it.isDigit() })) {
                        imageCode.value = newValue
                    }
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { imageCodeFieldFocused.value = it.isFocused }
            ) { innerTextField ->
                Box {
                    if (imageCode.value.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.image_captcha_hint),
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            }
            CaptchaImage(
                captcha = captcha,
                onRefresh = onRefreshCaptcha
            )
        }

        FocusableDivider(focusState = imageCodeFieldFocused)

        SpaceVerticalXLarge()

        AppButton(
            text = buttonText,
            onClick = { onConfirm(imageCode.value) },
            enabled = imageCode.value.length == 4
        )
    }
}

@Composable
fun CaptchaImage(
    modifier: Modifier = Modifier,
    captcha: Captcha,
    onRefresh: () -> Unit = {}
){
    if (captcha.data.isBlank()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .height(40.dp)
                .width(120.dp)
                .background(Color.LightGray.copy(alpha = 0.3f))
                .clickable(onClick = onRefresh)
        ) {
            Text(
                text = stringResource(id = R.string.refresh_captcha),
                color = Color.Gray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
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

    // 检查解码是否成功
    if (decodedBytes == null || decodedBytes.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .height(40.dp)
                .width(120.dp)
                .background(Color.LightGray.copy(alpha = 0.3f))
                .clickable(onClick = onRefresh)
        ) {
            Text(
                text = stringResource(id = R.string.refresh_captcha),
                color = Color.Gray,
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
            modifier = modifier
                .height(40.dp)
                .width(120.dp)
                .background(Color.LightGray.copy(alpha = 0.3f))
                .clickable(onClick = onRefresh)
        ) {
            Text(
                text = stringResource(id = R.string.refresh_captcha),
                color = Color.Gray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    Image(
        painter = BitmapPainter(bitmap.asImageBitmap()),
        contentDescription = stringResource(id = R.string.image_captcha_description),
        modifier = modifier
            .height(40.dp)
            .width(120.dp)
            .clickable(onClick = onRefresh)
    )
}