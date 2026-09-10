package com.seanchen.xinchat.core.ui.component.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.seanchen.xinchat.core.designsystem.theme.BgGreenLight
import com.seanchen.xinchat.core.designsystem.theme.BgPurpleLight
import com.seanchen.xinchat.core.designsystem.theme.BgRedLight
import com.seanchen.xinchat.core.designsystem.theme.BgYellowLight
import com.seanchen.xinchat.core.designsystem.theme.ColorDanger
import com.seanchen.xinchat.core.designsystem.theme.ColorSuccess
import com.seanchen.xinchat.core.designsystem.theme.ColorWarning
import com.seanchen.xinchat.core.designsystem.theme.Primary
import com.seanchen.xinchat.core.designsystem.theme.ShapeSmall
import com.seanchen.xinchat.core.designsystem.theme.SpacePaddingSmall
import com.seanchen.xinchat.core.designsystem.theme.SpacePaddingXSmall
import com.seanchen.xinchat.core.designsystem.theme.TextWhite

/**
 * 标签类型
 */
enum class TagType {
    // 默认
    DEFAULT,
    // 主要
    PRIMARY,
    // 警告
    WARNING,
    // 危险
    DANGER,
    // 成功
    SUCCESS
}

/**
 * 标签样式风格
 */
enum class TagStyle {
    // 基本样式
    FILLED,
    // 浅色样式
    LIGHT,
    // 空心样式
    OUTLINED
}

/**
 * 标签大小
 */
enum class TagSize {
    // 小型
    SMALL,
    // 中型,
    MEDIUM,
    // 大型
    LARGE
}

@Composable
private fun getTagColors(type: TagType, style: TagStyle): Pair<Color, Color> {
    return when (style) {
        TagStyle.FILLED -> {
            // 填充样式：背景使用主色，文字为白色
            when (type) {
                TagType.DEFAULT -> Pair(
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    MaterialTheme.colorScheme.surfaceVariant
                )

                TagType.PRIMARY -> Pair(TextWhite, Primary)
                TagType.WARNING -> Pair(TextWhite, ColorWarning)
                TagType.DANGER -> Pair(TextWhite, ColorDanger)
                TagType.SUCCESS -> Pair(TextWhite, ColorSuccess)
            }
        }

        TagStyle.LIGHT -> {
            // 浅色样式：背景使用透明度降低的主色，文字使用主色
            when (type) {
                TagType.DEFAULT -> Pair(
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    MaterialTheme.colorScheme.surfaceVariant
                )
                TagType.PRIMARY -> Pair(Primary, BgPurpleLight)
                TagType.WARNING -> Pair(ColorWarning, BgYellowLight)
                TagType.DANGER -> Pair(ColorDanger, BgRedLight)
                TagType.SUCCESS -> Pair(ColorSuccess, BgGreenLight)
            }
        }

        TagStyle.OUTLINED -> {
            // 空心样式：背景透明，使用边框，文字使用主色
            when (type) {
                TagType.DEFAULT -> Pair(MaterialTheme.colorScheme.onSurfaceVariant, Color.Transparent)
                TagType.PRIMARY -> Pair(Primary, Color.Transparent)
                TagType.WARNING -> Pair(ColorWarning, Color.Transparent)
                TagType.DANGER -> Pair(ColorDanger, Color.Transparent)
                TagType.SUCCESS -> Pair(ColorSuccess, Color.Transparent)
            }
        }
    }
}

@Composable
fun Tag(
    text: String,
    type: TagType = TagType.DEFAULT,
    style: TagStyle = TagStyle.FILLED,
    size: TagSize = TagSize.MEDIUM,
    shape: Shape = ShapeSmall,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall
){
    val (textColor, backgroundColor) = getTagColors(type, style)

    val padding = when (size) {
        TagSize.SMALL -> SpacePaddingXSmall
        TagSize.MEDIUM -> SpacePaddingSmall
        TagSize.LARGE -> SpacePaddingSmall.times(1.5f)
    }

    var tagModifier = if (style == TagStyle.OUTLINED) {
        Modifier
            .clip(shape)
            .border(1.dp, textColor, shape)
            .background(backgroundColor)
            .padding(horizontal = padding.times(1.5f), vertical = padding)
    } else {
        Modifier
            .clip(shape)
            .background(backgroundColor)
            .padding(horizontal = padding.times(1.5f), vertical = padding)
    }

    tagModifier = modifier.then(tagModifier)

    Box(
        contentAlignment = Alignment.Center,
        modifier = tagModifier
    ){
        Text(
            text = text,
            color = textColor,
            style = textStyle
        )
    }
}