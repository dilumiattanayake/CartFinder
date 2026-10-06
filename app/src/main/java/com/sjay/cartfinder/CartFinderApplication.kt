package com.sjay.cartfinder

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class CartFinderApplication : Application() {

    private var notificationListener: ListenerRegistration? = null
    private var lastNotifiedTime: Long = 0

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                startListeningForNotifications(user.uid)
            } else {
                stopListeningForNotifications()
            }
        }
    }

    private fun startListeningForNotifications(userId: String) {
        if (notificationListener != null) return
        
        lastNotifiedTime = System.currentTimeMillis()

        val db = FirebaseFirestore.getInstance()
        notificationListener = db.collection("notifications")
            .whereEqualTo("userId", userId)
            .whereEqualTo("isRead", false)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener

                for (change in snapshot.documentChanges) {
                    if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                        val doc = change.document
                        val createdAt = doc.getLong("createdAt") ?: 0L
                        if (createdAt > lastNotifiedTime) {
                            val title = doc.getString("title") ?: "New Notification"
                            val message = doc.getString("message") ?: ""
                            showPushNotification(title, message)
                        }
                    }
                }
            }
    }

    private fun stopListeningForNotifications() {
        notificationListener?.remove()
        notificationListener = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Orders and Updates"
            val descriptionText = "Notifications for order statuses and important updates"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("CARTFINDER_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showPushNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, "CARTFINDER_CHANNEL")
            .setSmallIcon(R.mipmap.ic_launcher) // Fallback icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
