package com.exa.test.pushes

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.valpu.seve.tetris.R
import com.valpu.seve.tetris.activities.Menu_Activity

class MessageReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getIntExtra(PushMsg.TYPE_EXTRA, 0)
        val intentToRepeat = Intent(context, Menu_Activity::class.java)
        intentToRepeat.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(context, type, intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT)
        val nm = PushMsg().getNotificationManager(context)
        val notification: Notification = configNotif(context, pendingIntent, nm as NotificationManager?).build()
        nm?.notify(type, notification)
    }

    private fun configNotif(context: Context, pendingIntent: PendingIntent?, nm: NotificationManager?): NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("default",
                "Daily Notification",
                NotificationManager.IMPORTANCE_HIGH)
            channel.description = "Daily Notification"
            nm?.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(context, "default")
            .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Удaча нe зaставилa ceбя ждaть!")
            .setAutoCancel(true)
    }
}
