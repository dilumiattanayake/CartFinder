package com.sjay.cartfinder.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sjay.cartfinder.MainActivity
import com.sjay.cartfinder.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "New Notification"
        val message = remoteMessage.notification?.body ?: remoteMessage.data["message"] ?: ""

        showPushNotification(title, message)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // If user is logged in, update the token in Firestore
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    FirebaseFirestore.getInstance().collection("users")
                        .document(user.uid)
                        .update("fcmToken", token)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun showPushNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "CARTFINDER_FCM_CHANNEL"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Cloud Messages",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Push Notifications from Firebase Cloud Messaging"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // Assuming ic_launcher exists, better to use a transparent white icon if available
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
