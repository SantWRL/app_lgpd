package br.ufpi.lgpd.educacional.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.ufpi.lgpd.educacional.util.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para o perfil do usuário.
 * Agora usa UserPreferences para ler dados reais persistidos (equivalente ao
 * UserProgressContext do React Native app-lei).
 */
class ProfileViewModel : ViewModel() {

    private val _userProfile = MutableStateFlow(
        UserProfile("Usuário", "", 1, 0, 0, 0, 0.0, 0)
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _achievements = MutableStateFlow<List<AchievementItem>>(emptyList())
    val achievements: StateFlow<List<AchievementItem>> = _achievements.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Espelha ACHIEVEMENTS_LIST do UserProgressContext.tsx
    private val allAchievements = listOf(
        Triple(UserPreferences.ACH_FIRST_LESSON, "Primeiros Passos", "Concluiu sua primeira lição."),
        Triple(UserPreferences.ACH_QUIZ_MASTER, "Gênio do Quiz", "Gabaritou um teste com 100%."),
        Triple(UserPreferences.ACH_STREAK_3, "Foco Total", "Manteve uma ofensiva de 3 dias seguidos."),
        Triple(UserPreferences.ACH_POINTS_100, "Expert LGPD", "Acumulou mais de 100 pontos."),
        Triple(UserPreferences.ACH_ALL_LESSONS, "Mestre da Privacidade", "Concluiu todos os módulos do curso.")
    )

    fun loadUserProfile(
        savedName: String = "Usuário",
        lessonsCompleted: Int = 0,
        quizzesCompleted: Int = 0,
        averageScore: Double = 0.0,
        totalPoints: Int = 0,
        streakDays: Int = 0,
        unlockedAchievements: Set<String> = emptySet()
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _userProfile.value = UserProfile(
                    name = savedName.ifBlank { "Usuário" },
                    email = "aluno@ufpi.br",
                    level = calculateLevel(totalPoints),
                    totalPoints = totalPoints,
                    lessonsCompleted = lessonsCompleted,
                    quizzesCompleted = quizzesCompleted,
                    averageScore = averageScore,
                    streakDays = streakDays
                )
                // Gera lista de conquistas com estado de desbloqueio real
                _achievements.value = allAchievements.map { (id, title, desc) ->
                    AchievementItem(title, desc, unlockedAchievements.contains(id))
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(profile: UserProfile) {
        _userProfile.value = profile
    }

    private fun calculateLevel(totalPoints: Int): Int = when {
        totalPoints >= 1500 -> 5
        totalPoints >= 1000 -> 4
        totalPoints >= 500  -> 3
        totalPoints >= 150  -> 2
        else                -> 1
    }
}

data class AchievementItem(
    val title: String,
    val description: String,
    val isUnlocked: Boolean
)
