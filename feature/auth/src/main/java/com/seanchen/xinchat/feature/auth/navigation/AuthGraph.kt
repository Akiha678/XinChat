package com.seanchen.xinchat.feature.auth.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.seanchen.xinchat.core.navigation.auth.AuthRoutes
import com.seanchen.xinchat.feature.auth.view.AccountLoginRoute

fun EntryProviderScope<NavKey>.authGraph() {
    entry<AuthRoutes.AccountLogin>{
        AccountLoginRoute()
    }
}