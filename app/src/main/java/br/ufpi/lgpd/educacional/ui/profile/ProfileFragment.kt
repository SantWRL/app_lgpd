package br.ufpi.lgpd.educacional.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * ProfileFragment - Tela de perfil e progresso do usuário
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
        loadProfile()
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

        binding.btnEditAvatar.setOnClickListener {
            showEditProfileDialog()
        }

        binding.btnAccountData.setOnClickListener {
            showEditProfileDialog()
        }

        binding.btnPrivacyPolicy.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Privacidade & LGPD")
                .setMessage("Seus dados são tratados com total segurança, seguindo as diretrizes da LGPD (Lei nº 13.709/2018).\n\n" +
                        "• Progresso salvo localmente\n" +
                        "• Sem rastreamento externo\n" +
                        "• Transparência total")
                .setPositiveButton("Entendido", null)
                .show()
        }

        binding.logoutButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Limpar Progresso")
                .setMessage("Tem certeza? Todos os seus pontos e aulas concluídas serão apagados.")
                .setPositiveButton("Sim, limpar") { _, _ ->
                    userPreferences.clearAll()
                    loadProfile()
                    Snackbar.make(binding.root, "Progresso limpo.", Snackbar.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun setupColorSelector() {
        binding.color0.setOnClickListener { updateAvatarColor(0) }
        binding.color1.setOnClickListener { updateAvatarColor(1) }
        binding.color2.setOnClickListener { updateAvatarColor(2) }
        binding.color3.setOnClickListener { updateAvatarColor(3) }
        binding.color4.setOnClickListener { updateAvatarColor(4) }
        binding.color5.setOnClickListener { updateAvatarColor(5) }
    }

    private fun updateAvatarColor(index: Int) {
        userPreferences.avatarColorIndex = index
        binding.avatarColorContainer.backgroundTintList = ColorStateList.valueOf(Color.parseColor(avatarColors[index]))
    }

    private fun showEditProfileDialog() {
        val input = EditText(requireContext())
        input.setText(userPreferences.userName)
        input.setPadding(40, 40, 40, 40)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Editar Perfil")
            .setMessage("Digite seu nome de exibição:")
            .setView(input)
            .setPositiveButton("Salvar") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotBlank()) {
                    userPreferences.userName = newName
                    loadProfile()
                    Snackbar.make(binding.root, "Perfil salvo!", Snackbar.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userProfile.collect { profile ->
                    updateUI(profile)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.achievements.collect { achievements ->
                    achievementAdapter.submitList(achievements)
                }
            }
        }
    }

    private fun loadProfile() {
        viewModel.loadUserProfile(
            savedName = userPreferences.userName,
            lessonsCompleted = userPreferences.getLessonsCompleted().size,
            quizzesCompleted = userPreferences.getQuizzesCompleted().size,
            averageScore = userPreferences.getAverageScore(),
            totalPoints = userPreferences.totalPoints,
            streakDays = userPreferences.streakDays,
            unlockedAchievements = userPreferences.getUnlockedAchievements()
        )
    }

    private fun updateUI(profile: UserProfile) {
        binding.apply {
            userName.text = profile.name
            userLevel.text = "Nível ${profile.level}"
            lessonsCompleted.text = profile.lessonsCompleted.toString()
            quizzesCompleted.text = profile.quizzesCompleted.toString()
            streakDays.text = profile.streakDays.toString()
            avatarInitials.text = userPreferences.getInitials(profile.name)
            
            // Apply saved color
            val colorIndex = userPreferences.avatarColorIndex
            if (colorIndex in avatarColors.indices) {
                avatarColorContainer.backgroundTintList = ColorStateList.valueOf(Color.parseColor(avatarColors[colorIndex]))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if (::userPreferences.isInitialized) {
            loadProfile()
        }
    }
}

data class UserProfile(
    val name: String,
    val email: String,
    val level: Int,
    val totalPoints: Int,
    val lessonsCompleted: Int,
    val quizzesCompleted: Int,
    val averageScore: Double,
    val streakDays: Int
)
