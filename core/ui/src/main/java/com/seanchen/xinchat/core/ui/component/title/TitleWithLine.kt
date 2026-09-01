package com.seanchen.xinchat.core.ui.component.title

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.seanchen.xinchat.core.designsystem.theme.SpaceHorizontalSmall
import com.seanchen.xinchat.core.designsystem.theme.TitleLarge
import com.seanchen.xinchat.core.ui.component.text.AppText

@Composable
fun TitleWithLine(
    text: String,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary
){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(lineColor)
        )
        SpaceHorizontalSmall()
        AppText(
            text = text,
            fontWeight = TitleLarge.fontWeight,
            color = textColor
        )
    }
}