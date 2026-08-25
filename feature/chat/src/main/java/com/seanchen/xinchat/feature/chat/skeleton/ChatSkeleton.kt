package com.seanchen.xinchat.feature.chat.skeleton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.seanchen.xinchat.core.designsystem.theme.SpacePaddingMedium
import com.seanchen.xinchat.core.designsystem.theme.SpacePaddingSmall
import androidx.compose.ui.unit.dp

@Composable
internal fun ChatLoadingSkeleton(){
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = SpacePaddingMedium),
    ) {
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(top = SpacePaddingSmall, bottom = SpacePaddingMedium),
            verticalArrangement = Arrangement.spacedBy(SpacePaddingMedium),
        ) {
            repeat(7) { index ->
                val alignEnd = index % 3 == 1
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = if (alignEnd) 92.dp else 0.dp,
                            end = if (alignEnd) 0.dp else 92.dp
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .width(if (index % 2 == 0) 190.dp else 240.dp)
                            .height(if (index == 0) 56.dp else 44.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(18.dp)
                            )
                    )
                }
            }
        }
    }
}
