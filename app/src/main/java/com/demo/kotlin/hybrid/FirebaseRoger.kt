package com.demo.kotlin.hybrid

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseRoger : FirebaseMessagingService() {

    override fun onMessageReceived (remoteMessage: RemoteMessage) {
        Allo.i ("onMessageReceived $javaClass")

        try {
            var link: String? = ""
            var title: String? = ""
            var message: String? = ""
            val from: String? = remoteMessage.from

            if (remoteMessage.data.isNotEmpty ()) {
                link = remoteMessage.data ["link"]
            }
            if (null != remoteMessage.notification) {
                title = remoteMessage.notification!!.title
                message = remoteMessage.notification!!.body
            }
            sendNotification (title, message, link)
        } catch (e: Exception) { e.printStackTrace () }
    }

    private fun sendNotification (title: String?, message: String?, link: String?) {
        Allo.i ("sendNotification [$title][$message][$link] $javaClass")

        try {
            val notificationId = System.currentTimeMillis ().toInt ()
            val channelId: String = applicationContext.packageName
            val channelName: String = applicationContext.packageName

            val params = Intent (applicationContext, MainActivity::class.java)
            params.putExtra (Allo.CUBE_LINK, link)
            params.addFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

            val intent = PendingIntent.getActivity (
                applicationContext,
                notificationId,
                params,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notificationBuilder = NotificationCompat.Builder (applicationContext, channelId)
                .setSmallIcon (R.mipmap.ic_launcher)
                .setAutoCancel (false)
                .setShowWhen (true)
                .setWhen (System.currentTimeMillis())
                .setContentTitle (title)
                .setContentText (message)
                .setContentIntent (intent)
            val notificationManager = getSystemService (Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel (
                    NotificationChannel (
                        channelId,
                        channelName,
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                )
            }
            notificationManager.notify (notificationId, notificationBuilder.build ())
        } catch (e: Exception) { e.printStackTrace () }
    }
}