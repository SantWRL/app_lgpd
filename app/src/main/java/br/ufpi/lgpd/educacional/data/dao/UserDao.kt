package br.ufpi.lgpd.educacional.data.dao

import androidx.room.*
import br.ufpi.lgpd.educacional.data.model.LessonProgress
import br.ufpi.lgpd.educacional.data.model.QuizResultRecord
import br.ufpi.lgpd.educacional.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // ─── Usuário ───────────────────────────────────────────────────────────────

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun observeUser(userId: String): Flow<User?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUser(userId: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    // ─── Progresso de Aulas ───────────────────────────────────────────────────

    @Query("SELECT * FROM lesson_progress WHERE userId = :userId")
    fun observeLessonProgress(userId: String): Flow<List<LessonProgress>>

    @Query("SELECT * FROM lesson_progress WHERE userId = :userId AND lessonId = :lessonId LIMIT 1")
    suspend fun getLessonProgress(userId: String, lessonId: Int): LessonProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLessonProgress(progress: LessonProgress)

    @Query("SELECT COUNT(*) FROM lesson_progress WHERE userId = :userId AND isCompleted = 1")
    suspend fun countCompletedLessons(userId: String): Int

    // ─── Resultados de Quiz ───────────────────────────────────────────────────

    @Query("SELECT * FROM quiz_results WHERE userId = :userId ORDER BY completedAt DESC")
    fun observeQuizResults(userId: String): Flow<List<QuizResultRecord>>

    @Query("SELECT * FROM quiz_results WHERE userId = :userId AND quizId = :quizId ORDER BY completedAt DESC LIMIT 1")
    suspend fun getLastQuizResult(userId: String, quizId: Int): QuizResultRecord?

    @Insert
    suspend fun insertQuizResult(result: QuizResultRecord)

    @Query("SELECT AVG(score) FROM quiz_results WHERE userId = :userId")
    suspend fun getAverageScore(userId: String): Double?

    @Query("SELECT SUM(pointsEarned) FROM quiz_results WHERE userId = :userId")
    suspend fun getTotalPoints(userId: String): Int?

    @Query("SELECT COUNT(DISTINCT quizId) FROM quiz_results WHERE userId = :userId")
    suspend fun countDistinctQuizzesCompleted(userId: String): Int
}
