package com.seanchen.xinchat.feature.user.component

import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.seanchen.xinchat.core.ui.component.list.AppListItem
import com.seanchen.xinchat.feature.user.R

@Composable
fun FunctionMenuSection(
    isLoggedIn: Boolean = false,
    isLoggingOut: Boolean = false,
    onLogoutClick: () -> Unit = {}
){
    Card{
        AppListItem(
            title = stringResource(id = R.string.about_us)
        )
        AppListItem(
            title = stringResource(id = R.string.settings)
        )
        AppListItem(
            title = stringResource(id = R.string.logout),
            trailingText = when {
                isLoggingOut -> stringResource(id = R.string.logging_out)
                !isLoggedIn -> stringResource(id = R.string.not_logged_in)
                else -> null
            },
            showArrow = isLoggedIn && !isLoggingOut,
            onClick = if (isLoggedIn && !isLoggingOut) onLogoutClick else ({})
        )
    }
}
