package com.example.gitusers.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.gitusers.R
import com.example.gitusers.main.MainActivity
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val TYPE_REPEATING        = "RepeatingAlarm"
        const val EXTRA_MESSAGE         = "message"
        const val EXTRA_TYPE            = "type"

        private const val ID_REPEATING  = 6789
    }

    override fun onReceive(context: Context, intent: Intent) {
        val type    = intent.getStringExtra(EXTRA_TYPE)
        val notifId = if (type.equals(TYPE_REPEATING, ignoreCase = true)) ID_REPEATING else return

        val title   = context.getString(R.string.app_name)
        val message = intent.getStringExtra(EXTRA_MESSAGE) as String

        showAlarmNotification(context, title, message, notifId)
    }

    fun setRepeatingAlarm(context: Context, type: String, message: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_MESSAGE, message)
            putExtra(EXTRA_TYPE, type)
        }

        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, intent, 0)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val diffrence   = calendar.timeInMillis - System.currentTimeMillis()

        if (diffrence > 0)
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
        else {
            val triggerTime: Long   = System.currentTimeMillis() + AlarmManager.INTERVAL_DAY - diffrence
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerTime, AlarmManager.INTERVAL_DAY, pendingIntent)
        }

        Toast.makeText(context, context.getString(R.string.reminder_success), Toast.LENGTH_SHORT).show()
    }

    private fun showAlarmNotification(context: Context, title: String, message: String, notifId: Int) {
        val channelId   = "Channel_1"
        val channelName = "Alarm manager channel"

        val notificationManagerCompat = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound  = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val goToMain        = Intent(context, MainActivity::class.java)
        val pendingIntent   = PendingIntent.getActivity(context, 0, goToMain, 0)

        val builder     = NotificationCompat.Builder(context, channelId)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_people_black_24)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(0, 500, 500, 500, 500))
            .setSound(alarmSound)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT)

            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(0, 500, 500, 500, 500)

            builder.setChannelId(channelId)
            notificationManagerCompat.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManagerCompat.notify(notifId, notification)
    }

    fun cancelAlarm(context: Context, type: String) {
        val alarmManager    = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent          = Intent(context, AlarmReceiver::class.java)
        val requestCode     = if (type.equals(TYPE_REPEATING, ignoreCase = true)) ID_REPEATING else 0
        val pendingIntent   = PendingIntent.getBroadcast(context, requestCode, intent, 0)

        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)

        Toast.makeText(context, context.getString(R.string.reminder_canceled), Toast.LENGTH_SHORT).show()
    }

}
