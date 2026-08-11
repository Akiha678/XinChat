package com.seanchen.xinchat.core.ui.component.appbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.seanchen.xinchat.core.designsystem.theme.ArrowLeftIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterTopAppBar(
    title: Int? = null,
    titleText: String? = null,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    actions: @Composable (RowScope.() -> Unit) = {},
    onBackClick: () -> Unit = {},
    showBackIcon: Boolean = true
){
    CenterAlignedTopAppBar(
        navigationIcon = {
            if (showBackIcon) {
                IconButton(onClick = onBackClick) {
                    ArrowLeftIcon()
                }
            }
        },
        title = {
            val finalTitle = titleText ?: title?.let { stringResource(it) } ?: ""
            if (finalTitle.isNotBlank()){
                Text(
                    text = finalTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        actions = actions,
        colors = colors
    )
}