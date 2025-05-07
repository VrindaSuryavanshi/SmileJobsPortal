package com.example.smilejobportal.Service;

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.smilejobportal.Activity.MainActivity
import com.example.smilejobportal.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingServiceNotification : FirebaseMessagingService() {

 override fun onMessageReceived(remoteMessage: RemoteMessage) {
  val title = remoteMessage.notification?.title ?: "New Job"
  val body = remoteMessage.notification?.body ?: "Check the app for details."

  // Increment badge count in SharedPreferences
  val prefs = getSharedPreferences("notifications", Context.MODE_PRIVATE)
  val count = prefs.getInt("unread_count", 0)
  prefs.edit().putInt("unread_count", count + 1).apply()

  showNotification(title, body)
 }

 private fun showNotification(title: String, message: String) {
  val channelId = "job_channel"
  val notificationId = System.currentTimeMillis().toInt()

  val intent = Intent(this, MainActivity::class.java).apply {
   flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
  }

  val pendingIntent = PendingIntent.getActivity(
          this, 0, intent,
          PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
  )

  val notificationBuilder = NotificationCompat.Builder(this, channelId)
          .setSmallIcon(R.drawable.bell)
          .setContentTitle(title)
          .setContentText(message)
          .setAutoCancel(true)
          .setContentIntent(pendingIntent)
          .setPriority(NotificationCompat.PRIORITY_HIGH)

  val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

  // Create channel for Android 8.0+
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
   val channel = NotificationChannel(
           channelId, "Job Notifications",
           NotificationManager.IMPORTANCE_HIGH
   )
   notificationManager.createNotificationChannel(channel)
  }

  notificationManager.notify(notificationId, notificationBuilder.build())
 }
}
