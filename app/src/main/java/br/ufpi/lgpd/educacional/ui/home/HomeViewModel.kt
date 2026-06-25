package br.ufpi.lgpd.educacional.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.ufpi.lgpd.educacional.data.LgpdContent
import br.ufpi.lgpd.educacional.data.model.Lesson
import br.ufpi.lgpd.educacional.data.model.Quiz
import br.ufpi.lgpd.educacional.data.repository.UserRepository
import br.ufpi.lgpd.educacional.util.getUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para a tela inicial.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository by lazy {
        application.getUserRepository()
    }

    private val _lessons = MutableStateFlow<List<Lesson>>(emptyList())
    val lessons: StateFlow<List<Lesson>> = _lessons.asStateFlow()
    private var allLessons: List<Lesson> = emptyList()

    private val _quizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val quizzes: StateFlow<List<Quiz>> = _quizzes.asStateFlow()

    private val _userProgress = MutableStateFlow(
        UserProgressStats("Usuário", 0, 0, 0, 0, 1, 0)
    )
    val userProgress: StateFlow<UserProgressStats> = _userProgress.asStateFlow()

    private val _selectedLesson = MutableStateFlow<Lesson?>(null)
    val selectedLesson: StateFlow<Lesson?> = _selectedLesson.asStateFlow()

    private val _selectedQuiz = MutableStateFlow<Quiz?>(null)
    val selectedQuiz: StateFlow<Quiz?> = _selectedQuiz.asStateFlow()

    fun filterByCategory(category: String) {
        _lessons.value = if (category == "Todos") allLessons
        else allLessons.filter { it.category == category }
    }

    fun loadQuizzes() {
        viewModelScope.launch {
            _quizzes.value = LgpdContent.quizzes
        }
    }

    fun refreshContent() {
        viewModelScope.launch {
            repository.ensureUserExists()
            val completedIds = repository.getCompletedLessonIds()
            val totalLessons = LgpdContent.lessons.size
            val lessons = LgpdContent.lessons.map { lesson ->
                lesson.copy(isCompleted = completedIds.contains(lesson.id))
            }
            allLessons = lessons
            _lessons.value = lessons

            val completedQuizIds = repository.getCompletedQuizIds()
            _quizzes.value = LgpdContent.quizzes.map { quiz ->
                quiz.copy(isCompleted = completedQuizIds.contains(quiz.id))
            }
            val user = repository.getUser()

            val highestQuizScore = repository.getHighestQuizScore() ?: 0
            _userProgress.value = UserProgressStats(
                userName = user?.name ?: "Usuário",
                lessonsCompleted = completedIds.size,
                totalLessons = totalLessons,
                completionPercentage = if (totalLessons == 0) 0 else (completedIds.size * 100) / totalLessons,
                totalPoints = user?.totalPoints ?: 0,
                currentLevel = user?.level ?: 1,
                bestQuizScore = highestQuizScore
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
    val userName: String,
    val lessonsCompleted: Int,
    val totalLessons: Int,
    val completionPercentage: Int,
    val totalPoints: Int,
    val currentLevel: Int,
    val bestQuizScore: Int = 0
)
