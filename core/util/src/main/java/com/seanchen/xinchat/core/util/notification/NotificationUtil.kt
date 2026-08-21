package com.seanchen.xinchat.core.util.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationUtil {
    private const val DEFAULT_CHANNEL_ID = "default_channel"
    private const val VERIFICATION_CODE_CHANNEL_ID = "verification_code_channel"

    @RequiresApi(Build.VERSION_CODES.O)
    fun initNotificationChannels(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val defaultChannel = NotificationChannel(
            DEFAULT_CHANNEL_ID,
            "默认通知",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "应用的默认通知渠道"
        }

        val verificationCodeChannel = NotificationChannel(
            VERIFICATION_CODE_CHANNEL_ID,
            "验证码通知",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "用于显示短信验证码的通知渠道"
        }

        notificationManager.createNotificationChannels(
            listOf(
                defaultChannel,
                verificationCodeChannel
            )
        )
    }

    /**
     * 发送验证码通知
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    fun sendVerificationCodeNotification(
        context: Context,
        code: String,
    ): Int {
        ensureChannelExists(
            context,
            VERIFICATION_CODE_CHANNEL_ID,
            "验证码通知",
            NotificationManager.IMPORTANCE_HIGH
        )

        val builder = NotificationCompat.Builder(context, VERIFICATION_CODE_CHANNEL_ID)
            .setContentTitle("验证码")
            .setContentText("验证码是:$code")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationId = System.currentTimeMillis().toInt()

        NotificationManagerCompat.from(context).apply {
            notify(notificationId, builder.build())
        }
        return notificationId
    }

    /**
     * 确保通知渠道存在
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun ensureChannelExists(
        context: Context,
        channelId: String,
        channelName: String,
        importance: Int
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val existingChannel = notificationManager.getNotificationChannel(channelId)

        if (existingChannel == null) {
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = when (channelId) {
                    DEFAULT_CHANNEL_ID -> "应用的默认通知渠道"
                    VERIFICATION_CODE_CHANNEL_ID -> "用于显示短信验证码的通知渠道"
                    else -> "通知渠道"
                }
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}