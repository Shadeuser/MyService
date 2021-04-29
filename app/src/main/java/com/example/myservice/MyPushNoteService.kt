package com.example.myservice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val PUSH_KEY_TITLE = "TITLE"
const val PUSH_KEY_MESSAGE = "MESSAGE"
const val CHANNEL_ID = "CHANNEL_ID"
const val NOTIFICATION_ID = 37


class MyPushNoteService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val remoteMessageData = remoteMessage.data
        if (remoteMessageData.isNotEmpty()) {
            handleDataMessage(remoteMessageData.toMap())
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val title = data[PUSH_KEY_TITLE]
        val message = data[PUSH_KEY_MESSAGE]
        if (!title.isNullOrBlank() && !message.isNullOrBlank()) {
            showNotification(title, message)
        }
    }

    private fun showNotification(title: String, message: String) {
        val notificationBuilder = NotificationCompat.Builder(
                applicationContext,
                CHANNEL_ID
        ).apply {
            this.setSmallIcon(R.drawable.ic_sun_foreground)
            this.setContentTitle(title)
            this.setContentText(message)
            this.priority = NotificationCompat.PRIORITY_DEFAULT
        }

        val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())


    }

    @RequiresApi (Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val name = "Channel name"
        val descriptionText = "Channel description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            this.description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }

    override fun onNewToken(p0: String) {

    }


}