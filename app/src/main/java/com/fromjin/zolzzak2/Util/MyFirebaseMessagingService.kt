package com.fromjin.zolzzak2.Util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.fromjin.zolzzak2.Activity.LoginActivity
import com.fromjin.zolzzak2.Activity.MainActivity
import com.fromjin.zolzzak2.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "From: ${remoteMessage.from}")


        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message Payload Body: ${remoteMessage.data}")
            sendNotification(remoteMessage.data["title"].toString(), remoteMessage.data["body"].toString())

            if (true) {
                scheduleJob()
            } else {

                handleNow()
            }
        }

        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: ${remoteMessage.notification!!.body}")
            sendNotification(remoteMessage.notification!!.body.toString(),
                remoteMessage.notification!!.body.toString()
            )
        }


    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        sendRegistrationToServer(token)
    }


    private fun scheduleJob() {


        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance(this).beginWith(work).enqueue()

    }


    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }


    private fun sendRegistrationToServer(token: String) {
        // TODO: Implement this method to send token to your app server.
        MySharedPreferences.setFcmToken(this, token)
        LoginActivity().login(MySharedPreferences.getKakaoToken(this), this, token)
    }

    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = "zolzzak2"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_icon_round)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }
}