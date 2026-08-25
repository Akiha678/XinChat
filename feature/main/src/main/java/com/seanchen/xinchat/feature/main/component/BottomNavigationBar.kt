package com.seanchen.xinchat.feature.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.seanchen.xinchat.core.designsystem.theme.PrimaryDefault
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalXSmall
import com.seanchen.xinchat.feature.main.model.TopLevelDestination

@Composable
fun BottomNavigationBar(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (Int) -> Unit,
    currentPageIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
    ) {
        destinations.forEachIndexed { index, destination ->
            val selected = index == currentPageIndex

            var isPressed by remember { mutableStateOf(false) }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isPressed = true
                                tryAwaitRelease()
                                isPressed = false
                            },
                            onTap = {
                                onNavigateToDestination(index)
                            }
                        )
                    }
                    .padding(vertical = SpaceVerticalXSmall)
            ) {
                TabLottieAnimation(
                    animRes = destination.animationResId,
                    isSelected = selected,
                )
                Text(
                    text = stringResource(id = destination.titleTextId),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selected) PrimaryDefault else MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.5f
                    )
                )
            }
        }
    }
}
@Composable
private fun TabLottieAnimation(
    animRes: Int,
    isSelected: Boolean
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animRes))

    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isSelected
    )

    LottieAnimation(
        composition = composition,
        progress = { if (!isSelected) 0f else progress },
        modifier = Modifier.size(30.dp)
    )
}