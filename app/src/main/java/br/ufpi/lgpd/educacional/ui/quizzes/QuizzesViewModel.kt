package br.ufpi.lgpd.educacional.ui.quizzes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.ufpi.lgpd.educacional.data.LgpdContent
import br.ufpi.lgpd.educacional.data.model.Quiz
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para a tela de testes.
 */
class QuizzesViewModel : ViewModel() {

    private val _quizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val quizzes: StateFlow<List<Quiz>> = _quizzes.asStateFlow()

    private val _selectedQuiz = MutableStateFlow<Quiz?>(null)
    val selectedQuiz: StateFlow<Quiz?> = _selectedQuiz.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadQuizzes(completedIds: Set<Int> = emptySet()) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _quizzes.value = LgpdContent.quizzes.map { quiz ->
                    quiz.copy(isCompleted = completedIds.contains(quiz.id))
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectQuiz(quiz: Quiz) {
        _selectedQuiz.value = quiz
    }
}
