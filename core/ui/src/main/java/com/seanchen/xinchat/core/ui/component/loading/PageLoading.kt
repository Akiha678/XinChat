package com.seanchen.xinchat.core.ui.component.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalSmall
import com.seanchen.xinchat.core.ui.component.text.AppText
import com.seanchen.xinchat.core.ui.component.text.TextSize

@Composable
fun PageLoading(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MiLoadingMobile(
            borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        SpaceVerticalSmall()
        AppText(
            text = "加载中",
            size = TextSize.BODY_MEDIUM
        )
    }
}