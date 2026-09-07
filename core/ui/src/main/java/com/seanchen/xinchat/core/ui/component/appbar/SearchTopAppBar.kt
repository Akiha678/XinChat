package com.seanchen.xinchat.core.ui.component.appbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seanchen.xinchat.core.designsystem.theme.ArrowLeftIcon
import com.seanchen.xinchat.core.designsystem.theme.ShapeMedium
import com.seanchen.xinchat.core.ui.R

/**
 * 带搜索入口的顶部栏。
 *
 * 搜索区域作为 AppBar 的标题内容展示，点击行为由页面决定，适用于跳转搜索页或展开搜索输入框。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
    placeholderText: String,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
    titleText: String? = null,
    actions: @Composable (RowScope.() -> Unit) = {},
    onBackClick: () -> Unit = {},
    showBackIcon: Boolean = true,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.background
    )
) {
    Column(modifier = modifier) {
        CenterAlignedTopAppBar(
            navigationIcon = {
                if (showBackIcon) {
                    IconButton(onClick = onBackClick) {
                        ArrowLeftIcon()
                    }
                }
            },
            title = {
                titleText?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            actions = actions,
            colors = colors
        )

        Card(
            shape = ShapeMedium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(horizontal = 16.dp)
                .clickable(onClick = onSearchClick)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = placeholderText,
                    modifier = Modifier.padding(start = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
