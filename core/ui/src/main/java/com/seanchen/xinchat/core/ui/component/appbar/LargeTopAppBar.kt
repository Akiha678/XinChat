package com.seanchen.xinchat.core.ui.component.appbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.seanchen.xinchat.core.designsystem.theme.ArrowLeftIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LargeTopAppBar(
    title: Int? = null,
    titleText: String? = null,
    actions: @Composable (RowScope.() -> Unit) = {},
    onBackClick: () -> Unit = {},
    showBackIcon: Boolean = true,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    expandedBackgroundColor: Color = MaterialTheme.colorScheme.background,
    collapsedBackgroundColor: Color = MaterialTheme.colorScheme.background
) {
    val scrollFraction = scrollBehavior?.state?.collapsedFraction ?: 0f
    val titleFontSize = lerp(
        start = 30.sp,
        stop = 16.sp,
        fraction = scrollFraction
    )
    val backgroundColor = lerp(
        start = expandedBackgroundColor,
        stop = collapsedBackgroundColor,
        fraction = scrollFraction
    )

    MediumTopAppBar(
        title = {
            val finalTitle = titleText ?: title?.let { stringResource(it) } ?: ""
            if (finalTitle.isNotBlank()) {
                Text(
                    text = finalTitle,
                    fontSize = titleFontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            if (showBackIcon) {
                IconButton(onClick = onBackClick) {
                    ArrowLeftIcon()
                }
            }
        },
        actions = actions,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor
        )
    )
}