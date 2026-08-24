package com.seanchen.xinchat.core.ui.component.empty

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.seanchen.xinchat.core.designsystem.theme.CommonIcon
import com.seanchen.xinchat.core.designsystem.theme.SpacePaddingLarge
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalSmall
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalXLarge
import com.seanchen.xinchat.core.designsystem.theme.appTextColors
import com.seanchen.xinchat.core.ui.R

@Composable
fun Empty(
    modifier: Modifier = Modifier,
    message: Int = R.string.empty_error,
    subtitle: Int? = null,
    retryButtonText: Int = R.string.click_retry,
    icon: Int = R.drawable.ic_empty_error,
    onRetryClick: (()-> Unit)? = null
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize().padding(SpacePaddingLarge)
    ) {
        CommonIcon(
            painter = painterResource(id = icon),
            size = 120.dp,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2F)
        )

        SpaceVerticalXLarge()

        Text(
            text = stringResource(id = message),
            style = MaterialTheme.typography.titleLarge
        )

        subtitle?.let {
            SpaceVerticalSmall()
            Text(
                text = stringResource(id = it),
                style = MaterialTheme.typography.bodyMedium,
                color = appTextColors().tertiary
            )
        }

        if (onRetryClick != null) {
            SpaceVerticalXLarge()
            OutlinedButton(
                onClick = onRetryClick,
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier.padding(horizontal = 50.dp)
                    .widthIn(200.dp)
            ) {
                Text(
                    text = stringResource(id = retryButtonText)
                )
            }
        }
    }
}