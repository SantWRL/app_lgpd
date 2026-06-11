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
import androidx.recyclerview.widget.LinearLayoutManager
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.databinding.FragmentProfileBinding
import br.ufpi.lgpd.educacional.ui.adapter.AchievementAdapter
import br.ufpi.lgpd.educacional.util.AvatarConstants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var achievementAdapter: AchievementAdapter

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

        binding.btnEditAvatar.setOnClickListener { showEditNameDialog() }
        binding.btnAccountData.setOnClickListener { showEditNameDialog() }

        // Garantir que os botões tenham o background escuro (visível no tema escuro)
        binding.btnAccountData.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_glass_card)
        binding.btnPrivacyPolicy.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_glass_card)

            binding.btnPrivacyPolicy.setOnClickListener {
                val builder = MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Privacidade & LGPD")
                    .setMessage(
                        "Seus dados são tratados com total segurança, seguindo as diretrizes da LGPD (Lei nº 13.709/2018).\n\n" +
                            "- Progresso salvo localmente\n" +
                            "- Sem rastreamento externo\n" +
                            "- Transparência total"
                    )
                    .setPositiveButton("Entendido", null)

                val dialog = builder.create()
                dialog.setOnShowListener {
                    try {
                        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_glass_card)
                        val titleView = dialog.findViewById<TextView>(com.google.android.material.R.id.alertTitle)
                        val msgView = dialog.findViewById<TextView>(android.R.id.message)
                        titleView?.setTextColor(requireContext().getColor(R.color.text_primary))
                        msgView?.setTextColor(requireContext().getColor(R.color.text_tertiary))
                        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                            ?.setTextColor(requireContext().getColor(R.color.primary))
                    } catch (_: Exception) {
                    }
                }
                dialog.show()
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
            binding.color0, binding.color1, binding.color2, binding.color3,
            binding.color4, binding.color5, binding.color6, binding.color7
        )
        colorViews.forEachIndexed { index, view ->
            view.setOnClickListener {
                viewModel.saveAvatarColor(index)
            }
        }
    }

    private fun showEditNameDialog() {
        val input = EditText(requireContext())
        input.setText(viewModel.userProfile.value.name)
        val padding = (16 * resources.displayMetrics.density).toInt()
        input.setPadding(padding, padding, padding, padding)
        // Estilizar input para tema escuro: texto claro e fundo transparente
        try {
            input.setTextColor(requireContext().getColor(R.color.text_primary))
            input.setHintTextColor(requireContext().getColor(R.color.text_tertiary))
            input.setBackgroundColor(Color.TRANSPARENT)
        } catch (_: Exception) {
        }

            val builder = MaterialAlertDialogBuilder(requireContext())
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

            val dialog = builder.create()
            dialog.setOnShowListener {
                try {
                    dialog.window?.setBackgroundDrawableResource(R.drawable.bg_glass_card)
                    val titleView = dialog.findViewById<TextView>(com.google.android.material.R.id.alertTitle)
                    val msgView = dialog.findViewById<TextView>(android.R.id.message)
                    titleView?.setTextColor(requireContext().getColor(R.color.text_primary))
                    msgView?.setTextColor(requireContext().getColor(R.color.text_tertiary))

                    // Botões (texto branco/primário)
                    dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                        ?.setTextColor(requireContext().getColor(R.color.primary))
                    dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
                        ?.setTextColor(requireContext().getColor(R.color.text_tertiary))
                } catch (_: Exception) {
                }
            }
            dialog.show()
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
            userPoints.text = profile.totalPoints.toString()
            lessonsCompleted.text = profile.lessonsCompleted.toString()
            quizzesCompleted.text = profile.quizzesCompleted.toString()
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

            val pct = ((profile.lessonsCompleted / 10.0) * 100).toInt().coerceIn(0, 100)
            lessonsProgressBar.progress = pct
            lessonsProgressText.text = "${profile.lessonsCompleted}/10 aulas"
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
