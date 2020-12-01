package com.exa.test.pushes

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import java.util.*


class PushMsg {
    fun sendPush(context: Context) {
        scheduleMsg(context, 0)
    }

    private fun scheduleMsg(context: Context, type: Int) {
        val i = Intent(context, MessageReceiver::class.java)
        i.putExtra(TYPE_EXTRA, type)
        val pendingIntent = PendingIntent.getBroadcast(context, type, i, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManagerRTC = context.getSystemService(ALARM_SERVICE) as AlarmManager
        val interval = 1000 * 60 * 60 * 8
        alarmManagerRTC.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval,
            interval.toLong(), pendingIntent)
//        alarmManagerRTC[AlarmManager.RTC_WAKEUP, calendar.timeInMillis] = pendingIntent
    }

    fun getNotificationManager(context: Context): Any? {
        return context.getSystemService(Context.NOTIFICATION_SERVICE)
    }

    companion object {
        const val TYPE_EXTRA = "msg"
    }
}