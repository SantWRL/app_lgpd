package br.ufpi.lgpd.educacional.ui.quizzes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.core.text.HtmlCompat
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.data.LgpdContent
import br.ufpi.lgpd.educacional.data.QuizQuestionContent
import br.ufpi.lgpd.educacional.data.database.AppDatabase
import br.ufpi.lgpd.educacional.data.repository.UserRepository
import br.ufpi.lgpd.educacional.databinding.FragmentQuizDetailBinding
import br.ufpi.lgpd.educacional.util.UserPreferences
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.google.android.material.snackbar.Snackbar

class QuizDetailFragment : Fragment() {

    private var _binding: FragmentQuizDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var userPreferences: UserPreferences
    private lateinit var repository: UserRepository
    private lateinit var questions: List<QuizQuestionContent>

    private var quizId: Int = 1
    private var currentQuestionIndex = 0
    private var correctAnswers = 0
    private var hasAnsweredCurrentQuestion = false

    private val optionButtons: List<RadioButton>
        get() = listOf(binding.optionA, binding.optionB, binding.optionC, binding.optionD)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreferences = UserPreferences(requireContext())
        val db = AppDatabase.getInstance(requireContext())
        repository = UserRepository(db.userDao())
        
        quizId = arguments?.getInt(ARG_QUIZ_ID) ?: 1
        questions = LgpdContent.questionsForQuiz(quizId)

        binding.quizTitle.text = LgpdContent.quizzes.firstOrNull { it.id == quizId }?.title ?: "Teste LGPD"
        binding.primaryActionButton.setOnClickListener {
            if (binding.resultCard.visibility == View.VISIBLE) {
                findNavController().popBackStack()
            } else if (hasAnsweredCurrentQuestion) {
                goToNextQuestion()
            } else {
                submitAnswer()
            }
        }

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        showQuestion()
    }

    private fun showQuestion() {
        val question = questions[currentQuestionIndex]
        hasAnsweredCurrentQuestion = false

        binding.questionCounter.text = getString(
            R.string.quiz_question,
            currentQuestionIndex + 1,
            questions.size
        )
        binding.quizProgress.max = questions.size
        binding.quizProgress.progress = currentQuestionIndex + 1
        
        // Formatar Markdown simples
        val questionHtml = question.question
            .replace(Regex("\\*\\*(.*?)\\*\\*"), "<b>$1</b>")
            .replace("\n", "<br>")
        binding.questionText.text = HtmlCompat.fromHtml(questionHtml, HtmlCompat.FROM_HTML_MODE_COMPACT)
        
        binding.explanationCard.visibility = View.GONE
        binding.optionsGroup.clearCheck()
        binding.primaryActionButton.text = getString(R.string.button_submit)

        optionButtons.forEachIndexed { index, radioButton ->
            radioButton.text = question.options[index]
            radioButton.isEnabled = true
        }

        // Reset Duo Style
        binding.bottomActionArea.setBackgroundColor(requireContext().getColor(android.R.color.transparent))
        binding.primaryActionButton.setBackgroundResource(R.drawable.bg_button_duo_green)
    }

    private fun submitAnswer() {
        val selectedIndex = optionButtons.indexOfFirst { it.id == binding.optionsGroup.checkedRadioButtonId }
        if (selectedIndex == -1) {
            Snackbar.make(binding.root, "Escolha uma alternativa para continuar.", Snackbar.LENGTH_SHORT).show()
            return
        }

        val question = questions[currentQuestionIndex]
        val isCorrect = selectedIndex == question.correctAnswer
        if (isCorrect) correctAnswers++

        hasAnsweredCurrentQuestion = true
        optionButtons.forEach { it.isEnabled = false }
        
        val statusColor = requireContext().getColor(if (isCorrect) R.color.duo_green else R.color.duo_red)
        val statusBg = if (isCorrect) "#E5F9D1" else "#FFDFE0"
        
        binding.answerStatus.text = if (isCorrect) "EXCELENTE!" else "OPS, FOI QUASE..."
        binding.answerStatus.setTextColor(statusColor)
        binding.bottomActionArea.setBackgroundColor(android.graphics.Color.parseColor(statusBg))
        
        // Formatar Markdown para explicação
        val explanationHtml = question.explanation
            .replace(Regex("\\*\\*(.*?)\\*\\*"), "<b>$1</b>")
            .replace("\n", "<br>")
        binding.explanationText.text = HtmlCompat.fromHtml(explanationHtml, HtmlCompat.FROM_HTML_MODE_COMPACT)
        
        binding.explanationCard.visibility = View.VISIBLE
        binding.primaryActionButton.text = if (currentQuestionIndex == questions.lastIndex) "FINALIZAR" else "CONTINUAR"
        
        // Troca cor do botão se estiver errado
        if (!isCorrect) {
            binding.primaryActionButton.setBackgroundResource(R.drawable.bg_button_duo_red)
        } else {
            binding.primaryActionButton.setBackgroundResource(R.drawable.bg_button_duo_green)
        }
    }

    private fun goToNextQuestion() {
        if (currentQuestionIndex == questions.lastIndex) {
            showResult()
            return
        }

        currentQuestionIndex++
        showQuestion()
    }

    private fun showResult() {
        val score = ((correctAnswers.toDouble() / questions.size) * 100).toInt()
        
        // Salva no banco de dados via repositório (Fonte de Verdade)
        viewLifecycleOwner.lifecycleScope.launch {
            repository.saveQuizResult(quizId, score)
        }
        
        binding.questionCard.visibility = View.GONE
        binding.explanationCard.visibility = View.GONE
        binding.resultCard.visibility = View.VISIBLE
        binding.questionCounter.text = "Teste concluído"
        binding.quizProgress.progress = questions.size
        binding.resultScore.text = "$score%"
        binding.resultMessage.text = if (score >= PASSING_SCORE) {
            "Muito bom. Seu resultado foi salvo no perfil."
        } else {
            "Resultado salvo. Revise a trilha e tente novamente para fixar melhor."
        }
        binding.primaryActionButton.text = getString(R.string.button_back)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_QUIZ_ID = "quizId"
        private const val PASSING_SCORE = 70
    }
}
