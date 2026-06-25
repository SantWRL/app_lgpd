package br.ufpi.lgpd.educacional.ui.settings

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.core.content.ContextCompat
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.data.repository.UserRepository
import br.ufpi.lgpd.educacional.databinding.FragmentSettingsBinding
import br.ufpi.lgpd.educacional.util.StudyReminderReceiver
import br.ufpi.lgpd.educacional.util.UserPreferences
import br.ufpi.lgpd.educacional.util.getUserRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private val requestPermissionLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            scheduleNotification()
        } else {
            binding.notificationSwitch.isChecked = false
            userPreferences.reminderEnabled = false
            Snackbar.make(binding.root, "Permissão necessária para lembretes.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: UserRepository
    private lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = getUserRepository()
        userPreferences = UserPreferences(requireContext())

        setupUI()
    }

    private fun setupUI() {
        // Back button
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Version
        binding.appVersion.text = getAppVersion()

        // GitHub
        binding.settingsGithub.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/SantWRL/app_lgpd"))
                startActivity(intent)
            } catch (_: Exception) {
                Snackbar.make(binding.root, "Não foi possível abrir o link.", Snackbar.LENGTH_SHORT).show()
            }
        }

        // Notifications toggle (lembrete de estudo)
        binding.notificationSwitch.isChecked = userPreferences.reminderEnabled
        binding.notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        scheduleNotification()
                    } else {
                        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                } else {
                    scheduleNotification()
                }
            } else {
                userPreferences.reminderEnabled = false
                StudyReminderReceiver.cancel(requireContext())
                Snackbar.make(binding.root, "Lembrete cancelado.", Snackbar.LENGTH_SHORT).show()
            }
        }

        // Dados da Conta (mostrar informações do perfil + editar)
        binding.settingsAccountData.setOnClickListener { showAccountDataDialog() }

        // Política de privacidade: mostrar resumo (não abrir link externo)
        binding.settingsPrivacy.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setTitle("Privacidade & LGPD")
                .setMessage(
                    "Resumo: Seus dados são tratados com segurança no dispositivo. " +
                        "O progresso é salvo localmente, não usamos rastreamento externo e não compartilhamos dados sem seu consentimento."
                )
                .setPositiveButton("Entendido", null)
                .show()
        }

        // Clear data
        binding.settingsClearData.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setTitle("Limpar Progresso")
                .setMessage("Tem certeza? Todos os seus pontos e aulas concluídas serão apagados.")
                .setPositiveButton("Sim, limpar") { _, _ ->
                    lifecycleScope.launch {
                        repository.clearAllProgress()
                        repository.ensureUserExists()
                        Snackbar.make(binding.root, "Progresso limpo.", Snackbar.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun scheduleNotification() {
        userPreferences.reminderEnabled = true
        StudyReminderReceiver.schedule(
            requireContext(),
            userPreferences.reminderHour,
            userPreferences.reminderMinute
        )
        Snackbar.make(
            binding.root,
            "Lembrete agendado para às ${userPreferences.reminderHour}:${String.format("%02d", userPreferences.reminderMinute)}",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showAccountDataDialog() {
        lifecycleScope.launch {
            val user = repository.getUser()
            val name = user?.name ?: "Usuário"
            val level = user?.level ?: 1
            val points = user?.totalPoints ?: 0

            val input = EditText(requireContext()).apply {
                setText(name)
                setTextColor(requireContext().getColor(R.color.text_primary))
                setHintTextColor(requireContext().getColor(R.color.text_tertiary))
                setBackgroundColor(Color.TRANSPARENT)
            }

            val message = "Nome atual: $name\n" +
                "Nível: $level\n" +
                "Pontos: $points\n\n" +
                "Digite um novo nome:"

            val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setTitle("Dados da Conta")
                .setMessage(message)
                .setView(input)
                .setPositiveButton("Salvar", null)
                .setNegativeButton("Cancelar", null)
                .create()

            dialog.setOnShowListener {
                dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val newName = input.text?.toString()?.trim().orEmpty()
                    if (newName.isNotBlank()) {
                        lifecycleScope.launch {
                            repository.updateUserName(newName)
                            Snackbar.make(binding.root, "Nome atualizado!", Snackbar.LENGTH_SHORT).show()
                        }
                        dialog.dismiss()
                    } else {
                        input.error = "Nome não pode ser vazio"
                    }
                }
                try {
                    val titleView = dialog.findViewById<TextView>(com.google.android.material.R.id.alertTitle)
                    val msgView = dialog.findViewById<TextView>(android.R.id.message)
                    titleView?.setTextColor(requireContext().getColor(R.color.text_primary))
                    msgView?.setTextColor(requireContext().getColor(R.color.text_tertiary))
                    dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                        ?.setTextColor(requireContext().getColor(R.color.primary))
                    dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
                        ?.setTextColor(requireContext().getColor(R.color.text_tertiary))
                } catch (_: Exception) { }
            }
            dialog.show()
            input.requestFocus()
            val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.showSoftInput(input, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun getAppVersion(): String {
        return try {
            val pkgInfo = requireContext().packageManager.getPackageInfo(
                requireContext().packageName, 0
            )
            pkgInfo.versionName ?: "1.0.0"
        } catch (_: PackageManager.NameNotFoundException) {
            "1.0.0"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
