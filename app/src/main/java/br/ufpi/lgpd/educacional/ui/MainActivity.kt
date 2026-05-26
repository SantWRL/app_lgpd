package br.ufpi.lgpd.educacional.ui

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.data.repository.UserRepository
import br.ufpi.lgpd.educacional.databinding.ActivityMainBinding
import br.ufpi.lgpd.educacional.ui.onboarding.OnboardingActivity
import br.ufpi.lgpd.educacional.util.UserPreferences
import br.ufpi.lgpd.educacional.util.getUserRepository
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * MainActivity - Tela principal do app com navegação.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var userPreferences: UserPreferences
    private lateinit var repository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userPreferences = UserPreferences(this)
        repository = getUserRepository()

        if ((applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            Timber.plant(Timber.DebugTree())
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!userPreferences.hasSeenOnboarding) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }

        lifecycleScope.launch {
            repository.ensureUserExists()
            repository.updateStreak()
        }

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbar.title = when (destination.id) {
                R.id.homeFragment -> getString(R.string.home_title)
                R.id.lessonsFragment -> "Lições"
                R.id.quizzesFragment -> "Testes"
                R.id.profileFragment -> "Perfil"
                R.id.quizDetailFragment -> "Responder Teste"
                R.id.wordleFragment -> "Termo"
                R.id.wordsearchFragment -> "Caça-Palavras"
                R.id.lessonDetailFragment -> "Estudo"
                else -> getString(R.string.app_name)
            }

            val fullScreens = listOf(
                R.id.quizDetailFragment,
                R.id.wordleFragment,
                R.id.wordsearchFragment,
                R.id.lessonDetailFragment
            )
            binding.bottomNavigation.visibility = if (destination.id in fullScreens) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
