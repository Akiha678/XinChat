package com.seanchen.xinchat.feature.auth.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.seanchen.xinchat.core.designsystem.component.StartRow
import com.seanchen.xinchat.core.designsystem.theme.Primary
import com.seanchen.xinchat.core.designsystem.theme.SpaceHorizontalXLarge


@Composable
fun PhoneInputField(
    phone: String,
    onPhoneChange: (String) -> Unit,
    phoneFieldFocused: MutableState<Boolean>,
    placeholder: String = "",
    nextAction: ImeAction = ImeAction.Next,
    modifier: Modifier = Modifier
){
    StartRow(modifier = modifier) {
        Text(
            text = "+86",
            color = Primary,
            fontSize = 16.sp,
            modifier = Modifier.padding(end = SpaceHorizontalXLarge)
        )

        BasicTextField(
            value = phone,
            onValueChange = onPhoneChange,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = nextAction
            ),
            singleLine = true,
            modifier = Modifier.weight(1f)
                .onFocusChanged { phoneFieldFocused.value = it.isFocused }
        ) { innerTextField ->
            Box{
                if (phone.isEmpty()) {
                    Text(
                        text = placeholder.ifEmpty { "请输入手机号" },
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
                innerTextField()
            }
        }
    }
    FocusableDivider(focusState = phoneFieldFocused)
}