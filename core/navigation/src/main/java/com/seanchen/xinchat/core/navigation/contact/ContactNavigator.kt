package com.seanchen.xinchat.core.navigation.contact

import com.seanchen.xinchat.core.navigation.navigate

object ContactNavigator {

    /**
     * 跳转至联系人界面
     */
    fun toContact() {
        navigate(ContactRoutes.Contact)
    }

    /**
     * 跳转至添加朋友界面
     */
    fun toAddFriend(){
        navigate(ContactRoutes.AddFriend)
    }
}