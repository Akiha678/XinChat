package com.seanchen.xinchat.feature.auth.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seanchen.xinchat.core.designsystem.component.TopColumn
import com.seanchen.xinchat.core.designsystem.theme.SpaceHorizontalXXLarge
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalXXLarge
import com.seanchen.xinchat.core.ui.component.scaffold.AppScaffold

@Composable
fun AnimatedAuthPage(
    title: String,
    modifier: Modifier = Modifier,
    withFadeIn: Boolean = false,
    onBackClick: (() -> Unit) = {},
    content: @Composable () -> Unit
) {
    val isAnimationPlayed = rememberSaveable { mutableStateOf(false) }

    val animationState = remember{
        MutableTransitionState(isAnimationPlayed.value)
    }

    LaunchedEffect(Unit) {
        if (!isAnimationPlayed.value) {
            // 首次进入，触发动画
            animationState.targetState = true
            // 标记动画已播放
            isAnimationPlayed.value = true
        } else {
            animationState.targetState = true
        }
    }

    AppScaffold(
        backgroundColor = MaterialTheme.colorScheme.surface,
        onBackClick = onBackClick
    ) {
        AnimatedVisibility(
            visibleState = animationState,
            enter = slideInVertically (
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 400)
            )
        ) {
            TopColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = SpaceHorizontalXXLarge)
                    .padding(top = SpaceVerticalXXLarge),

            ){
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.height(64.dp))

                content()
            }
        }
    }

}