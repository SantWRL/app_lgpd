package br.ufpi.lgpd.educacional.ui.profile

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.content.ContextCompat
import android.widget.TextView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.databinding.FragmentProfileBinding
import br.ufpi.lgpd.educacional.ui.adapter.AchievementAdapter
import br.ufpi.lgpd.educacional.ui.adapter.LessonProgressAdapter
import br.ufpi.lgpd.educacional.data.LgpdContent
import br.ufpi.lgpd.educacional.ui.adapter.QuizProgressAdapter
import br.ufpi.lgpd.educacional.ui.lessons.LessonDetailFragment
import br.ufpi.lgpd.educacional.ui.quizzes.QuizDetailFragment
import br.ufpi.lgpd.educacional.util.AvatarConstants
import br.ufpi.lgpd.educacional.util.showEditNameDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var achievementAdapter: AchievementAdapter
    private lateinit var lessonProgressAdapter: LessonProgressAdapter
    private lateinit var quizProgressAdapter: QuizProgressAdapter

    private val avatarEmojis = AvatarConstants.EMOJIS

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAchievements()
        setupButtons()
        setupColorSelector()
        observeData()
    }

    private fun setupAchievements() {
        achievementAdapter = AchievementAdapter()
        binding.achievementsRecyclerView.apply {
            adapter = achievementAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        lessonProgressAdapter = LessonProgressAdapter { item ->
            val args = Bundle().apply {
                putInt(LessonDetailFragment.ARG_LESSON_ID, item.lessonId)
            }
            findNavController().navigate(R.id.action_profileFragment_to_lessonDetailFragment, args)
        }
        binding.lessonProgressRecyclerView.apply {
            adapter = lessonProgressAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }

        quizProgressAdapter = QuizProgressAdapter { item ->
            val args = Bundle().apply {
                putInt(QuizDetailFragment.ARG_QUIZ_ID, item.quizId)
            }
            findNavController().navigate(R.id.action_profileFragment_to_quizDetailFragment, args)
        }
        binding.quizProgressRecyclerView.apply {
            adapter = quizProgressAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
    }

    private fun setupButtons() {
        binding.btnShare.setOnClickListener {
            val profile = viewModel.userProfile.value
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Estou aprendendo sobre a LGPD e já conquistei ${profile.totalPoints} pontos com uma ofensiva de ${profile.streakDays} dias! Junte-se a mim!"
                )
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, "Compartilhar Progresso"))
        }

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }

        binding.btnEditAvatar.setOnClickListener { showEditProfileNameDialog() }
    }

    private fun setupColorSelector() {
        val colorViews = listOf(
            binding.color0, binding.color1, binding.color2, binding.color3,
            binding.color4, binding.color5, binding.color6, binding.color7
        )
        colorViews.forEachIndexed { index, view ->
            view.setOnClickListener {
                viewModel.saveAvatarColor(index)
            }
        }
    }

    private fun showEditProfileNameDialog() {
        showEditNameDialog(
            currentName = viewModel.userProfile.value.name
        ) { newName ->
            viewModel.saveName(newName)
            Snackbar.make(binding.root, "Perfil salvo!", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userProfile.collect { profile ->
                        updateUI(profile)
                    }
                }
                launch {
                    viewModel.achievements.collect { list ->
                        achievementAdapter.submitList(list)
                    }
                }
                launch {
                    viewModel.lessonProgress.collect { list ->
                        lessonProgressAdapter.submitList(list)
                    }
                }
                launch {
                    viewModel.quizProgress.collect { list ->
                        quizProgressAdapter.submitList(list)
                    }
                }
                launch {
                    viewModel.isLoading.collect { loading ->
                        binding.loadingIndicator.isVisible = loading
                    }
                }
            }
        }
    }

    private fun updateUI(profile: UserProfile) {
        binding.apply {
            userName.text = profile.name
            userLevel.text = "Nível ${profile.level}"
            userPoints.text = profile.totalPoints.toString()
            lessonsCompleted.text = profile.lessonsCompleted.toString()

            averageScore.text = "%.1f%%".format(profile.averageScore)
            streakDays.text = profile.streakDays.toString()

            // Uniformizar cor dos números para visual consistente
            try {
                val numColor = requireContext().getColor(R.color.text_primary)
                lessonsCompleted.setTextColor(numColor)
                userPoints.setTextColor(numColor)
                streakDays.setTextColor(numColor)
            } catch (_: Exception) {
            }

            val emoji = avatarEmojis.getOrElse(profile.avatarColorIndex) { avatarEmojis.first() }
            avatarInitials.text = emoji
            avatarInitials.textSize = 42f

            try {
                val color = Color.parseColor(profile.avatarColor)
                avatarColorContainer.backgroundTintList = ColorStateList.valueOf(color)
            } catch (_: Exception) {
            }

            highlightSelectedColor(profile.avatarColorIndex)

            val totalLessons = LgpdContent.lessons.size
            val pct = ((profile.lessonsCompleted / totalLessons.toDouble()) * 100).toInt().coerceIn(0, 100)
            lessonsProgressBar.progress = pct
            lessonsProgressText.text = "${profile.lessonsCompleted}/${totalLessons} aulas"

            val totalQuizzes = LgpdContent.quizzes.size
            val quizPct = ((profile.quizzesCompleted / totalQuizzes.toDouble()) * 100).toInt().coerceIn(0, 100)
            quizProgressBar.progress = quizPct
            quizProgressText.text = "${profile.quizzesCompleted}/${totalQuizzes} testes"

            // Tempo de estudo estimado (soma dos estimatedTime das aulas concluídas)
            val hours = profile.totalStudyTimeMinutes / 60
            val mins = profile.totalStudyTimeMinutes % 60
            totalStudyTime.text = when {
                profile.totalStudyTimeMinutes == 0 -> "0 min"
                hours > 0 -> "${hours}h ${mins}min"
                else -> "${mins} min"
            }

            // Data de início
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            sinceDate.text = sdf.format(java.util.Date(profile.joinDate))
        }
    }

    private fun highlightSelectedColor(selectedIndex: Int) {
        val colorViews = listOf(
            binding.color0, binding.color1, binding.color2, binding.color3,
            binding.color4, binding.color5, binding.color6, binding.color7
        )
        colorViews.forEachIndexed { index, view ->
            val isSelected = index == selectedIndex
            view.animate()
                .scaleX(if (isSelected) 1.20f else 1f)
                .scaleY(if (isSelected) 1.20f else 1f)
                .alpha(if (isSelected) 1f else 0.5f)
                .setDuration(200)
                .start()
            view.elevation = if (isSelected) 8f else 2f
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFromDatabase()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
