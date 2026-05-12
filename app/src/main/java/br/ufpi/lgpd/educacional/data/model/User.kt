package br.ufpi.lgpd.educacional.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Modelo de dados para o Usuário
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String = "default_user",
    val name: String = "Usuário",
    val email: String = "",
    val bio: String = "",
    val profileType: String = "student", // student, teacher, technician
    val avatarColorIndex: Int = 0,       // índice de cor do avatar (0-5)
    val totalPoints: Int = 0,
    val level: Int = 1,
    val lessonsCompleted: Int = 0,
    val quizzesCompleted: Int = 0,
    val averageScore: Double = 0.0,
    val streakDays: Int = 0,
    val lastStreakDate: Long = 0L,
    val joinDate: Long = System.currentTimeMillis(),
    val lastAccessDate: Long = System.currentTimeMillis()
)

/**
 * Progresso do usuário em uma lição
 */
@Entity(tableName = "lesson_progress")
data class LessonProgress(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val lessonId: Int,
    val isCompleted: Boolean = false,
    val progress: Int = 0, // 0-100
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

/**
 * Conquista do usuário
 */
@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val criteria: String
)

/**
 * Conquista desbloqueada pelo usuário
 */
@Entity(tableName = "user_achievements")
data class UserAchievement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val achievementId: String,
    val unlockedAt: Long = System.currentTimeMillis()
)

/**
 * Resultado de quiz salvo no banco
 */
@Entity(tableName = "quiz_results")
data class QuizResultRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val quizId: Int,
    val score: Int,          // percentual 0-100
    val pointsEarned: Int,
    val completedAt: Long = System.currentTimeMillis()
)

/**
 * Estatísticas gerais do usuário
 */
data class UserStats(
    val totalLessonsAvailable: Int,
    val lessonsCompleted: Int,
    val lessonsInProgress: Int,
    val completionPercentage: Double,
    val averageQuizScore: Double,
    val totalAchievements: Int,
    val streakDays: Int,
    val totalPoints: Int
)
