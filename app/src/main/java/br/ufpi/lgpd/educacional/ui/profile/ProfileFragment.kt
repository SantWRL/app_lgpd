package br.ufpi.lgpd.educacional.ui.profile

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
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
import androidx.recyclerview.widget.LinearLayoutManager
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.databinding.FragmentProfileBinding
import br.ufpi.lgpd.educacional.ui.adapter.AchievementAdapter
import br.ufpi.lgpd.educacional.util.UserPreferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * ProfileFragment — tela de perfil, progresso e conquistas do usuário.
 */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var userPreferences: UserPreferences
    private lateinit var achievementAdapter: AchievementAdapter

    private val avatarColors = listOf(
        "#4F46E5", "#10B981", "#F59E0B", "#EF4444", "#8B5CF6", "#EC4899"
    )

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

        userPreferences = UserPreferences(requireContext())
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
    }

    private fun setupButtons() {
        binding.btnShare.setOnClickListener {
            val profile = viewModel.userProfile.value
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Estou aprendendo sobre a LGPD e já conquistei ${profile.totalPoints} pontos com uma ofensiva de ${profile.streakDays} dias! Junte-se a mim!")
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, "Compartilhar Progresso"))
        }

        binding.btnEditAvatar.setOnClickListener { showEditNameDialog() }
        binding.btnAccountData.setOnClickListener { showEditNameDialog() }

        binding.btnPrivacyPolicy.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Privacidade & LGPD")
                .setMessage("Seus dados são tratados com total segurança, seguindo as diretrizes da LGPD (Lei nº 13.709/2018).\n\n" +
                        "• Progresso salvo localmente\n" +
                        "• Sem rastreamento externo\n" +
                        "• Transparência total")
                .setPositiveButton("Entendido", null)
                .show()
        }

        binding.logoutButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Limpar Progresso")
                .setMessage("Tem certeza? Todos os seus pontos e aulas concluídas serão apagados.")
                .setPositiveButton("Sim, limpar") { _, _ ->
                    viewModel.clearSession()
                    Snackbar.make(binding.root, "Progresso limpo.", Snackbar.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun setupColorSelector() {
        val colorViews = listOf(
            binding.color0, binding.color1, binding.color2,
            binding.color3, binding.color4, binding.color5
        )
        colorViews.forEachIndexed { index, view ->
            view.setOnClickListener {
                viewModel.saveAvatarColor(index)
            }
        }
    }

    private fun showEditNameDialog() {
        val input = EditText(requireContext())
        input.setText(userPreferences.userName)
        val padding = (16 * resources.displayMetrics.density).toInt()
        input.setPadding(padding, padding, padding, padding)
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Editar Perfil")
            .setMessage("Digite seu nome de exibição:")
            .setView(input)
            .setPositiveButton("Salvar") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotBlank()) {
                    viewModel.saveName(newName)
                    Snackbar.make(binding.root, "Perfil salvo!", Snackbar.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
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
            userPoints.text = "${profile.totalPoints} pontos"
            lessonsCompleted.text = profile.lessonsCompleted.toString()
            quizzesCompleted.text = profile.quizzesCompleted.toString()
            averageScore.text = "%.1f%%".format(profile.averageScore)
            streakDays.text = profile.streakDays.toString()
            avatarInitials.text = userPreferences.getInitials(profile.name)
            
            // Cor do avatar
            try {
                val color = Color.parseColor(profile.avatarColor)
                avatarColorContainer.backgroundTintList = ColorStateList.valueOf(color)
            } catch (_: Exception) { }

            // Destaca a cor selecionada
            highlightSelectedColor(profile.avatarColorIndex)

            // Barra de progresso de aulas
            val pct = ((profile.lessonsCompleted / 10.0) * 100).toInt().coerceIn(0, 100)
            lessonsProgressBar.progress = pct
            lessonsProgressText.text = "${profile.lessonsCompleted}/10 aulas"
        }
    }

    private fun highlightSelectedColor(selectedIndex: Int) {
        val colorViews = listOf(
            binding.color0, binding.color1, binding.color2,
            binding.color3, binding.color4, binding.color5
        )
        colorViews.forEachIndexed { index, view ->
            view.alpha = if (index == selectedIndex) 1f else 0.4f
            view.scaleX = if (index == selectedIndex) 1.25f else 1f
            view.scaleY = if (index == selectedIndex) 1.25f else 1f
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
