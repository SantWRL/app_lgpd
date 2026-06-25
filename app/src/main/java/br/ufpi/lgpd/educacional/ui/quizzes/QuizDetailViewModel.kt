package br.ufpi.lgpd.educacional.ui.quizzes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.ufpi.lgpd.educacional.data.repository.UserRepository
import br.ufpi.lgpd.educacional.util.getUserRepository
import kotlinx.coroutines.launch

class QuizDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository by lazy {
        application.getUserRepository()
    }

    fun saveResult(quizId: Int, score: Int) {
        viewModelScope.launch {
            repository.saveQuizResult(quizId, score)
        }
    }
}
