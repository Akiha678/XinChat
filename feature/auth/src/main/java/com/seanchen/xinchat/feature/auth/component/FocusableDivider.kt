package com.seanchen.xinchat.feature.auth.component

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seanchen.xinchat.core.designsystem.theme.Primary
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalLarge

@Composable
fun FocusableDivider(
    focusState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    thickness: Float = 1f,
    withSpacing: Boolean = true
){
    if (withSpacing) {
        SpaceVerticalLarge()
    }

    HorizontalDivider(
        modifier = modifier,
        thickness = thickness.dp,
        color = if (focusState.value) Primary else MaterialTheme.colorScheme.outline
    )
}