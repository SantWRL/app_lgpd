package br.ufpi.lgpd.educacional.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.databinding.ActivityMainBinding
import br.ufpi.lgpd.educacional.ui.onboarding.OnboardingActivity
import br.ufpi.lgpd.educacional.util.UserPreferences
import timber.log.Timber

/**
 * MainActivity - Tela principal do app com navegação
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        userPreferences = UserPreferences(this)
        
        // Setup Timber logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verificar se o usuário já passou pelo Onboarding
        if (!userPreferences.hasSeenOnboarding) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }

        // Atualizar ofensiva (streak) do usuário ao abrir o app
        userPreferences.updateStreak()

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Setup com bottom navigation
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        // Listener para mudança de fragmentos
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
            
            // Ocultar bottom nav em telas de jogos ou detalhes profundos
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

// Placeholder para BuildConfig
object BuildConfig {
    const val DEBUG = true
}
