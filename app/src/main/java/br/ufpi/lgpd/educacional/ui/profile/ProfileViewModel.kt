package br.ufpi.lgpd.educacional.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.ufpi.lgpd.educacional.data.LgpdContent
import br.ufpi.lgpd.educacional.data.model.QuizResultRecord
import br.ufpi.lgpd.educacional.data.model.User
import br.ufpi.lgpd.educacional.data.repository.UserRepository
import br.ufpi.lgpd.educacional.util.AvatarConstants
import br.ufpi.lgpd.educacional.util.getUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository by lazy {
        application.getUserRepository()
    }

    private val avatarColors = AvatarConstants.COLORS

    private val _userProfile = MutableStateFlow(buildDefaultProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _achievements = MutableStateFlow<List<AchievementItem>>(emptyList())
    val achievements: StateFlow<List<AchievementItem>> = _achievements.asStateFlow()

    private val _lessonProgress = MutableStateFlow<List<LessonProgressItem>>(emptyList())
    val lessonProgress: StateFlow<List<LessonProgressItem>> = _lessonProgress.asStateFlow()

    private val _quizProgress = MutableStateFlow<List<QuizProgressItem>>(emptyList())
    val quizProgress: StateFlow<List<QuizProgressItem>> = _quizProgress.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureUserExists()
            loadFromDatabase()
        }
    }

    fun loadFromDatabase() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = repository.getUser() ?: User()
                val completedIds = repository.getCompletedLessonIds()
                val studyTime = LgpdContent.lessons
                    .filter { it.id in completedIds }
                    .sumOf { it.estimatedTime }
                _userProfile.value = user.toProfile(avatarColors, studyTimeMinutes = studyTime)
                _achievements.value = buildAchievements(user)
                loadLessonProgress()
                loadQuizProgress()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadLessonProgress() {
        val allProgress = repository.observeLessonProgress().firstOrNull() ?: emptyList()
        val progressByLessonId = allProgress.associateBy { it.lessonId }
        val allLessons = LgpdContent.lessons
        _lessonProgress.value = allLessons.map { lesson ->
            val progress = progressByLessonId[lesson.id]
            LessonProgressItem(
                lessonId = lesson.id,
                title = lesson.title,
                category = lesson.category,                    isCompleted = progress?.isCompleted == true,
                    completedAt = progress?.completedAt,
                    difficulty = lesson.difficulty
                )
        }.sortedBy { it.isCompleted } // pendentes primeiro
    }

    private suspend fun loadQuizProgress() {
        val allResults = repository.observeQuizResults().firstOrNull() ?: emptyList()
        // Get best score per quiz
        val bestScores = allResults.groupBy { it.quizId }.mapValues { (_, results) ->
            results.maxByOrNull { it.score }
        }
        val allQuizzes = LgpdContent.quizzes
        _quizProgress.value = allQuizzes.map { quiz ->
            val best = bestScores[quiz.id]
            QuizProgressItem(
                quizId = quiz.id,
                title = quiz.title,
                category = quiz.category,
                bestScore = best?.score,                    totalQuestions = quiz.totalQuestions,
                    isCompleted = best != null,
                    completedAt = best?.completedAt,
                    difficulty = quiz.difficulty
                )
        }.sortedBy { it.isCompleted } // pendentes primeiro
    }

    fun saveName(name: String) {
        viewModelScope.launch {
            repository.updateUserName(name)
            loadFromDatabase()
        }
    }

    fun saveBio(bio: String) {
        viewModelScope.launch {
            repository.updateUserBio(bio)
            loadFromDatabase()
        }
    }

    fun saveProfileType(type: String) {
        viewModelScope.launch {
            repository.updateProfileType(type)
            loadFromDatabase()
        }
    }

    fun saveAvatarColor(index: Int) {
        viewModelScope.launch {
            repository.updateAvatarColor(index)
            loadFromDatabase()
        }
    }

    fun clearSession() {
        viewModelScope.launch {
            // Remove todo o progresso do banco, recria o usuário padrão e recarrega
            repository.clearAllProgress()
            repository.ensureUserExists()
            loadFromDatabase()
        }
    }

    private fun User.toProfile(colors: List<String>, studyTimeMinutes: Int = 0): UserProfile = UserProfile(
        name = name,
        email = email,
        bio = bio,
        profileType = profileType,
        level = level,
        totalPoints = totalPoints,
        lessonsCompleted = lessonsCompleted,
        quizzesCompleted = quizzesCompleted,
        averageScore = averageScore,
        streakDays = streakDays,
        avatarColor = colors.getOrElse(avatarColorIndex) { colors.first() },
        avatarColorIndex = avatarColorIndex,
        totalStudyTimeMinutes = studyTimeMinutes,
        joinDate = joinDate
    )

    private fun buildDefaultProfile() = UserProfile(
        name = "Usuário",
        email = "",
        bio = "",
        profileType = "student",
        level = 1,
        totalPoints = 0,
        lessonsCompleted = 0,
        quizzesCompleted = 0,
        averageScore = 0.0,
        streakDays = 0,
        avatarColor = avatarColors.first(),
        avatarColorIndex = 0,
        totalStudyTimeMinutes = 0,
        joinDate = System.currentTimeMillis()
    )

    private fun buildAchievements(user: User): List<AchievementItem> {
        return listOf(
            AchievementItem("Primeiro passo", "Concluiu a introdução à LGPD.", user.lessonsCompleted >= 1, "\uD83C\uDFAF"),
            AchievementItem("Guardião dos dados", "Aprendeu a identificar dados pessoais e sensíveis.", user.lessonsCompleted >= 2, "\uD83D\uDEE1\uFE0F"),
            AchievementItem("Titular consciente", "Revisou os principais direitos previstos na LGPD.", user.lessonsCompleted >= 3, "\u2696\uFE0F"),
            AchievementItem("Mestre LGPD", "Complete todas as ${LgpdContent.lessons.size} aulas.", user.lessonsCompleted >= LgpdContent.lessons.size, "\uD83C\uDFC6"),
            AchievementItem("Foco Total", "Estude por 3 dias seguidos.", user.streakDays >= 3, "\uD83D\uDD25"),
            AchievementItem("Expert LGPD", "Acumulou mais de 100 pontos.", user.totalPoints >= 100, "\u2B50")
        )
    }
}

data class UserProfile(
    val name: String,
    val email: String,
    val bio: String,
    val profileType: String,
    val level: Int,
    val totalPoints: Int,
    val lessonsCompleted: Int,
    val quizzesCompleted: Int,
    val averageScore: Double,
    val streakDays: Int,
    val avatarColor: String,
    val avatarColorIndex: Int,
    val totalStudyTimeMinutes: Int = 0,
    val joinDate: Long = System.currentTimeMillis()
)

data class AchievementItem(
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val emoji: String = "\u2B50"
)

data class LessonProgressItem(
    val lessonId: Int,
    val title: String,
    val category: String,
    val isCompleted: Boolean,
    val completedAt: Long? = null,
    val difficulty: String = "BEGINNER"
)

data class QuizProgressItem(
    val quizId: Int,
    val title: String,
    val category: String,
    val bestScore: Int?,
    val totalQuestions: Int,
    val isCompleted: Boolean,
    val completedAt: Long? = null,
    val difficulty: String = "BEGINNER"
)
