package com.seanchen.xinchat.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * 超大标题文字标题
 * 尺寸：22sp, 行高：31sp, 字重：中粗体
 */
val DisplayLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    fontSize = 22.sp,
    lineHeight = 31.sp
)

/**
 * 大标题文字样式
 * 尺寸：18sp, 行高：27sp, 字重：中粗体
 */
val DisplayMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    lineHeight = 27.sp
)

/**
 * 二级标题文字样式
 * 尺寸：16sp, 行高：24sp, 字重：粗体
 */
val TitleLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = 16.sp,
    lineHeight = 24.sp
)

/**
 * 类别名称文字样式
 * 尺寸：14sp, 行高：22sp, 字重：粗体
 */
val TitleMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
    lineHeight = 22.sp
)

/**
 * 正文内容文字样式
 * 尺寸: 14sp (28px), 行高: 22sp (44px), 字重: 常规体
 * 适用场景: 正文内容、段落文字、列表内容
 */
val BodyLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp, // 28px
    lineHeight = 22.sp, // 44px
)

/**
 * 辅助文字样式
 * 尺寸: 12sp (24px), 行高: 18sp (36px), 字重: 常规体
 * 适用场景: 辅助文字、标签文字、底部导航栏文字、次要信息
 */
val BodyMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp, // 24px
    lineHeight = 18.sp, // 36px
)

val Typography = Typography(
    // 超大标题
    displayLarge = DisplayLarge,
    // 大标题
    displayMedium = DisplayMedium,
    // 二级标题
    titleLarge = TitleLarge,
    // 类别名称
    titleMedium = TitleMedium,
    // 正文内容
    bodyLarge = BodyLarge,
    // 辅助文字、标签文字
    bodyMedium = BodyMedium,
    // 其他必需的Material 3样式，但使用我们的字体规范
    displaySmall = DisplayMedium.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    headlineLarge = TitleLarge,
    headlineMedium = TitleLarge.copy(
        fontWeight = FontWeight.SemiBold
    ),
    headlineSmall = TitleMedium,
    titleSmall = TitleMedium.copy(
        fontWeight = FontWeight.Medium
    ),
    bodySmall = BodyMedium.copy(
        fontSize = 11.sp,
        lineHeight = 16.sp
    ),
    labelLarge = BodyMedium,
    labelMedium = BodyMedium.copy(
        fontWeight = FontWeight.Medium
    ),
    labelSmall = BodyMedium.copy(
        fontSize = 11.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Medium
    )
)