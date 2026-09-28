package com.seanchen.xinchat.core.navigation.contact

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object ContactRoutes {

    @Serializable
    data object Contact : NavKey

    @Serializable
    data object AddFriend : NavKey

    /**
     * 好友信息界面
     */
    @Serializable
    data class FriendInfo(val userId: Long) : NavKey
}