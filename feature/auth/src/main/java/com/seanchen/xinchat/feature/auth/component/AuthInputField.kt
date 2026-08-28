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

@Composable
fun AuthInputField(
    value: String,
    onValueChange: (String) -> Unit,
    fieldFocused: MutableState<Boolean>,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    nextAction: ImeAction = ImeAction.Next,
    modifier: Modifier = Modifier,
    leadingContent: @Composable (() -> Unit)? = null
) {
    StartRow(modifier = modifier) {
        if (leadingContent != null) {
            leadingContent()
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = nextAction
            ),
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { fieldFocused.value = it.isFocused }
        ) { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
                innerTextField()
            }
        }
    }

    FocusableDivider(focusState = fieldFocused)
}
