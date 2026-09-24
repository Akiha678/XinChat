package com.seanchen.xinchat.feature.user.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.seanchen.xinchat.core.designsystem.R as DesignR
import com.seanchen.xinchat.feature.user.R

private const val MAX_NICKNAME_LENGTH = 80

/**
 * 修改昵称对话框
 *
 * @param visible 是否可见
 * @param currentNickname 当前昵称，用于初次回填
 * @param isSubmitting 是否正在向服务端提交
 * @param onDismiss 关闭弹窗回调
 * @param onConfirm 确认修改回调
 */
@Composable
fun EditNicknameDialog(
    visible: Boolean,
    currentNickname: String,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    if (!visible) return

    var text by remember(currentNickname) { mutableStateOf(currentNickname) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val trimmed = text.trim()
    val isLengthValid = trimmed.length <= MAX_NICKNAME_LENGTH
    val canSubmit = trimmed.isNotEmpty() && isLengthValid && !isSubmitting

    AlertDialog(
        onDismissRequest = {
            if (!isSubmitting) onDismiss()
        },
        title = {
            Text(
                text = stringResource(id = R.string.profile_edit_nickname),
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { newText ->
                        if (newText.length <= MAX_NICKNAME_LENGTH) {
                            text = newText
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    enabled = !isSubmitting,
                    placeholder = {
                        Text(text = stringResource(id = R.string.profile_edit_nickname_hint))
                    },
                    supportingText = {
                        Text(text = "${text.length}/$MAX_NICKNAME_LENGTH")
                    },
                    trailingIcon = {
                        if (text.isNotEmpty() && !isSubmitting) {
                            IconButton(onClick = { text = "" }) {
                                Icon(
                                    painter = painterResource(id = DesignR.drawable.ic_close),
                                    contentDescription = "清空",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (canSubmit) {
                                onConfirm(trimmed)
                            }
                        }
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(trimmed) },
                enabled = canSubmit
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = stringResource(id = R.string.profile_confirm))
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSubmitting
            ) {
                Text(text = stringResource(id = R.string.profile_cancel))
            }
        }
    )
}
