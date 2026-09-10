package com.seanchen.xinchat.feature.chat.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue

@Composable
fun ChatInputArea(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 显示表情选择器
    val (showEmojiSelector, setShowEmojiSelector) = remember {
        mutableStateOf(
            false
        )
    }
    // 显示功能选择器
    val (showFunctionSelector, setShowFunctionSelector) = remember {
        mutableStateOf(
            false
        )
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val inputFieldFocusRequester = remember { FocusRequester() }

    val hideKeyboard = {
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    val handleEmojiSelectorToggle: (Boolean) -> Unit = { show ->
        if (show) {
            hideKeyboard()
            setShowFunctionSelector(false)
        }
        setShowEmojiSelector(show)
    }

    val handleFunctionSelectorToggle: (Boolean) -> Unit = { show ->
        if (show) {
            hideKeyboard()
            setShowEmojiSelector(false)
        }
        setShowFunctionSelector(show)
    }

    val emojiSelectorHeight by animateDpAsState(
        targetValue = if (showEmojiSelector) 240.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "emojiSelectorHeight"
    )

    val functionSelectorHeight by animateDpAsState(
        targetValue = if (showFunctionSelector) 130.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "functionSelectorHeight"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            // 键盘弹出时导航栏被键盘覆盖，这里只保留未被键盘遮挡的导航栏高度，
            // 避免输入栏与键盘之间出现多余空隙。
            .windowInsetsPadding(WindowInsets.navigationBars.exclude(WindowInsets.ime))
    ) {
        ChatInputBar(
            inputText = inputText,
            onInputTextChange = onInputTextChange,
            showEmojiSelector = showEmojiSelector,
            onEmojiSelectorToggle = handleEmojiSelectorToggle,
            onSendMessage = onSendMessage,
            onFunctionSelectorToggle = handleFunctionSelectorToggle,
            showFunctionSelector = showFunctionSelector,
            focusRequester = inputFieldFocusRequester,
            onFocusRequested = {
                setShowEmojiSelector(false)
                setShowFunctionSelector(false)
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (emojiSelectorHeight > 0.dp) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(emojiSelectorHeight)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                EmojiSelector(
                    onEmojiSelected = { emoji ->
                        onInputTextChange(inputText + emoji)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        if (functionSelectorHeight > 0.dp) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(functionSelectorHeight)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
            ){
                FunctionSelector(
                    onFunctionSelected = { function ->
                        setShowFunctionSelector(false)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}