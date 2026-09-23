package br.ufpi.lgpd.educacional.data.remote

import android.content.Context
import br.ufpi.lgpd.educacional.data.repository.UserRepository
import br.ufpi.lgpd.educacional.util.AvatarConstants
import br.ufpi.lgpd.educacional.util.SupabaseConfig
import br.ufpi.lgpd.educacional.util.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber

/**
 * Ranking global via Supabase. Sobe o XP local do dispositivo para a tabela
 * `leaderboard` via RPC `submit_score` (validação de ganhos no banco) e busca
 * o top N via RPC `get_top_ranking`.
 *
 * Privacidade: o usuário pode sair do ranking público (toggle em Configurações).
 * Quando desativado, sobe "Anônimo" — o XP continua sendo enviado (mantém o
 * placar coerente) mas sem nome identificável.
 *
 * Tudo em Dispatchers.IO; falhas são logadas no Timber mas nunca travam o app.
 */
object RankingService {

    data class RankingEntry(
        val displayName: String,
        val avatarColor: String,
        val totalPoints: Int,
        val streakDays: Int,
        val lessonsCompleted: Int,
        val isMe: Boolean = false
    )

    private const val PREFS = "lgpd_ranking_prefs"
    private const val KEY_DEVICE_ID = "device_id"

    /** ID estável do dispositivo (gerado 1x e guardado em prefs). */
    fun getDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.getString(KEY_DEVICE_ID, null)?.let { return it }

        val fresh = java.util.UUID.randomUUID().toString()
        prefs.edit().putString(KEY_DEVICE_ID, fresh).apply()
        return fresh
    }

    /** Envia o XP/streak/aulas locais para o ranking global (RPC, best-effort). */
    suspend fun syncMyScore(context: Context, repository: UserRepository) {
        if (!SupabaseConfig.isEnabled) return
        withContext(Dispatchers.IO) {
            try {
                val user = repository.getUser() ?: return@withContext
                val prefs = UserPreferences(context)
                val publicName = if (prefs.leaderboardParticipation) {
                    user.name.ifBlank { "Anônimo" }
                } else {
                    "Anônimo"
                }
                val avatarColor = AvatarConstants.COLORS.getOrElse(user.avatarColorIndex) {
                    AvatarConstants.COLORS.first()
                }
                val args = JSONObject().apply {
                    put("p_device_id", getDeviceId(context))
                    put("p_display_name", publicName)
                    put("p_avatar_color", avatarColor)
                    put("p_total_points", user.totalPoints)
                    put("p_streak_days", user.streakDays)
                    put("p_lessons_completed", user.lessonsCompleted)
                }
                SupabaseClient.rpc("submit_score", args)
            } catch (e: Exception) {
                Timber.w(e, "syncMyScore falhou (ranking é best-effort)")
            }
        }
    }

    /** Busca o top N do ranking global via RPC get_top_ranking (ordena no banco). */
    suspend fun fetchTop(context: Context, maxRows: Int = 20): List<RankingEntry> =
        withContext(Dispatchers.IO) {
            if (!SupabaseConfig.isEnabled) return@withContext emptyList()
            try {
                val myDeviceId = getDeviceId(context)
                val args = JSONObject().put("max_rows", maxRows)
                val rows = org.json.JSONArray(SupabaseClient.rpc("get_top_ranking", args))
                (0 until rows.length()).map { i ->
                    val row = rows.getJSONObject(i)
                    RankingEntry(
                        displayName = row.optString("display_name", "Anônimo"),
                        avatarColor = row.optString("avatar_color", "#89B4FA"),
                        totalPoints = row.optInt("total_points", 0),
                        streakDays = row.optInt("streak_days", 0),
                        lessonsCompleted = row.optInt("lessons_completed", 0),
                        isMe = row.optString("device_id") == myDeviceId
                    )
                }
            } catch (e: Exception) {
                Timber.w(e, "fetchTop falhou")
                emptyList()
            }
        }
}
