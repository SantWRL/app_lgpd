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

    companion object {
        private const val KEY_HAS_SEEN_ONBOARDING = "has_seen_onboarding"
    }
}
