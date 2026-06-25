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
