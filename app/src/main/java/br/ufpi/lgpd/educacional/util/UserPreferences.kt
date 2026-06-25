package br.ufpi.lgpd.educacional.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Preferências locais usadas apenas para estados simples que não fazem parte do Room.
 */
class UserPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("lgpd_user_prefs", Context.MODE_PRIVATE)

    var hasSeenOnboarding: Boolean
        get() = prefs.getBoolean(KEY_HAS_SEEN_ONBOARDING, false)
        set(value) = prefs.edit { putBoolean(KEY_HAS_SEEN_ONBOARDING, value) }

    var reminderEnabled: Boolean
        get() = prefs.getBoolean(KEY_REMINDER_ENABLED, false)
        set(value) = prefs.edit { putBoolean(KEY_REMINDER_ENABLED, value) }

    var reminderHour: Int
        get() = prefs.getInt(KEY_REMINDER_HOUR, 18)
        set(value) = prefs.edit { putInt(KEY_REMINDER_HOUR, value) }

    var reminderMinute: Int
        get() = prefs.getInt(KEY_REMINDER_MINUTE, 0)
        set(value) = prefs.edit { putInt(KEY_REMINDER_MINUTE, value) }

    companion object {
        private const val KEY_HAS_SEEN_ONBOARDING = "has_seen_onboarding"
        private const val KEY_REMINDER_ENABLED = "reminder_enabled"
        private const val KEY_REMINDER_HOUR = "reminder_hour"
        private const val KEY_REMINDER_MINUTE = "reminder_minute"
    }
}
