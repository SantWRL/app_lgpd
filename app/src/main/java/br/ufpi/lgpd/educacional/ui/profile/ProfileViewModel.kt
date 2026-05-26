package br.ufpi.lgpd.educacional.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.ufpi.lgpd.educacional.data.model.User
import br.ufpi.lgpd.educacional.data.repository.UserRepository
import br.ufpi.lgpd.educacional.util.AvatarConstants
import br.ufpi.lgpd.educacional.util.getUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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
                _userProfile.value = user.toProfile(avatarColors)
                _achievements.value = buildAchievements(user)
            } finally {
                _isLoading.value = false
            }
        }
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
            br.ufpi.lgpd.educacional.data.database.AppDatabase.getInstance(getApplication()).clearAllTables()
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
        avatarColorIndex = 0
    )

    private fun buildAchievements(user: User): List<AchievementItem> {
        return listOf(
            AchievementItem("Primeiro passo", "Concluiu a introdução à LGPD.", user.lessonsCompleted >= 1, "\uD83C\uDFAF"),
            AchievementItem("Guardião dos dados", "Aprendeu a identificar dados pessoais e sensíveis.", user.lessonsCompleted >= 2, "\uD83D\uDEE1\uFE0F"),
            AchievementItem("Titular consciente", "Revisou os principais direitos previstos na LGPD.", user.lessonsCompleted >= 3, "\u2696\uFE0F"),
            AchievementItem("Mestre LGPD", "Complete todas as 10 aulas.", user.lessonsCompleted >= 10, "\uD83C\uDFC6"),
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
    val avatarColorIndex: Int
)

data class AchievementItem(
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val emoji: String = "\u2B50"
)
