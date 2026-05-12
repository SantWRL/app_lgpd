package br.ufpi.lgpd.educacional.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Equivalente ao AsyncStorage + UserProgressContext do React Native app-lei.
 * Persiste localmente: nome, pontos, lições concluídas, conquistas, streak, etc.
 */
class UserPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("lgpd_user_prefs", Context.MODE_PRIVATE)

    companion object {
        // Chaves - espelham as KEYS do UserProgressContext.tsx
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_BIO = "user_bio"
        private const val KEY_PROFILE_TYPE = "profile_type"
        private const val KEY_AVATAR_COLOR = "avatar_color"
        private const val KEY_TOTAL_POINTS = "total_points"
        private const val KEY_LESSONS_COMPLETED = "lessons_completed"
        private const val KEY_QUIZZES_COMPLETED = "quizzes_completed"
        private const val KEY_STREAK_DAYS = "streak_days"
        private const val KEY_LAST_STREAK_DATE = "last_streak_date"
        private const val KEY_LEVEL = "level"
        private const val KEY_HAS_SEEN_ONBOARDING = "has_seen_onboarding"
        private const val KEY_ACHIEVEMENTS = "user_achievements"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"

        // IDs das conquistas - espelham ACHIEVEMENTS_LIST do React
        const val ACH_FIRST_LESSON = "first_lesson"
        const val ACH_QUIZ_MASTER = "quiz_master"
        const val ACH_STREAK_3 = "streak_3"
        const val ACH_POINTS_100 = "points_100"
        const val ACH_ALL_LESSONS = "all_lessons"

        // Pontos por ação - espelham a lógica do UserProgressContext
        const val POINTS_PER_LESSON = 10
        const val POINTS_FIRST_QUIZ = 20
        const val POINTS_IMPROVED_QUIZ = 5
        const val POINTS_PER_WORDLE_WIN = 15
    }

    // ─── User Name & Bio ─────────────────────────────────────────────────────
    var userName: String
        get() = prefs.getString(KEY_USER_NAME, "Usuário") ?: "Usuário"
        set(value) = prefs.edit { putString(KEY_USER_NAME, value) }

    var userBio: String
        get() = prefs.getString(KEY_USER_BIO, "") ?: ""
        set(value) = prefs.edit { putString(KEY_USER_BIO, value) }

    var profileType: String
        get() = prefs.getString(KEY_PROFILE_TYPE, "student") ?: "student"
        set(value) = prefs.edit { putString(KEY_PROFILE_TYPE, value) }

    var avatarColorIndex: Int
        get() = prefs.getInt(KEY_AVATAR_COLOR, 0)
        set(value) = prefs.edit { putInt(KEY_AVATAR_COLOR, value) }

    // ─── Points & Level ───────────────────────────────────────────────────────
    var totalPoints: Int
        get() = prefs.getInt(KEY_TOTAL_POINTS, 0)
        set(value) {
            prefs.edit {
                putInt(KEY_TOTAL_POINTS, value)
                putInt(KEY_LEVEL, calculateLevel(value))
            }
        }

    val level: Int
        get() = prefs.getInt(KEY_LEVEL, 1)

    fun addPoints(amount: Int): Int {
        val newTotal = totalPoints + amount
        totalPoints = newTotal
        checkAndSaveAchievements()
        return newTotal
    }

    private fun calculateLevel(points: Int): Int = when {
        points >= 1500 -> 5
        points >= 1000 -> 4
        points >= 500  -> 3
        points >= 150  -> 2
        else           -> 1
    }

    // ─── Lessons Completed ────────────────────────────────────────────────────
    fun getLessonsCompleted(): Set<Int> {
        val raw = prefs.getString(KEY_LESSONS_COMPLETED, "") ?: ""
        return if (raw.isEmpty()) emptySet()
        else raw.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
    }

    fun completeLesson(lessonId: Int): Boolean {
        val current = getLessonsCompleted().toMutableSet()
        if (current.contains(lessonId)) return false // já concluída
        current.add(lessonId)
        prefs.edit { putString(KEY_LESSONS_COMPLETED, current.joinToString(",")) }
        addPoints(POINTS_PER_LESSON)
        checkAndSaveAchievements()
        return true
    }

    fun isLessonCompleted(lessonId: Int): Boolean = getLessonsCompleted().contains(lessonId)

    fun getCompletionPercentage(totalLessons: Int): Int {
        if (totalLessons == 0) return 0
        return (getLessonsCompleted().size * 100) / totalLessons
    }

    // ─── Quizzes Completed ────────────────────────────────────────────────────
    /** Returns map of quizId → bestScore */
    fun getQuizzesCompleted(): Map<Int, Int> {
        val raw = prefs.getString(KEY_QUIZZES_COMPLETED, "") ?: ""
        if (raw.isEmpty()) return emptyMap()
        return raw.split(";").mapNotNull { entry ->
            val parts = entry.split(":")
            if (parts.size == 2) parts[0].toIntOrNull()?.let { id ->
                parts[1].toIntOrNull()?.let { score -> id to score }
            } else null
        }.toMap()
    }

    fun saveQuizResult(quizId: Int, score: Int): Int {
        val current = getQuizzesCompleted().toMutableMap()
        val previousBest = current[quizId]
        var pointsToAdd = 0
        when {
            previousBest == null -> {
                current[quizId] = score
                pointsToAdd = POINTS_FIRST_QUIZ
            }
            score > previousBest -> {
                current[quizId] = score
                pointsToAdd = POINTS_IMPROVED_QUIZ
            }
        }
        val serialized = current.entries.joinToString(";") { "${it.key}:${it.value}" }
        prefs.edit { putString(KEY_QUIZZES_COMPLETED, serialized) }
        if (pointsToAdd > 0) addPoints(pointsToAdd)
        checkAndSaveAchievements()
        return pointsToAdd
    }

    fun getAverageScore(): Double {
        val quizzes = getQuizzesCompleted()
        if (quizzes.isEmpty()) return 0.0
        return quizzes.values.average()
    }

    // ─── Streak ───────────────────────────────────────────────────────────────
    var streakDays: Int
        get() = prefs.getInt(KEY_STREAK_DAYS, 0)
        private set(value) = prefs.edit { putInt(KEY_STREAK_DAYS, value) }

    fun updateStreak() {
        val lastMs = prefs.getLong(KEY_LAST_STREAK_DATE, 0L)
        val todayMs = System.currentTimeMillis()
        val diffDays = ((todayMs - lastMs) / 86_400_000L).toInt()
        val newStreak = when {
            diffDays == 0 -> streakDays        // mesmo dia, não altera
            diffDays == 1 -> streakDays + 1    // dia seguinte, incrementa
            else          -> 1                 // quebrou streak, reinicia
        }
        prefs.edit {
            putInt(KEY_STREAK_DAYS, newStreak)
            putLong(KEY_LAST_STREAK_DATE, todayMs)
        }
        checkAndSaveAchievements()
    }

    // ─── Achievements ─────────────────────────────────────────────────────────
    fun getUnlockedAchievements(): Set<String> {
        val raw = prefs.getString(KEY_ACHIEVEMENTS, "") ?: ""
        return if (raw.isEmpty()) emptySet()
        else raw.split(",").map { it.trim() }.toSet()
    }

    private fun unlockAchievement(id: String) {
        val current = getUnlockedAchievements().toMutableSet()
        if (!current.contains(id)) {
            current.add(id)
            prefs.edit { putString(KEY_ACHIEVEMENTS, current.joinToString(",")) }
        }
    }

    fun checkAndSaveAchievements(): Set<String> {
        val lessons = getLessonsCompleted()
        val quizzes = getQuizzesCompleted()
        if (lessons.size >= 1) unlockAchievement(ACH_FIRST_LESSON)
        if (lessons.size >= 10) unlockAchievement(ACH_ALL_LESSONS)
        if (quizzes.values.any { it == 100 }) unlockAchievement(ACH_QUIZ_MASTER)
        if (totalPoints >= 100) unlockAchievement(ACH_POINTS_100)
        if (streakDays >= 3) unlockAchievement(ACH_STREAK_3)
        return getUnlockedAchievements()
    }

    // ─── Onboarding ───────────────────────────────────────────────────────────
    var hasSeenOnboarding: Boolean
        get() = prefs.getBoolean(KEY_HAS_SEEN_ONBOARDING, false)
        set(value) = prefs.edit { putBoolean(KEY_HAS_SEEN_ONBOARDING, value) }

    // ─── Notifications ────────────────────────────────────────────────────────
    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_NOTIFICATIONS_ENABLED, value) }

    // ─── Clear All (equivalente ao clearAll() do React) ──────────────────────
    fun clearAll() = prefs.edit { clear() }

    // ─── Helpers ─────────────────────────────────────────────────────────────
    fun getInitials(name: String = userName): String {
        if (name.isBlank()) return "U"
        return name.trim().split(" ")
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .take(2)
            .joinToString("")
    }
}
