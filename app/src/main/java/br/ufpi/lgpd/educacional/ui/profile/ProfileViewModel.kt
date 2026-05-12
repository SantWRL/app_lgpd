package br.ufpi.lgpd.educacional.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.ufpi.lgpd.educacional.data.database.AppDatabase
import br.ufpi.lgpd.educacional.data.model.User
import br.ufpi.lgpd.educacional.data.repository.UserRepository
import br.ufpi.lgpd.educacional.util.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para o perfil do usuário.
 * Combina dados do Room (via UserRepository) com SharedPreferences.
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository by lazy {
        val db = AppDatabase.getInstance(application)
        UserRepository(db.userDao())
    }

    private val prefs: UserPreferences by lazy {
        UserPreferences(application)
    }

    private val _userProfile = MutableStateFlow(buildDefaultProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _achievements = MutableStateFlow<List<AchievementItem>>(emptyList())
    val achievements: StateFlow<List<AchievementItem>> = _achievements.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Espelha ACHIEVEMENTS_LIST do UserProgressContext.tsx
    private val avatarColors = listOf(
        "#4F46E5", "#10B981", "#F59E0B", "#EF4444", "#8B5CF6", "#EC4899"
    )

    init {
        viewModelScope.launch {
            repository.ensureUserExists()
            repository.updateStreak()
            loadFromDatabase()
        }
    }

    fun loadFromDatabase() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = repository.getUser() ?: User()

                // Sincroniza prefs → Room
                if (user.name == "Usuário" && prefs.userName != "Usuário") {
                    repository.updateUserName(prefs.userName)
                }
                
                val freshUser = repository.getUser() ?: user
                _userProfile.value = freshUser.toProfile(avatarColors)
                
                // Gera conquistas baseadas no progresso real
                _achievements.value = buildAchievements(freshUser)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveName(name: String) {
        viewModelScope.launch {
            prefs.userName = name
            repository.updateUserName(name)
            loadFromDatabase()
        }
    }

    fun saveBio(bio: String) {
        viewModelScope.launch {
            prefs.userBio = bio
            repository.updateUserBio(bio)
            loadFromDatabase()
        }
    }

    fun saveProfileType(type: String) {
        viewModelScope.launch {
            prefs.profileType = type
            repository.updateProfileType(type)
            loadFromDatabase()
        }
    }

    fun saveAvatarColor(index: Int) {
        viewModelScope.launch {
            prefs.avatarColorIndex = index
            repository.updateAvatarColor(index)
            loadFromDatabase()
        }
    }

    fun clearSession() {
        viewModelScope.launch {
            val db = AppDatabase.getInstance(getApplication())
            db.clearAllTables()
            prefs.clearAll()
            _userProfile.value = buildDefaultProfile()
            _achievements.value = emptyList()
        }
    }

    private fun User.toProfile(colors: List<String>): UserProfile = UserProfile(
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
        avatarColorIndex = avatarColorIndex
    )

    private fun buildDefaultProfile() = UserProfile(
        name = "Usuário", email = "", bio = "",
        profileType = "student", level = 1, totalPoints = 0,
        lessonsCompleted = 0, quizzesCompleted = 0, averageScore = 0.0,
        streakDays = 0, avatarColor = "#4F46E5", avatarColorIndex = 0
    )

    private fun buildAchievements(user: User): List<AchievementItem> {
        return listOf(
            AchievementItem(
                "Primeiro passo", "Concluiu a introdução à LGPD.",
                user.lessonsCompleted >= 1, "🎯"
            ),
            AchievementItem(
                "Guardião dos dados", "Aprendeu a identificar dados pessoais e sensíveis.",
                user.lessonsCompleted >= 2, "🛡️"
            ),
            AchievementItem(
                "Titular consciente", "Revisou os principais direitos previstos na LGPD.",
                user.lessonsCompleted >= 3, "⚖️"
            ),
            AchievementItem(
                "Mestre LGPD", "Complete todas as 10 aulas.",
                user.lessonsCompleted >= 10, "🏆"
            ),
            AchievementItem(
                "Foco Total", "Estude por 3 dias seguidos.",
                user.streakDays >= 3, "🔥"
            ),
            AchievementItem(
                "Expert LGPD", "Acumulou mais de 100 pontos.",
                user.totalPoints >= 100, "⭐"
            )
        )
    }
}

data class UserProfile(
    val name: String,
    val email: String,
    val bio: String,
    val profileType: String,   // student | teacher | technician
    val level: Int,
    val totalPoints: Int,
    val lessonsCompleted: Int,
    val quizzesCompleted: Int,
    val averageScore: Double,
    val streakDays: Int,
    val avatarColor: String,
    val avatarColorIndex: Int
)

data class AchievementItem(
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val emoji: String = "⭐"
)
