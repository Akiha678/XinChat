package com.seanchen.xinchat

import com.seanchen.xinchat.core.util.storage.MMKVUtils
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        MMKVUtils.init(this)
    }
}
