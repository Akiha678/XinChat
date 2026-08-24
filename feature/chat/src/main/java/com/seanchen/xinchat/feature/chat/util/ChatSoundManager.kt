package com.seanchen.xinchat.feature.chat.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.seanchen.xinchat.feature.chat.R

class ChatSoundManager(private val context: Context) {
    private var soundPool: SoundPool? = null
    private var sendSoundId: Int = 0
    private var receiveSoundId: Int = 0
    private var isInitialized = false

    init {
        initSoundPool()
    }


    private fun initSoundPool(){
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(audioAttributes)
                .build()

            soundPool?.let { pool ->
                sendSoundId = pool.load(context, R.raw.send, 1)
                receiveSoundId = pool.load(context, R.raw.receive, 1)

                pool.setOnLoadCompleteListener { _, _, status ->
                    if (status == 0) {
                        isInitialized = true
                    }
                }
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    /**
     * 播放发送消息音效
     */
    fun playMessageSentSound() {
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
    fun release(){
        soundPool?.release()
        soundPool = null
    }
}