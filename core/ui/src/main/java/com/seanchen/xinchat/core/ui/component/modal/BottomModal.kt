package com.seanchen.xinchat.core.ui.component.modal

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seanchen.xinchat.core.designsystem.theme.SpacePaddingLarge
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalLarge
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalMedium
import com.seanchen.xinchat.core.designsystem.theme.TitleLarge
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomModal(
    visible: Boolean,
    title: String? = null,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
    showDragIndicator: Boolean = true,
    horizontalPadding: Dp = SpacePaddingLarge,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    titleStyle: TextStyle = TitleLarge,
    indicatorColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
    content: @Composable ColumnScope.() -> Unit
){
    // 添加协程作用域用于动画控制
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(visible) {
        if (!visible && sheetState.isVisible) {
            coroutineScope.launch {
                sheetState.hide()
            }
        }
    }

    if (visible || sheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = containerColor,
            shape = shape,
            dragHandle = {
                if (showDragIndicator) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = SpacePaddingLarge),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp, 4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(indicatorColor)
                        )
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = horizontalPadding)
                    .wrapContentHeight()
                    .animateContentSize()
            ) {
                title?.let {
                    Text(
                        text = it,
                        style = titleStyle,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    SpaceVerticalLarge()
                }
                content()
                SpaceVerticalMedium()
            }
        }
    }
}