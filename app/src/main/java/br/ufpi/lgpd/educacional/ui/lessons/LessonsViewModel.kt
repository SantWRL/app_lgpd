package br.ufpi.lgpd.educacional.ui.lessons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.ufpi.lgpd.educacional.data.LgpdContent
import br.ufpi.lgpd.educacional.data.model.Lesson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para a tela de aulas.
 */
class LessonsViewModel : ViewModel() {

    private val _lessons = MutableStateFlow<List<Lesson>>(emptyList())
    val lessons: StateFlow<List<Lesson>> = _lessons.asStateFlow()

    private val _selectedLesson = MutableStateFlow<Lesson?>(null)
    val selectedLesson: StateFlow<Lesson?> = _selectedLesson.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadLessons(completedIds: Set<Int>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _lessons.value = LgpdContent.lessons.map { lesson ->
                    lesson.copy(isCompleted = completedIds.contains(lesson.id))
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectLesson(lesson: Lesson) {
        _selectedLesson.value = lesson
    }


}
