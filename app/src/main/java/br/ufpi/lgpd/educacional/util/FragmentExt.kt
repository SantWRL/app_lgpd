package br.ufpi.lgpd.educacional.util

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.data.database.AppDatabase
import br.ufpi.lgpd.educacional.data.repository.UserRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

/**
 * Extensoes utilitarias para telas e listas.
 */
fun Context.getUserRepository(): UserRepository {
    val db = AppDatabase.getInstance(applicationContext)
    return UserRepository(db.userDao())
}

fun Fragment.getUserRepository(): UserRepository = requireContext().getUserRepository()

fun RecyclerView.setupHorizontalList(adapter: RecyclerView.Adapter<*>) {
    this.adapter = adapter
    this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
}

fun Fragment.showInfoDialog(
    title: String,
    message: String,
    positiveText: String = "Entendido",
    onPositive: () -> Unit = {}
) {
    MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveText) { _, _ -> onPositive() }
        .show()
}

fun Fragment.showConfirmDialog(
    title: String,
    message: String,
    positiveText: String = "Sim",
    negativeText: String = "Cancelar",
    onPositive: () -> Unit = {},
    onNegative: () -> Unit = {}
) {
    MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveText) { _, _ -> onPositive() }
        .setNegativeButton(negativeText) { _, _ -> onNegative() }
        .show()
}

fun Fragment.shareText(title: String, text: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    startActivity(Intent.createChooser(sendIntent, title))
}

fun Fragment.launchSafely(block: suspend () -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        block()
    }
}

fun Fragment.showEditNameDialog(
    currentName: String,
    message: String = "Digite seu nome de exibição:",
    onSave: (String) -> Unit
) {
    val input = android.widget.EditText(requireContext())
    input.setText(currentName)
    val padding = (16 * resources.displayMetrics.density).toInt()
    input.setPadding(padding, padding, padding, padding)
    try {
        input.setTextColor(requireContext().getColor(R.color.text_primary))
        input.setHintTextColor(requireContext().getColor(R.color.text_tertiary))
        input.setBackgroundColor(android.graphics.Color.TRANSPARENT)
    } catch (_: Exception) {}

    val builder = MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
        .setTitle("Editar Perfil")
        .setMessage(message)
        .setView(input)
        .setPositiveButton("Salvar", null)
        .setNegativeButton("Cancelar", null)

    val dialog = builder.create()
    dialog.setOnShowListener {
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val newName = input.text?.toString()?.trim().orEmpty()
            if (newName.isNotBlank()) {
                onSave(newName)
                dialog.dismiss()
            } else {
                input.error = "Nome não pode ser vazio"
            }
        }
        try {
            val titleView = dialog.findViewById<android.widget.TextView>(com.google.android.material.R.id.alertTitle)
            val msgView = dialog.findViewById<android.widget.TextView>(android.R.id.message)
            titleView?.setTextColor(requireContext().getColor(R.color.text_primary))
            msgView?.setTextColor(requireContext().getColor(R.color.text_tertiary))
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                ?.setTextColor(requireContext().getColor(R.color.primary))
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
                ?.setTextColor(requireContext().getColor(R.color.text_tertiary))
        } catch (_: Exception) {}
        input.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.showSoftInput(input, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
    }
    dialog.show()
}
