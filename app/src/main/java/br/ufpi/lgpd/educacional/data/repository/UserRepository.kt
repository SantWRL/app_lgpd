package br.ufpi.lgpd.educacional.data.repository

import br.ufpi.lgpd.educacional.data.dao.UserDao
import br.ufpi.lgpd.educacional.data.model.LessonProgress
import br.ufpi.lgpd.educacional.data.model.QuizResultRecord
import br.ufpi.lgpd.educacional.data.model.User
import br.ufpi.lgpd.educacional.data.model.UserStats
import br.ufpi.lgpd.educacional.util.LevelConstants
import br.ufpi.lgpd.educacional.util.PointsConstants
import br.ufpi.lgpd.educacional.util.StreakConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar

/**
 * Repositório responsável por toda a lógica de persistência do usuário,
 * progresso de aulas e resultados de quizzes via Room.
 */
class UserRepository(private val dao: UserDao) {

    companion object {
        const val DEFAULT_USER_ID = "default_user"
        const val LESSON_COMPLETION_POINTS = PointsConstants.LESSON_COMPLETION
        const val POINTS_FIRST_QUIZ = PointsConstants.FIRST_QUIZ_ATTEMPT
        const val POINTS_IMPROVED_QUIZ = PointsConstants.QUIZ_SCORE_IMPROVEMENT
        const val POINTS_PER_WORDLE_WIN = PointsConstants.WORDLE_WIN
        const val POINTS_PER_WORDSEARCH_WIN = PointsConstants.WORDSEARCH_WIN
    }

    // ─── Fluxos observáveis ────────────────────────────────────────────────────

    fun observeUser(userId: String = DEFAULT_USER_ID): Flow<User?> =
        dao.observeUser(userId)

    fun observeQuizResults(userId: String = DEFAULT_USER_ID): Flow<List<QuizResultRecord>> =
        dao.observeQuizResults(userId)

    fun observeLessonProgress(userId: String = DEFAULT_USER_ID): Flow<List<LessonProgress>> =
        dao.observeLessonProgress(userId)

    // ─── Operações do usuário ──────────────────────────────────────────────────

    /** Garante que o usuário padrão exista no banco. */
    suspend fun ensureUserExists(userId: String = DEFAULT_USER_ID) {
        if (dao.getUser(userId) == null) {
            dao.upsertUser(User(id = userId))
        }
    }

    suspend fun getUser(userId: String = DEFAULT_USER_ID): User? =
        dao.getUser(userId)

    suspend fun updateUserName(name: String, userId: String = DEFAULT_USER_ID) {
        val user = dao.getUser(userId) ?: User(id = userId)
        dao.upsertUser(user.copy(name = name.trim().ifBlank { "Usuário" }))
    }

    suspend fun updateUserBio(bio: String, userId: String = DEFAULT_USER_ID) {
        val user = dao.getUser(userId) ?: User(id = userId)
        dao.upsertUser(user.copy(bio = bio.trim()))
    }

    suspend fun updateAvatarColor(colorIndex: Int, userId: String = DEFAULT_USER_ID) {
        val user = dao.getUser(userId) ?: User(id = userId)
        dao.upsertUser(user.copy(avatarColorIndex = colorIndex))
    }

    suspend fun updateProfileType(type: String, userId: String = DEFAULT_USER_ID) {
        val user = dao.getUser(userId) ?: User(id = userId)
        dao.upsertUser(user.copy(profileType = type))
    }

    // ─── Streak ───────────────────────────────────────────────────────────────

    /**
     * Atualiza o streak diário do usuário.
     * Incrementa se o último acesso foi ontem; reinicia se foi há mais de 2 dias.
     */
    suspend fun updateStreak(userId: String = DEFAULT_USER_ID) {
        val user = dao.getUser(userId) ?: User(id = userId)
        val today = startOfDay(System.currentTimeMillis())
        val lastDate = startOfDay(user.lastStreakDate)

        val newStreak = when {
            lastDate == 0L -> 1                                    // primeiro acesso
            today - lastDate == StreakConstants.MS_PER_DAY -> user.streakDays + 1  // ontem
            today == lastDate -> user.streakDays                   // mesmo dia
            else -> 1                                              // quebrou o streak
        }

        dao.upsertUser(
            user.copy(
                streakDays = newStreak,
                lastStreakDate = today,
                lastAccessDate = System.currentTimeMillis()
            )
        )
    }

    // ─── Progresso de Aulas ───────────────────────────────────────────────────

    suspend fun markLessonCompleted(lessonId: Int, userId: String = DEFAULT_USER_ID) {
        val existing = dao.getLessonProgress(userId, lessonId)
        if (existing?.isCompleted == true) return

        val progress = existing?.copy(isCompleted = true, progress = 100, completedAt = System.currentTimeMillis())
            ?: LessonProgress(
                userId = userId,
                lessonId = lessonId,
                isCompleted = true,
                progress = 100,
                completedAt = System.currentTimeMillis()
            )
        dao.upsertLessonProgress(progress)

        // Concede pontos pela aula
        val user = dao.getUser(userId) ?: User(id = userId)
        val completed = dao.countCompletedLessons(userId)
        dao.upsertUser(
            user.copy(
                lessonsCompleted = completed,
                totalPoints = user.totalPoints + LESSON_COMPLETION_POINTS,
                level = calculateLevel(user.totalPoints + LESSON_COMPLETION_POINTS)
            )
        )
    }

    suspend fun getCompletedLessonIds(userId: String = DEFAULT_USER_ID): Set<Int> =
        dao.getCompletedLessonIds(userId).toSet()

    suspend fun isLessonCompleted(lessonId: Int, userId: String = DEFAULT_USER_ID): Boolean =
        dao.getLessonProgress(userId, lessonId)?.isCompleted == true

    // ─── Resultados de Quiz ───────────────────────────────────────────────────

    suspend fun saveQuizResult(quizId: Int, score: Int, userId: String = DEFAULT_USER_ID): Int {
        val previousBest = dao.getBestQuizScore(userId, quizId)
        val pointsEarned = when {
            previousBest == null -> POINTS_FIRST_QUIZ
            score > previousBest -> POINTS_IMPROVED_QUIZ
            else -> 0
        }
        dao.insertQuizResult(
            QuizResultRecord(
                userId = userId,
                quizId = quizId,
                score = score,
                pointsEarned = pointsEarned
            )
        )

        val avgScore = dao.getAverageScore(userId) ?: 0.0
        val quizzesCompleted = dao.countDistinctQuizzesCompleted(userId)
        val user = dao.getUser(userId) ?: User(id = userId)
        val totalPoints = user.totalPoints + pointsEarned

        dao.upsertUser(
            user.copy(
                quizzesCompleted = quizzesCompleted,
                averageScore = avgScore,
                totalPoints = totalPoints,
                level = calculateLevel(totalPoints)
            )
        )

        return pointsEarned
    }

    suspend fun addBonusPoints(points: Int, userId: String = DEFAULT_USER_ID) {
        if (points <= 0) return
        val user = dao.getUser(userId) ?: User(id = userId)
        val totalPoints = user.totalPoints + points
        dao.upsertUser(
            user.copy(
                totalPoints = totalPoints,
                level = calculateLevel(totalPoints)
            )
        )
    }

    suspend fun getUserStats(userId: String = DEFAULT_USER_ID, totalLessonsAvailable: Int = 10): UserStats {
        val user = dao.getUser(userId) ?: User(id = userId)
        val completed = dao.countCompletedLessons(userId)
        val quizResults = dao.observeQuizResults(userId).firstOrNull() ?: emptyList()
        val avgScore = if (quizResults.isEmpty()) 0.0 else quizResults.map { it.score }.average()
        val completionPct = if (totalLessonsAvailable == 0) 0.0
        else (completed.toDouble() / totalLessonsAvailable) * 100

        return UserStats(
            totalLessonsAvailable = totalLessonsAvailable,
            lessonsCompleted = completed,
            lessonsInProgress = 0,
            completionPercentage = completionPct,
            averageQuizScore = avgScore,
            totalAchievements = 0,
            streakDays = user.streakDays,
            totalPoints = user.totalPoints
        )
    }

    // ─── Auxiliares ───────────────────────────────────────────────────────────

    private fun calculateLevel(totalPoints: Int): Int = when {
        totalPoints >= LevelConstants.LEVEL_5_THRESHOLD -> 5
        totalPoints >= LevelConstants.LEVEL_4_THRESHOLD -> 4
        totalPoints >= LevelConstants.LEVEL_3_THRESHOLD -> 3
        totalPoints >= LevelConstants.LEVEL_2_THRESHOLD -> 2
        else                                            -> 1
    }

    private fun startOfDay(millis: Long): Long {
        if (millis == 0L) return 0L
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
