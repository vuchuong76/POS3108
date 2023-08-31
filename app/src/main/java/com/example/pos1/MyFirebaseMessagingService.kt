package com.example.pos1

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    //thông báo từ Firebase Cloud Message đến, phương thức này sẽ được gọi.
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        //  kiểm tra xem thông báo có chứa nội dung payload (thông báo đẩy) không
        if (remoteMessage.notification != null) {
            sendNotification(remoteMessage.notification!!.body)
        }
    }
//Khi một mã thông báo mới được tạo cho ứng dụng, phương thức này sẽ được gọi.
    override fun onNewToken(token: String) {
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        // TODO: Implement this method to send token to your app server.
    }

    private fun sendNotification(messageBody: String?) {
        //Intent mở mainActivity,nếu có tồn tại trong stack thì xoá các hoạt động khác
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val channelId = "POST"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = channelId
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelId, name, importance)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        //Xây dựng và hiển thị thông báo sử dụng NotificationCompat.Builder.
        // Thông báo này sẽ có tiêu đề là "POST", nội dung là messageBody,
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_lock_idle_alarm)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.ic_lock_idle_alarm
                    )
                )
                .setContentTitle("POST")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationManager.IMPORTANCE_MAX)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager


        notificationManager.notify(0, notificationBuilder.build())
    }


}




