package com.demo.kotlin.hybrid

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class FirebaseService : NotificationListenerService () {
    override fun onCreate () {
        super.onCreate ()
        Allo.i ("onCreate $javaClass")
    }

    override fun onNotificationPosted (sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        Allo.i ("onNotificationPosted [" + applicationContext.packageName + "][" + sbn.packageName + "] $javaClass")

        try {
            if (applicationContext.packageName == sbn.packageName) {
                // (여기선 스킵요) NotificationListenerService 상속 클래스는 권한 획등용.
                // 푸시 알림 수신 관련 처리는 FirebaseMessagingService 상속된 클래스의 onMessageReceived () 쪽에서 처리함
            }
        } catch (e: Exception) { e.printStackTrace () }
    }
}
