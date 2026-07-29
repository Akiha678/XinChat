package com.seanchen.xinchat.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// 标准圆角数值定义

/**
 * 超小圆角数值：4dp
 */
val RadiusXSmall = 4.dp

/**
 * 小圆角数值：8dp
 */
val RadiusSmall = 8.dp

/**
 * 中圆角数值：12dp
 */
val RadiusMedium = 12.dp

/**
 * 大圆角数值：16dp
 */
val RadiusLarge = 16.dp

/**
 * 超大圆角数值：24dp
 */
val RadiusExtraLarge = 24.dp

/**
 * 超小圆角：4dp
 */
val ShapeXSmall = RoundedCornerShape(RadiusXSmall)

/**
 * 小圆角：8dp
 */
val ShapeSmall = RoundedCornerShape(RadiusSmall)

/**
 * 中圆角：12dp
 */
val ShapeMedium = RoundedCornerShape(RadiusMedium)

/**
 * 大圆角：16dp
 */
val ShapeLarge = RoundedCornerShape(RadiusLarge)

/**
 * 超大圆角：24dp
 */
val ShapeExtraLarge = RoundedCornerShape(RadiusExtraLarge)

/**
 * 圆形
 */
val ShapeCircle = RoundedCornerShape(percent = 50)

val AppShapes = Shapes(
    small = ShapeSmall,
    medium = ShapeMedium,
    large = ShapeLarge,
    extraLarge = ShapeExtraLarge
)