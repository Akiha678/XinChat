package com.seanchen.xinchat.feature.main.model

import androidx.annotation.StringRes
import com.seanchen.xinchat.core.navigation.main.MainRoutes
import com.seanchen.xinchat.feature.main.R

enum class TopLevelDestination(
    @param:StringRes val titleTextId: Int,
    val route: Any
) {
    HOME(
        titleTextId = R.string.home,
        route = MainRoutes.Main
    ),

    CONTACT(
        titleTextId = R.string.contact,
        route = MainRoutes.Contact
    ),

    ME(
        titleTextId = R.string.me,
        route = MainRoutes.Me
    )
}