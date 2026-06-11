package br.ufpi.lgpd.educacional.ui.quizzes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.data.model.Quiz
import br.ufpi.lgpd.educacional.data.repository.UserRepository
import br.ufpi.lgpd.educacional.databinding.FragmentQuizzesBinding
import br.ufpi.lgpd.educacional.ui.adapter.QuizzesListAdapter
import br.ufpi.lgpd.educacional.util.getUserRepository
import kotlinx.coroutines.launch

/**
 * QuizzesFragment - Tela listando todos os quizzes disponíveis
 */
class QuizzesFragment : Fragment() {

    private var _binding: FragmentQuizzesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuizzesViewModel by viewModels()
    private lateinit var repository: UserRepository

    private lateinit var adapter: QuizzesListAdapter
    private var allQuizzes: List<Quiz> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizzesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = getUserRepository()
        setupRecyclerView()
        setupFilters()
        observeData()
        loadContent()
    }

    private fun loadContent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.ensureUserExists()
            viewModel.loadQuizzes(repository.getCompletedQuizIds())
        }
    }

    private fun setupRecyclerView() {
        adapter = QuizzesListAdapter { quiz ->
            viewModel.selectQuiz(quiz)
            val args = Bundle().apply {
                putInt(QuizDetailFragment.ARG_QUIZ_ID, quiz.id)
            }
            findNavController().navigate(R.id.action_quizzesFragment_to_quizDetailFragment, args)
        }

        binding.quizzesRecyclerView.apply {
            adapter = this@QuizzesFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupFilters() {
        binding.filterAll.setOnClickListener { adapter.submitList(allQuizzes) }
        binding.filterFundamentos.setOnClickListener { filterByCategory("Fundamentos") }
        binding.filterDireitos.setOnClickListener { filterByCategory("Direitos") }
        binding.filterSeguranca.setOnClickListener { filterByCategory("Segurança") }
    }

    private fun filterByCategory(category: String) {
        adapter.submitList(allQuizzes.filter { it.category == category })
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.quizzes.collect { quizzes ->
                    allQuizzes = quizzes
                    adapter.submitList(quizzes)
                }
            }
        }
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
