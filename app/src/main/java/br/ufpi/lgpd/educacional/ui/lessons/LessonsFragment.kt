package br.ufpi.lgpd.educacional.ui.lessons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.data.model.Lesson
import br.ufpi.lgpd.educacional.data.repository.UserRepository
import br.ufpi.lgpd.educacional.databinding.FragmentLessonsBinding
import br.ufpi.lgpd.educacional.ui.adapter.LessonsListAdapter
import br.ufpi.lgpd.educacional.util.getUserRepository
import androidx.recyclerview.widget.RecyclerView
import br.ufpi.lgpd.educacional.util.AnimationUtils
import kotlinx.coroutines.launch

/**
 * LessonsFragment - Tela listando todas as lições disponíveis
 */
class LessonsFragment : Fragment() {

    private var _binding: FragmentLessonsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LessonsViewModel by activityViewModels()
    private lateinit var repository: UserRepository

    private lateinit var adapter: LessonsListAdapter
    private var allLessons: List<Lesson> = emptyList()
    private var hasAnimated = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = getUserRepository()

        setupRecyclerView()
        setupFilters()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = LessonsListAdapter { lesson ->
            viewModel.selectLesson(lesson)
            val args = Bundle().apply {
                putInt(LessonDetailFragment.ARG_LESSON_ID, lesson.id)
            }
            findNavController().navigate(R.id.action_lessonsFragment_to_lessonDetailFragment, args)
        }

        binding.lessonsRecyclerView.apply {
            adapter = this@LessonsFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            setItemViewCacheSize(12)
        }
    }

    private fun setupFilters() {
        binding.filterAll.setOnClickListener { adapter.submitList(allLessons) }
        binding.filterBeginner.setOnClickListener { filterByDifficulty("BEGINNER") }
        binding.filterIntermediate.setOnClickListener { filterByDifficulty("INTERMEDIATE") }
        binding.filterAdvanced.setOnClickListener { filterByDifficulty("ADVANCED") }
    }

    private fun filterByDifficulty(difficulty: String) {
        adapter.submitList(allLessons.filter { it.difficulty == difficulty })
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.lessons.collect { lessons ->
                    allLessons = lessons
                    adapter.submitList(lessons)
                    // Stagger animation on first load
                    if (!hasAnimated && lessons.isNotEmpty()) {
                        hasAnimated = true
                        AnimationUtils.attachStaggerAnimation(binding.lessonsRecyclerView, maxItems = 10)
                    }
                }
            }
        }
    }

    private fun loadContent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.ensureUserExists()
            viewModel.loadLessons(repository.getCompletedLessonIds())
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
