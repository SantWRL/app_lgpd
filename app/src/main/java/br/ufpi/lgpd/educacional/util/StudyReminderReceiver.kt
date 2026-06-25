package br.ufpi.lgpd.educacional.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.Calendar

/**
 * BroadcastReceiver que recebe o alarme do AlarmManager e exibe a notificação
 * de lembrete para estudar.
 */
class StudyReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NotificationHelper.createNotificationChannel(context)
        NotificationHelper.showStudyReminder(context)
    }

    companion object {
        private const val REQUEST_CODE = 2001
        const val ACTION_REMINDER = "br.ufpi.lgpd.educacional.ACTION_STUDY_REMINDER"

        /**
         * Agenda o lembrete diário no horário especificado.
         * @param hourOfDay Hora do dia (0-23)
         * @param minute Minuto (0-59)
         */
        fun schedule(context: Context, hourOfDay: Int = 18, minute: Int = 0) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, StudyReminderReceiver::class.java).apply {
                action = ACTION_REMINDER
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                // Se já passou, agenda para amanhã
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }

        /** Cancela o lembrete agendado. */
        fun cancel(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, StudyReminderReceiver::class.java).apply {
                action = ACTION_REMINDER
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
