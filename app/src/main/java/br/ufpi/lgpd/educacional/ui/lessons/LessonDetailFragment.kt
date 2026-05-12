package br.ufpi.lgpd.educacional.ui.lessons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import br.ufpi.lgpd.educacional.databinding.FragmentLessonDetailBinding
import br.ufpi.lgpd.educacional.util.UserPreferences
import kotlinx.coroutines.launch

class LessonDetailFragment : Fragment() {

    private var _binding: FragmentLessonDetailBinding? = null
    private val binding get() = _binding!!

    // Using activityViewModels to share data with LessonsFragment
    private val viewModel: LessonsViewModel by activityViewModels()
    private lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences(requireContext())

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        observeData()
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedLesson.collect { lesson ->
                    if (lesson != null) {
                        binding.lessonCategoryBadge.text = lesson.category.uppercase()
                        binding.lessonTitle.text = lesson.title
                        binding.lessonContent.text = lesson.content

                        val isCompleted = userPreferences.isLessonCompleted(lesson.id)
                        if (isCompleted) {
                            binding.btnCompleteLesson.text = "Lição Concluída"
                            binding.btnCompleteLesson.isEnabled = false
                            binding.btnCompleteLesson.alpha = 0.5f
                        } else {
                            binding.btnCompleteLesson.text = "Marcar como Concluída"
                            binding.btnCompleteLesson.isEnabled = true
                            binding.btnCompleteLesson.alpha = 1.0f
                            binding.btnCompleteLesson.setOnClickListener {
                                userPreferences.completeLesson(lesson.id)
                                Toast.makeText(requireContext(), "+10 XP! Lição concluída.", Toast.LENGTH_SHORT).show()
                                binding.btnCompleteLesson.text = "Lição Concluída"
                                binding.btnCompleteLesson.isEnabled = false
                                binding.btnCompleteLesson.alpha = 0.5f
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
