package com.seanchen.xinchat.core.ui.component.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seanchen.xinchat.core.designsystem.theme.ArrowRightIcon
import com.seanchen.xinchat.core.designsystem.theme.SpaceHorizontalMedium
import com.seanchen.xinchat.core.designsystem.theme.SpaceHorizontalXSmall
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalMedium
import com.seanchen.xinchat.core.ui.component.divider.AppDivider
import com.seanchen.xinchat.core.ui.component.text.AppText
import com.seanchen.xinchat.core.ui.component.text.TextSize
import com.seanchen.xinchat.core.ui.component.text.TextType

@Composable
fun AppListItem(
    title: String,
    modifier: Modifier = Modifier,
    leadingIcon: Int? = null,
    leadingIconTint: Color = MaterialTheme.colorScheme.onSurface,
    leadingContent: @Composable (() -> Unit)? = null,
    description: String? = null,
    trailingText: String? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    showArrow: Boolean = true,
    showDivider: Boolean = true,
    verticalPadding: Dp = SpaceVerticalMedium,
    horizontalPadding: Dp = SpaceHorizontalMedium,
    onClick: () -> Unit = {}
){
    Column{
        Row(
            modifier = modifier.fillMaxWidth().clickable{ onClick() }.padding(vertical = verticalPadding, horizontal = horizontalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingContent != null) {
                leadingContent()
                SpaceHorizontalMedium()
            } else if (leadingIcon != null) {
                Icon(
                    painter = painterResource(id = leadingIcon),
                    contentDescription = title,
                    modifier = Modifier.size(20.dp),
                    tint = leadingIconTint
                )
                SpaceHorizontalMedium()
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                AppText(
                    text = title,
                    size = TextSize.BODY_LARGE,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (!description.isNullOrEmpty()) {
                    AppText(
                        text = description,
                        type = TextType.SECONDARY,
                        size = TextSize.BODY_MEDIUM,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            if (trailingContent != null) {
                trailingContent()
            } else if (!trailingText.isNullOrEmpty()) {
                AppText(
                    text = trailingText,
                    type = TextType.SECONDARY,
                    size = TextSize.BODY_MEDIUM
                )
                SpaceHorizontalXSmall()
            }
            if (showArrow) {
                ArrowRightIcon(size = 16.dp)
            }
        }
        if (showDivider) {
            AppDivider()
        }
    }
}