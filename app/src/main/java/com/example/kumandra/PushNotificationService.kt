package com.example.kumandra

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.kumandra.data.local.StudentModel
import com.example.kumandra.data.local.UserSession
import com.example.kumandra.ui.MainActivity
import com.example.kumandra.viewmodel.DetailStoryViewModel
import com.example.kumandra.viewmodel.FcmViewModel
import com.example.kumandra.viewmodel.ViewModelFactory
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class PushNotificationService(): FirebaseMessagingService() {

    private lateinit var viewModel: FcmViewModel
    override fun onCreate() {
        super.onCreate()
        // Inisialisasi TokenViewModel
        val app = applicationContext as Application
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(app).create(FcmViewModel::class.java)
    }
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM TOKEN", "New Token: $token")
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        viewModel.addFcmToken(ID, "2", token)

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("FCM", "From: ${message.from}")

        message.data.isNotEmpty().let {
            Log.d("FCM", "Message data payload: ${message.data}")
            //      handleNow(message.data)
        }

        // Periksa apakah pesan berisi payload notifikasi.
        message.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")
            sendNotification(it.body)
        }
    }

    private fun sendNotification(messageBody: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)




        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(getString(R.string.fcm_message))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = getString(R.string.default_notification_channel_id)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object{
        var ID = ""
    }
}