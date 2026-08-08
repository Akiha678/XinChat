package com.seanchen.xinchat.feature.auth.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.seanchen.xinchat.feature.auth.view.LoginRoute
import com.seanchen.xinchat.feature.auth.view.RegisterRoute
import kotlinx.serialization.Serializable

@Serializable
data object LoginKey : NavKey

@Serializable
data object RegisterKey : NavKey

fun EntryProviderScope<NavKey>.authEntries(
    onRegisterClick: () -> Unit,
    onRegisterBack: () -> Unit,
) {
    entry<LoginKey> { LoginRoute(onRegisterClick = onRegisterClick) }
    entry<RegisterKey> { RegisterRoute(onBack = onRegisterBack) }
}
