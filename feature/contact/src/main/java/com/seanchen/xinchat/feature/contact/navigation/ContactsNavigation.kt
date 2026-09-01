package com.seanchen.xinchat.feature.contact.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.seanchen.xinchat.core.navigation.main.MainRoutes
import com.seanchen.xinchat.feature.contact.view.ContactRoute

fun EntryProviderScope<NavKey>.contactGraph() {
    entry<MainRoutes.Contact> {
        ContactRoute()
    }
}
