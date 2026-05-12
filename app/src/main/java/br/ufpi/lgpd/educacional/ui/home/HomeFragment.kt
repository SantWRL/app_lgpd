package br.ufpi.lgpd.educacional.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.databinding.FragmentHomeBinding
import br.ufpi.lgpd.educacional.ui.adapter.LessonCardAdapter
import br.ufpi.lgpd.educacional.ui.adapter.QuizCardAdapter
import br.ufpi.lgpd.educacional.ui.quizzes.QuizDetailFragment
import br.ufpi.lgpd.educacional.util.UserPreferences
import kotlinx.coroutines.launch

/**
 * HomeFragment - Tela inicial do app
 * Espelha a Home do React app-lei com hero banner, categorias, cursos, gamificação e recursos.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var userPreferences: UserPreferences

    private lateinit var lessonAdapter: LessonCardAdapter
    private lateinit var quizAdapter: QuizCardAdapter

    // Category pills
    private val categoryPills get() = listOf(
        binding.catAll,
        binding.catFundamentos,
        binding.catConformidade,
        binding.catDireitos,
        binding.catAtores,
        binding.catSeguranca
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreferences = UserPreferences(requireContext())
        setupRecyclerViews()
        setupCategories()
        setupGameCards()
        setupResourceCards()
        observeData()
        loadContent()
        updateGreeting()
    }

    // ── Greeting ─────────────────────────────────────────────────────────────
    private fun updateGreeting() {
        val firstName = userPreferences.userName.split(" ").firstOrNull() ?: "Usuário"
        binding.homeGreeting.text = "Olá, $firstName!"
    }

    // ── RecyclerViews ─────────────────────────────────────────────────────────
    private fun setupRecyclerViews() {
        lessonAdapter = LessonCardAdapter { lesson ->
            viewModel.selectLesson(lesson)
            // No index.tsx do React, clicar no card de aula na home também abre a trilha ou detalhe.
        }

        quizAdapter = QuizCardAdapter { quiz ->
            viewModel.selectQuiz(quiz)
            val args = Bundle().apply {
                putInt(QuizDetailFragment.ARG_QUIZ_ID, quiz.id)
            }
            findNavController().navigate(R.id.action_homeFragment_to_quizDetailFragment, args)
        }

        binding.lessonsRecyclerView.apply {
            adapter = lessonAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        binding.quizzesRecyclerView.apply {
            adapter = quizAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    // ── Category Pill Logic ───────────────────────────────────────────────────
    private fun setupCategories() {
        val categoryMap = mapOf(
            binding.catAll to "Todos",
            binding.catFundamentos to "Fundamentos",
            binding.catConformidade to "Conformidade",
            binding.catDireitos to "Direitos",
            binding.catAtores to "Atores",
            binding.catSeguranca to "Segurança"
        )
        categoryMap.forEach { (pill, category) ->
            pill.setOnClickListener {
                setActiveCategory(pill)
                viewModel.filterByCategory(category)
            }
        }
    }

    private fun setActiveCategory(selected: TextView) {
        categoryPills.forEach { pill ->
            if (pill == selected) {
                pill.setBackgroundResource(R.drawable.badge_background)
                pill.backgroundTintList = requireContext().getColorStateList(R.color.white)
                pill.setTextColor(requireContext().getColor(R.color.primary))
            } else {
                pill.setBackgroundResource(R.drawable.bg_glass_card)
                pill.backgroundTintList = null
                pill.setTextColor(0xFFE2E8F0.toInt())
            }
        }
    }

    // ── Game Cards ────────────────────────────────────────────────────────────
    private fun setupGameCards() {
        binding.cardWordle.setOnClickListener {
            findNavController().navigate(R.id.wordleFragment)
        }
        binding.cardWordsearch.setOnClickListener {
            findNavController().navigate(R.id.wordsearchFragment)
        }
    }

    // ── Resource Cards (Portal ANPD, GDPR, etc.) ─────────────────────────────
    private fun setupResourceCards() {
        binding.cardAnpd.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gov.br/anpd/pt-br")))
            } catch (e: Exception) {
                showInfo("Portal ANPD", "Acesse: https://www.gov.br/anpd/pt-br")
            }
        }
        binding.cardGdpr.setOnClickListener {
            showInfo(
                "LGPD × GDPR",
                "Principais Diferenças:\n\n" +
                "• Bases Legais: A LGPD possui 10 bases legais para tratar dados, enquanto a GDPR possui apenas 6.\n\n" +
                "• Vazamentos: Na GDPR, notificação em até 72 horas. Na LGPD, o prazo é 3 dias úteis.\n\n" +
                "• Multas: GDPR = 20M Euros. LGPD = 50M Reais."
            )
        }
        binding.cardResumo.setOnClickListener {
            showInfo(
                "Resumo Legal",
                "A LGPD (Lei nº 13.709/2018) estabelece regras estritas sobre coleta, armazenamento, " +
                "tratamento e compartilhamento de dados pessoais em solo brasileiro. Seu principal objetivo " +
                "é proteger os direitos fundamentais de liberdade e de privacidade do cidadão."
            )
        }
        binding.cardDicionario.setOnClickListener {
            showInfo(
                "Dicionário Jurídico",
                "Titular: pessoa a quem os dados se referem.\n\n" +
                "Controlador: decide sobre o tratamento de dados.\n\n" +
                "Operador: realiza o tratamento em nome do controlador.\n\n" +
                "Encarregado (DPO): canal de comunicação entre titular, controlador e ANPD.\n\n" +
                "ANPD: Autoridade Nacional de Proteção de Dados.\n\n" +
                "Dados Sensíveis: origem racial, saúde, biometria, religião, vida sexual, etc."
            )
        }
        binding.btnVerDetalhes.setOnClickListener {
            val stats = viewModel.userProgress.value
            showInfo(
                "Seu Desempenho",
                "📚 Módulos concluídos: ${stats.lessonsCompleted}/${stats.totalLessons}\n\n" +
                "⭐ XP Total: ${stats.totalPoints} pontos\n\n" +
                "🎯 Nível: ${stats.currentLevel}\n\n" +
                "📊 Conclusão: ${stats.completionPercentage}%"
            )
        }
    }

    private fun showInfo(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Entendido", null)
            .show()
    }

    // ── Observe ViewModel Data ────────────────────────────────────────────────
    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.lessons.collect { lessons ->
                        lessonAdapter.submitList(lessons)
                    }
                }
                launch {
                    viewModel.quizzes.collect { quizzes ->
                        quizAdapter.submitList(quizzes)
                    }
                }
                launch {
                    viewModel.userProgress.collect { stats ->
                        updateProgressUI(stats)
                    }
                }
            }
        }
    }

    private fun updateProgressUI(stats: UserProgressStats) {
        binding.apply {
            progressPercentage.text = "${stats.completionPercentage}%"
            progressBar.progress = stats.completionPercentage
            homeDescription.text = "Você já completou ${stats.completionPercentage}% do conteúdo essencial."
        }
    }

    private fun loadContent() {
        val completedLessons = userPreferences.getLessonsCompleted()
        viewModel.loadLessons(completedLessons)
        viewModel.loadQuizzes()
        
        viewModel.loadUserProgress(
            lessonsCompleted = completedLessons.size,
            totalLessons = 10,
            completionPercentage = userPreferences.getCompletionPercentage(10),
            totalPoints = userPreferences.totalPoints,
            currentLevel = userPreferences.level
        )
    }

    override fun onResume() {
        super.onResume()
        if (::userPreferences.isInitialized) {
            updateGreeting()
            loadContent()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
