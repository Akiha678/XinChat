package com.seanchen.xinchat.feature.chat.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.seanchen.xinchat.feature.chat.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatSoundManager(private val context: Context) {
    private var soundPool: SoundPool? = null
    private var sendSoundId: Int = 0
    private var receiveSoundId: Int = 0

    @Volatile
    private var isInitialized = false

    @Volatile
    private var isInitializing = false
    private val lock = Any()

    /**
     * 异步预加载音效资源（在后台协程执行，彻底避免阻塞 UI 主线程）
     */
    fun preload(scope: CoroutineScope) {
        if (isInitialized || isInitializing) return
        scope.launch(Dispatchers.IO) {
            ensureSoundPoolInitialized()
        }
    }

    private fun ensureSoundPoolInitialized() {
        if (isInitialized) return
        synchronized(lock) {
            if (isInitialized || isInitializing) return
            isInitializing = true
            try {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                val pool = SoundPool.Builder()
                    .setMaxStreams(2)
                    .setAudioAttributes(audioAttributes)
                    .build()

                soundPool = pool
                sendSoundId = pool.load(context.applicationContext, R.raw.send, 1)
                receiveSoundId = pool.load(context.applicationContext, R.raw.receive, 1)

                pool.setOnLoadCompleteListener { _, _, status ->
                    if (status == 0) {
                        isInitialized = true
                        isInitializing = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                isInitializing = false
            }
        }
    }

    /**
     * 播放发送消息音效
     */
    fun playMessageSentSound() {
        if (!isInitialized) {
            ensureSoundPoolInitialized()
        }
        if (isInitialized && sendSoundId != 0) {
            soundPool?.play(
                sendSoundId,
                1.0f,
                1.0f,
                1,
                0,
                1.0f
            )
        }
    }

    /**
     * 播放接收消息音效
     */
    fun playMessageReceivedSound() {
        if (!isInitialized) {
            ensureSoundPoolInitialized()
        }
        if (isInitialized && receiveSoundId != 0) {
            soundPool?.play(
                receiveSoundId,
                1.0f,
                1.0f,
                1,
                0,
                1.0f
            )
        }
    }

    /**
     * 释放资源
     */
    fun release() {
        synchronized(lock) {
            soundPool?.release()
            soundPool = null
            sendSoundId = 0
            receiveSoundId = 0
            isInitialized = false
            isInitializing = false
        }
    }
}