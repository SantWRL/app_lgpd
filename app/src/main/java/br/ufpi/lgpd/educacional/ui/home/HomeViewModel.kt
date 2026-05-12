package br.ufpi.lgpd.educacional.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.ufpi.lgpd.educacional.data.LgpdContent
import br.ufpi.lgpd.educacional.data.database.AppDatabase
import br.ufpi.lgpd.educacional.data.model.Lesson
import br.ufpi.lgpd.educacional.data.model.Quiz
import br.ufpi.lgpd.educacional.data.repository.UserRepository
import br.ufpi.lgpd.educacional.util.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para a tela inicial.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository by lazy {
        UserRepository(AppDatabase.getInstance(application).userDao())
    }
    private val prefs: UserPreferences by lazy { UserPreferences(application) }

    private val _lessons = MutableStateFlow<List<Lesson>>(emptyList())
    val lessons: StateFlow<List<Lesson>> = _lessons.asStateFlow()
    private var allLessons: List<Lesson> = emptyList()

    private val _quizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val quizzes: StateFlow<List<Quiz>> = _quizzes.asStateFlow()

    private val _userProgress = MutableStateFlow(
        UserProgressStats(0, 0, 0, 0, 1)
    )
    val userProgress: StateFlow<UserProgressStats> = _userProgress.asStateFlow()

    private val _selectedLesson = MutableStateFlow<Lesson?>(null)
    val selectedLesson: StateFlow<Lesson?> = _selectedLesson.asStateFlow()

    private val _selectedQuiz = MutableStateFlow<Quiz?>(null)
    val selectedQuiz: StateFlow<Quiz?> = _selectedQuiz.asStateFlow()

    fun loadLessons(completedIds: Set<Int>) {
        viewModelScope.launch {
            val list = LgpdContent.lessons.map { lesson ->
                lesson.copy(isCompleted = completedIds.contains(lesson.id))
            }
            allLessons = list
            _lessons.value = list
        }
    }

    fun filterByCategory(category: String) {
        _lessons.value = if (category == "Todos") allLessons
        else allLessons.filter { it.category == category }
    }

    fun loadQuizzes() {
        viewModelScope.launch {
            _quizzes.value = LgpdContent.quizzes
        }
    }

    fun loadUserProgress(
        lessonsCompleted: Int,
        totalLessons: Int,
        completionPercentage: Int,
        totalPoints: Int,
        currentLevel: Int
    ) {
        viewModelScope.launch {
            repository.ensureUserExists()
            repository.updateStreak()
            val user = repository.getUser()
            
            // Prioritize database data if available, else use passed params
            val finalCompleted = user?.lessonsCompleted ?: lessonsCompleted
            val finalPoints = user?.totalPoints ?: totalPoints
            val finalLevel = user?.level ?: currentLevel
            val finalTotal = totalLessons.coerceAtLeast(10)
            val finalPct = if (finalTotal == 0) 0 else ((finalCompleted.toDouble() / finalTotal) * 100).toInt()

            _userProgress.value = UserProgressStats(
                lessonsCompleted = finalCompleted,
                totalLessons = finalTotal,
                completionPercentage = finalPct,
                totalPoints = finalPoints,
                currentLevel = finalLevel
            )
        }
    }

    fun selectLesson(lesson: Lesson) {
        _selectedLesson.value = lesson
    }

    fun selectQuiz(quiz: Quiz) {
        _selectedQuiz.value = quiz
    }
}

data class UserProgressStats(
    val lessonsCompleted: Int,
    val totalLessons: Int,
    val completionPercentage: Int,
    val totalPoints: Int,
    val currentLevel: Int
)
