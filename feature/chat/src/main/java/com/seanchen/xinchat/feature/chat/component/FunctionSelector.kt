package com.seanchen.xinchat.feature.chat.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.seanchen.xinchat.core.designsystem.component.AppRow
import com.seanchen.xinchat.core.designsystem.theme.ShapeCircle
import com.seanchen.xinchat.core.designsystem.theme.ShapeSmall
import com.seanchen.xinchat.core.designsystem.theme.SpaceHorizontalMedium
import com.seanchen.xinchat.core.designsystem.theme.SpacePaddingMedium
import com.seanchen.xinchat.core.designsystem.theme.SpacePaddingSmall
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalSmall
import com.seanchen.xinchat.core.ui.component.text.AppText
import com.seanchen.xinchat.core.ui.component.text.TextSize
import com.seanchen.xinchat.feature.chat.R

@Composable
fun FunctionSelector(
    onFunctionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
){
    Surface(modifier = modifier) {
        val outlineColor = MaterialTheme.colorScheme.outline

        Column{
            Canvas(
                modifier = modifier.fillMaxWidth().height(1.dp)
            ) {
                drawLine(
                    color = outlineColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }

            AppRow(
                modifier = Modifier.fillMaxWidth().padding(SpacePaddingMedium),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FunctionItem(
                    icon = R.drawable.ic_image_fill,
                    label = stringResource(R.string.gallery),
                    onClick = { onFunctionSelected("gallery") }
                )

                SpaceHorizontalMedium()

                FunctionItem(
                    icon = R.drawable.ic_camera_fill,
                    label = stringResource(R.string.camera),
                    onClick = { onFunctionSelected("camera") }
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun FunctionItem(
    icon: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier.clip(ShapeSmall).clickable(onClick = onClick).padding(SpacePaddingSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(56.dp).background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = ShapeCircle
            ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        SpaceVerticalSmall()

        AppText(
            text = label,
            size = TextSize.BODY_SMALL
        )
    }
}