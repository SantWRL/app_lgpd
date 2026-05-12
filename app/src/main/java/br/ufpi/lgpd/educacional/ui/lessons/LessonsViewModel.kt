package br.ufpi.lgpd.educacional.ui.lessons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                _lessons.value = getMockLessons().map { lesson ->
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

    private fun getMockLessons(): List<Lesson> {
        return listOf(
            Lesson(
                id = 1,
                title = "Introdução à LGPD",
                description = "Entenda objetivo, alcance e conceitos centrais da lei.",
                content = "A LGPD protege dados pessoais em meios físicos e digitais e orienta como organizações públicas e privadas devem coletar, usar, compartilhar, armazenar e eliminar informações de pessoas naturais.",
                category = "Fundamentos",
                orderIndex = 1,
                estimatedTime = 12,
                difficulty = "BEGINNER"
            ),
            Lesson(
                id = 2,
                title = "Dados pessoais e sensíveis",
                description = "Aprenda a diferenciar dado comum, sensível, anonimizado e pseudonimizado.",
                content = "Dado pessoal identifica ou pode identificar uma pessoa. Dados sensíveis exigem cuidado maior, como origem racial, saúde, biometria, religião, opinião política e vida sexual.",
                category = "Fundamentos",
                orderIndex = 2,
                estimatedTime = 18,
                difficulty = "BEGINNER"
            ),
            Lesson(
                id = 3,
                title = "10 princípios da LGPD",
                description = "Veja como finalidade, necessidade e transparência guiam qualquer tratamento.",
                content = "Os princípios funcionam como critérios de qualidade: use dados para finalidades legítimas, colete apenas o necessário, mantenha informações corretas e com segurança, e consiga demonstrar conformidade.",
                category = "Fundamentos",
                orderIndex = 3,
                estimatedTime = 22,
                difficulty = "BEGINNER"
            ),
            Lesson(
                id = 4,
                title = "Bases legais",
                description = "Conheça consentimento, obrigação legal, contrato, legítimo interesse e outras hipóteses.",
                content = "Todo tratamento precisa de uma base legal. A escolha depende da finalidade, do contexto e do tipo de dado. Consentimento não é a única opção e deve ser livre, informado e inequívoco quando utilizado.",
                category = "Conformidade",
                orderIndex = 4,
                estimatedTime = 24,
                difficulty = "INTERMEDIATE"
            ),
            Lesson(
                id = 5,
                title = "Direitos dos titulares",
                description = "Saiba como exercer acesso, correção, eliminação, portabilidade e revisão.",
                content = "Titulares podem pedir confirmação de tratamento, acesso, correção, anonimização, bloqueio, eliminação, informação sobre compartilhamento, portabilidade e revisão de decisões automatizadas.",
                category = "Direitos",
                orderIndex = 5,
                estimatedTime = 24,
                difficulty = "INTERMEDIATE"
            ),
            Lesson(
                id = 6,
                title = "Atores da LGPD",
                description = "Entenda titular, controlador, operador, encarregado e ANPD.",
                content = "O controlador decide finalidades e meios do tratamento; o operador atua em nome dele; o encarregado faz a ponte com titulares e ANPD; a ANPD orienta, fiscaliza e aplica sanções.",
                category = "Atores",
                orderIndex = 6,
                estimatedTime = 18,
                difficulty = "INTERMEDIATE"
            ),
            Lesson(
                id = 7,
                title = "Segurança e prevenção",
                description = "Aprenda medidas técnicas e administrativas para reduzir riscos.",
                content = "Boas práticas incluem controle de acesso, senhas fortes, mínimo privilégio, registro de atividades, backup, criptografia quando cabível, treinamento e descarte seguro de documentos.",
                category = "Segurança",
                orderIndex = 7,
                estimatedTime = 28,
                difficulty = "INTERMEDIATE"
            ),
            Lesson(
                id = 8,
                title = "Incidentes de segurança",
                description = "Veja como reconhecer, registrar e comunicar vazamentos.",
                content = "Um incidente pode envolver acesso indevido, perda, alteração ou divulgação não autorizada. A resposta deve avaliar risco aos titulares, conter danos, documentar decisões e comunicar quando necessário.",
                category = "Segurança",
                orderIndex = 8,
                estimatedTime = 22,
                difficulty = "ADVANCED"
            ),
            Lesson(
                id = 9,
                title = "LGPD no contexto acadêmico",
                description = "Aplique a lei em matrícula, pesquisa, extensão, eventos e sistemas universitários.",
                content = "Universidades tratam dados de alunos, servidores, participantes de pesquisa e comunidade externa. É essencial limitar acesso, informar finalidades e proteger dados sensíveis em projetos acadêmicos.",
                category = "Aplicação",
                orderIndex = 9,
                estimatedTime = 25,
                difficulty = "ADVANCED"
            ),
            Lesson(
                id = 10,
                title = "Checklist de conformidade",
                description = "Monte um plano prático para revisar processos e documentos.",
                content = "Mapeie dados, finalidades, bases legais, compartilhamentos, prazos de retenção, riscos, medidas de segurança e canais para atendimento aos titulares.",
                category = "Aplicação",
                orderIndex = 10,
                estimatedTime = 20,
                difficulty = "ADVANCED"
            )
        )
    }
}
