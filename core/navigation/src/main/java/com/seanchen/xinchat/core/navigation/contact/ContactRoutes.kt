package com.seanchen.xinchat.core.navigation.contact

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object ContactRoutes {

    /**
     * 联系人界面
     */
    @Serializable
    data object Contact : NavKey

    /**
     * 添加好友界面
     */
    @Serializable
    data object AddFriend : NavKey

    /**
     * 好友信息界面
     */
    @Serializable
    data class FriendInfo(val userId: Long) : NavKey
}