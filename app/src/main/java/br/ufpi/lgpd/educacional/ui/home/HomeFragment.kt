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
import br.ufpi.lgpd.educacional.ui.lessons.LessonDetailFragment
import br.ufpi.lgpd.educacional.ui.quizzes.QuizDetailFragment
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var lessonAdapter: LessonCardAdapter
    private lateinit var quizAdapter: QuizCardAdapter

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
        }

        binding.quizzesRecyclerView.apply {
            adapter = quizAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

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

    private fun setupGameCards() {
        binding.cardWordle.setOnClickListener {
            findNavController().navigate(R.id.wordleFragment)
        }
        binding.cardWordsearch.setOnClickListener {
            findNavController().navigate(R.id.wordsearchFragment)
        }
    }

    private fun setupResourceCards() {
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
    }

    override fun onResume() {
        super.onResume()
        loadContent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
