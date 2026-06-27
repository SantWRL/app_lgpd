package br.ufpi.lgpd.educacional.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Reagenda o alarme de lembrete de estudos após o dispositivo reiniciar.
 * O AlarmManager perde todos os alarmes no reboot — este receiver restaura
 * o lembrete se o usuário o havia ativado.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val prefs = UserPreferences(ctx)
        if (prefs.reminderEnabled) {
            StudyReminderReceiver.schedule(ctx, prefs.reminderHour, prefs.reminderMinute)
        }
    }
}
