package com.seanchen.xinchat.feature.main.model

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.seanchen.xinchat.core.navigation.main.MainRoutes
import com.seanchen.xinchat.feature.main.R

enum class TopLevelDestination(
    @param:StringRes val titleTextId: Int,
    @param:RawRes val animationResId: Int,
    val route: Any
) {
    HOME(
        titleTextId = R.string.home,
        animationResId = R.raw.home,
        route = MainRoutes.Main,
    ),

    CONTACT(
        titleTextId = R.string.contact,
        animationResId = R.raw.category,
        route = MainRoutes.Contact
    ),

    ME(
        titleTextId = R.string.me,
        animationResId = R.raw.me,
        route = MainRoutes.Me,
    )
}