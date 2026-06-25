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
import br.ufpi.lgpd.educacional.ui.feed.FeedCompactAdapter
import br.ufpi.lgpd.educacional.ui.feed.NewsScraper
import br.ufpi.lgpd.educacional.util.AnimationUtils
import br.ufpi.lgpd.educacional.util.UserPreferences
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.data.LgpdContent
import br.ufpi.lgpd.educacional.databinding.FragmentHomeBinding
import br.ufpi.lgpd.educacional.ui.adapter.LessonCardAdapter
import br.ufpi.lgpd.educacional.ui.adapter.QuizCardAdapter
import br.ufpi.lgpd.educacional.ui.lessons.LessonDetailFragment
import br.ufpi.lgpd.educacional.ui.quizzes.QuizDetailFragment
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    companion object {
        private const val HOME_NEWS_LIMIT = 4
        private const val ISO_QUIZ_ID = 10
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var lessonAdapter: LessonCardAdapter
    private lateinit var quizAdapter: QuizCardAdapter
    private lateinit var newsAdapter: FeedCompactAdapter
    private var hasAnimated = false

    private val categoryPills
        get() = listOf(
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
        setupRecyclerViews()
        setupCategories()
        setupGameCards()
        setupResourceCards()
        observeData()
        loadContent()
        updateReminderBadge()
    }

    private fun updateReminderBadge() {
        val prefs = UserPreferences(requireContext())
        binding.reminderBadge.visibility = if (prefs.reminderEnabled) View.VISIBLE else View.GONE
    }

    private fun setupRecyclerViews() {
        lessonAdapter = LessonCardAdapter { lesson ->
            viewModel.selectLesson(lesson)
            val args = Bundle().apply {
                putInt(LessonDetailFragment.ARG_LESSON_ID, lesson.id)
            }
            findNavController().navigate(R.id.action_homeFragment_to_lessonDetailFragment, args)
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
            setHasFixedSize(true)
            setItemViewCacheSize(10)
        }

        binding.quizzesRecyclerView.apply {
            adapter = quizAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            setItemViewCacheSize(10)
        }

        // news recycler (horizontal carousel)
        newsAdapter = FeedCompactAdapter(onRetryClick = { loadNews() })
        binding.homeNewsRecyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
        // Animate news section entrance (once)
        if (!hasAnimated) {
            hasAnimated = true
            AnimationUtils.slideUpFadeIn(binding.newsSection, delay = 400L)
        }

        // Abre a tela completa de notícias do app
        binding.btnVerNoticias.setOnClickListener {
            try {
                findNavController().navigate(R.id.feedFragment)
            } catch (_: Exception) {
                showInfo("Notícias", "Não foi possível abrir a tela de notícias agora.")
            }
        }
    }

    private fun setupCategories() {
        val categoryMap = mapOf(
            binding.catAll to br.ufpi.lgpd.educacional.util.CategoryConstants.ALL,
            binding.catFundamentos to br.ufpi.lgpd.educacional.util.CategoryConstants.FUNDAMENTOS,
            binding.catConformidade to br.ufpi.lgpd.educacional.util.CategoryConstants.CONFORMIDADE,
            binding.catDireitos to br.ufpi.lgpd.educacional.util.CategoryConstants.DIREITOS,
            binding.catAtores to br.ufpi.lgpd.educacional.util.CategoryConstants.ATORES,
            binding.catSeguranca to br.ufpi.lgpd.educacional.util.CategoryConstants.SEGURANCA
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
                pill.setTextColor(requireContext().getColor(R.color.text_secondary))
            }
        }
    }

    private fun setupGameCards() {
        binding.cardWordle.setOnClickListener {
            findNavController().navigate(R.id.wordleFragment)
        }
        binding.cardWordsearch.setOnClickListener {
            findNavController().navigate(R.id.wordsearchFragment)
        }
        binding.cardQuizRelampago.setOnClickListener {
            val quizzes = LgpdContent.quizzes
            val isoQuiz = quizzes.firstOrNull { it.id == ISO_QUIZ_ID }
            val targetQuiz = isoQuiz ?: quizzes.random()
            val args = Bundle().apply {
                putInt(QuizDetailFragment.ARG_QUIZ_ID, targetQuiz.id)
            }
            findNavController().navigate(R.id.action_homeFragment_to_quizDetailFragment, args)
        }
        // Botão de notificação — mostra desempenho rápido
        binding.notificationBtn.setOnClickListener {
            val stats = viewModel.userProgress.value
            showInfo(
                "Status",
                "Módulos: ${stats.lessonsCompleted}/${stats.totalLessons}\n" +
                    "XP: ${stats.totalPoints} | Nível: ${stats.currentLevel}\n" +
                    "Conclusão: ${stats.completionPercentage}%"
            )
        }
    }

    private fun setupResourceCards() {
        // Conteúdo do e-CAD foi priorizado em Aulas e Quizzes.
        binding.cardECad.visibility = View.GONE

        binding.cardAnpd.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gov.br/anpd/pt-br")))
            } catch (_: Exception) {
                showInfo("Portal ANPD", "Acesse: https://www.gov.br/anpd/pt-br")
            }
        }

        binding.cardGdpr.setOnClickListener {
            showInfo(
                "LGPD x GDPR",
                "Bases legais: a LGPD trabalha com 10 bases e a GDPR com 6.\n\n" +
                    "Incidentes: a GDPR fala em até 72 horas; na LGPD o prazo depende da regulamentação aplicável.\n\n" +
                    "Multas: os limites e critérios são diferentes entre os regimes."
            )
        }

        binding.cardResumo.setOnClickListener {
            showInfo(
                "Resumo legal",
                "A LGPD define regras para coleta, armazenamento, tratamento e compartilhamento de dados pessoais no Brasil."
            )
        }

        binding.cardDicionario.setOnClickListener {
            showInfo(
                "Dicionário jurídico",
                "Titular: pessoa a quem os dados se referem.\n\n" +
                    "Controlador: decide sobre o tratamento.\n\n" +
                    "Operador: trata os dados em nome do controlador.\n\n" +
                    "Encarregado: canal entre titular, controlador e ANPD."
            )
        }

        binding.cardECad.setOnClickListener {
            showInfo(
                "Estatuto Digital (e-CAD)",
                "Resumo: A Lei nº 15.211/2025 protege crianças e adolescentes em ambientes digitais. " +
                    "Nosso app respeita essas regras e não compartilha dados sem o seu consentimento."
            )
        }

        binding.btnVerDetalhes.setOnClickListener {
            val stats = viewModel.userProgress.value
            showInfo(
                "Seu desempenho",
                "Módulos concluídos: ${stats.lessonsCompleted}/${stats.totalLessons}\n\n" +
                    "XP total: ${stats.totalPoints}\n\n" +
                    "Nível: ${stats.currentLevel}\n\n" +
                    "Conclusão: ${stats.completionPercentage}%"
            )
        }
    }

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
                        updateGreeting(stats.userName)
                        updateProgressUI(stats)
                    }
                }
            }
        }
    }

    private fun updateGreeting(name: String) {
        val firstName = name.split(" ").firstOrNull()?.ifBlank { "Usuário" } ?: "Usuário"
        binding.homeGreeting.text = "Olá, $firstName!"
    }

    private fun updateProgressUI(stats: UserProgressStats) {
        binding.progressPercentage.text = "${stats.completionPercentage}%"
        binding.progressBar.progress = stats.completionPercentage
        binding.homeDescription.text =
            "Você já completou ${stats.completionPercentage}% do conteúdo essencial."

        // Atualizar recorde do Quiz Relâmpago
        val recorde = stats.bestQuizScore
        if (recorde > 0) {
            binding.quizRecordeBadge.text = "Recorde: ${recorde}%"
        } else {
            binding.quizRecordeBadge.text = "Recorde: --"
        }
    }

    private fun showInfo(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Entendido", null)
            .show()
    }

    private fun loadContent() {
        viewModel.refreshContent()
        loadNews()
    }

    private fun loadNews() {
        NewsScraper.fetchNews(
            onSuccess = { posts ->
                newsAdapter.submitList(posts.take(HOME_NEWS_LIMIT))
            },
            onError = { _ ->
                // Nunca ocorre com dados estáticos
            }
        )
    }

    override fun onResume() {
        super.onResume()
        updateReminderBadge()
        loadNews()
        viewModel.refreshContent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
