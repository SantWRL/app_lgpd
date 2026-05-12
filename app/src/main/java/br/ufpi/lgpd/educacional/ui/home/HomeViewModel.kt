package br.ufpi.lgpd.educacional.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.ufpi.lgpd.educacional.data.LgpdContent
import br.ufpi.lgpd.educacional.data.model.Lesson
import br.ufpi.lgpd.educacional.data.model.Quiz
import br.ufpi.lgpd.educacional.util.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para a tela inicial.
 */
class HomeViewModel : ViewModel() {

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
            val list = getMockLessons().map { lesson ->
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
            _quizzes.value = getMockQuizzes()
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
            _userProgress.value = UserProgressStats(
                lessonsCompleted = lessonsCompleted,
                totalLessons = totalLessons,
                completionPercentage = completionPercentage,
                totalPoints = totalPoints,
                currentLevel = currentLevel
            )
        }
    }

    fun selectLesson(lesson: Lesson) {
        _selectedLesson.value = lesson
    }

    fun selectQuiz(quiz: Quiz) {
        _selectedQuiz.value = quiz
    }

    private fun getMockLessons(): List<Lesson> {
        return listOf(
            Lesson(1, "Introdução à LGPD", "Entenda objetivo, alcance e conceitos centrais da lei.", "A LGPD protege dados pessoais em meios físicos e digitais e orienta como organizações públicas e privadas devem coletar, usar, compartilhar, armazenar e eliminar informações de pessoas naturais.", "Fundamentos", 1, 12, "BEGINNER"),
            Lesson(2, "Dados pessoais e sensíveis", "Aprenda a diferenciar dado comum, sensível, anonimizado e pseudonimizado.", "Dado pessoal identifica ou pode identificar uma pessoa. Dados sensíveis exigem cuidado maior, como origem racial, saúde, biometria, religião, opinião política e vida sexual.", "Fundamentos", 2, 18, "BEGINNER"),
            Lesson(3, "10 princípios da LGPD", "Veja como finalidade, necessidade e transparência guiam qualquer tratamento.", "Os princípios funcionam como critérios de qualidade: use dados para finalidades legítimas, colete apenas o necessário, mantenha informações corretas e com segurança, e consiga demonstrar conformidade.", "Fundamentos", 3, 22, "BEGINNER"),
            Lesson(4, "Bases legais", "Conheça consentimento, obrigação legal, contrato, legítimo interesse e outras hipóteses.", "Todo tratamento precisa de uma base legal. A escolha depende da finalidade, do contexto e do tipo de dado. Consentimento não é a única opção e deve ser livre, informado e inequívoco quando utilizado.", "Conformidade", 4, 24, "INTERMEDIATE"),
            Lesson(5, "Direitos dos titulares", "Saiba como exercer acesso, correção, eliminação, portabilidade e revisão.", "Titulares podem pedir confirmação de tratamento, acesso, correção, anonimização, bloqueio, eliminação, informação sobre compartilhamento, portabilidade e revisão de decisões automatizadas.", "Direitos", 5, 24, "INTERMEDIATE"),
            Lesson(6, "Atores da LGPD", "Entenda titular, controlador, operador, encarregado e ANPD.", "O controlador decide finalidades e meios do tratamento; o operador atua em nome dele; o encarregado faz a ponte com titulares e ANPD; a ANPD orienta, fiscaliza e aplica sanções.", "Atores", 6, 18, "INTERMEDIATE"),
            Lesson(7, "Segurança e prevenção", "Aprenda medidas técnicas e administrativas para reduzir riscos.", "Boas práticas incluem controle de acesso, senhas fortes, mínimo privilégio, registro de atividades, backup, criptografia quando cabível, treinamento e descarte seguro de documentos.", "Segurança", 7, 28, "INTERMEDIATE"),
            Lesson(8, "Incidentes de segurança", "Veja como reconhecer, registrar e comunicar vazamentos.", "Um incidente pode envolver acesso indevido, perda, alteração ou divulgação não autorizada. A resposta deve avaliar risco aos titulares, conter danos, documentar decisões e comunicar quando necessário.", "Segurança", 8, 22, "ADVANCED"),
            Lesson(9, "LGPD no contexto acadêmico", "Aplique a lei em matrícula, pesquisa, extensão, eventos e sistemas universitários.", "Universidades tratam dados de alunos, servidores, participantes de pesquisa e comunidade externa. É essencial limitar acesso, informar finalidades e proteger dados sensíveis em projetos acadêmicos.", "Aplicação", 9, 25, "ADVANCED"),
            Lesson(10, "Checklist de conformidade", "Monte um plano prático para revisar processos e documentos.", "Mapeie dados, finalidades, bases legais, compartilhamentos, prazos de retenção, riscos, medidas de segurança e canais para atendimento aos titulares.", "Aplicação", 10, 20, "ADVANCED")
        )
    }

    private fun getMockQuizzes(): List<Quiz> {
        return LgpdContent.quizzes
    }
}

data class UserProgressStats(
    val lessonsCompleted: Int,
    val totalLessons: Int,
    val completionPercentage: Int,
    val totalPoints: Int,
    val currentLevel: Int
)
